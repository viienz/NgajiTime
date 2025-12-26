package com.example.ngajitime.data.repository

import android.util.Log
import com.example.ngajitime.data.local.dao.SesiDao
import com.example.ngajitime.data.local.dao.SurahDao
import com.example.ngajitime.data.local.dao.TargetDao
import com.example.ngajitime.data.local.entity.SesiNgaji
import com.example.ngajitime.data.local.entity.SurahProgress
import com.example.ngajitime.data.local.entity.TargetUser
import com.example.ngajitime.util.DataSurah
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NgajiRepository @Inject constructor(
    private val sesiDao: SesiDao,
    private val targetDao: TargetDao,
    private val surahDao: SurahDao,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    // =========================================================
    // 1. DATA FLOW & DASAR
    // =========================================================
    val allSurah: Flow<List<SurahProgress>> = surahDao.getAllSurah()
    val allRiwayatSesi: Flow<List<SesiNgaji>> = sesiDao.getAllSesi()

    fun getUserTarget(): Flow<TargetUser?> = targetDao.getUserTarget()

    suspend fun getUserTargetOneShot(): TargetUser? {
        return targetDao.getUserTargetOneShot()
    }

    // =========================================================
    // 2. SINKRONISASI USER (SAVE & UPDATE)
    // =========================================================

    // Simpan Target (Lokal + Cloud)
    suspend fun saveTarget(user: TargetUser) {
        targetDao.saveTarget(user)
        uploadUserToCloud(user)
    }

    // Update Streak
    suspend fun updateStreak(newStreak: Int, today: Long) {
        val currentUser = targetDao.getUserTargetOneShot()
        if (currentUser != null) {
            val updatedUser = currentUser.copy(
                currentStreak = newStreak,
                lastStreakDate = today
            )
            targetDao.saveTarget(updatedUser)
            uploadUserToCloud(updatedUser)
        }
    }

    // Update Mode Cuti
    suspend fun setStreakFreeze(isActive: Boolean) {
        val currentUser = targetDao.getUserTargetOneShot()
        if (currentUser != null) {
            val updatedUser = currentUser.copy(isStreakFreeze = isActive)
            targetDao.saveTarget(updatedUser)
            uploadUserToCloud(updatedUser)
        }
    }

    // Update Total Ayat
    suspend fun tambahTotalAyat(jumlahAyatBaru: Int) {
        val currentUser = targetDao.getUserTargetOneShot()
        if (currentUser != null) {
            val totalBaru = currentUser.totalAyatDibaca + jumlahAyatBaru
            val updatedUser = currentUser.copy(totalAyatDibaca = totalBaru)
            targetDao.saveTarget(updatedUser)
            uploadUserToCloud(updatedUser)
        }
    }

    // Alias
    suspend fun addProgressAyat(ayat: Int) = tambahTotalAyat(ayat)

    // =========================================================
    // 3. PROGRESS SURAH & SESI
    // =========================================================

    suspend fun updateProgressSurah(id: Int, ayat: Int, total: Int) {
        val isKhatam = ayat >= total
        surahDao.updateProgress(id, ayat, System.currentTimeMillis(), isKhatam)

        // Sync Cloud (Bookmark)
        val uid = auth.currentUser?.uid
        if (uid != null) {
            val dataBookmark = hashMapOf(
                "nomorSurah" to id,
                "ayatTerakhirDibaca" to ayat,
                "isKhatam" to isKhatam,
                "lastRead" to System.currentTimeMillis()
            )
            try {
                firestore.collection("users").document(uid)
                    .collection("bookmark_surah").document(id.toString())
                    .set(dataBookmark, SetOptions.merge())
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    suspend fun insertSesi(sesi: SesiNgaji) {
        sesiDao.insertSesi(sesi)
        val uid = auth.currentUser?.uid
        if (uid != null) {
            try {
                firestore.collection("users").document(uid)
                    .collection("riwayat_sesi").add(sesi)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    // Statistik Lokal
    fun getHalamanHariIni(start: Long, end: Long): Flow<Int?> = sesiDao.getTotalHalamanHariIni(start, end)
    suspend fun getRataRataKecepatan(): Long = sesiDao.getAverageSecondsPerPage() ?: 180L

    suspend fun cekDanIsiDataSurah() {
        if (surahDao.getSurahById(1) == null) surahDao.insertAll(DataSurah.list114Surah)
    }

    // =========================================================
    // 4. FIREBASE HELPER (UPLOAD, CHECK, NUKE) 🔥
    // =========================================================

    // Upload Helper
    private suspend fun uploadUserToCloud(user: TargetUser) {
        val uid = auth.currentUser?.uid ?: return
        try {
            firestore.collection("users").document(uid)
                .set(user, SetOptions.merge())
        } catch (e: Exception) { e.printStackTrace() }
    }

    // [PENTING] Cek User Lama (Dipanggil ViewModel saat Login)
    suspend fun cekUserLamaDanSimpan(uid: String): Boolean {
        return try {
            Log.d("FIREBASE_CEK", "Mulai cek data untuk UID: $uid")
            val docSnapshot = firestore.collection("users").document(uid).get().await()

            if (docSnapshot.exists()) {
                Log.d("FIREBASE_CEK", "Dokumen ditemukan!")
                val cloudUser = docSnapshot.toObject(TargetUser::class.java)

                if (cloudUser != null) {
                    Log.d("FIREBASE_CEK", "Konversi SUKSES: ${cloudUser.namaUser}")
                    // 1. Simpan Profil Utama
                    targetDao.saveTarget(cloudUser.copy(id = 1))

                    // --- [TAMBAHKAN BARIS INI] ---
                    // 2. Download Riwayat & Bookmark (Pemicu Full Sync)
                    syncDataFromCloud()
                    // -----------------------------

                    return true
                } else {
                    Log.e("FIREBASE_CEK", "Konversi GAGAL (Null).")
                }
            } else {
                Log.d("FIREBASE_CEK", "User Baru.")
            }
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // [PENTING] Hapus Data Lokal (Dipanggil saat Logout)
    suspend fun clearAllLocalData() {
        targetDao.nukeTable()
        sesiDao.nukeTable()
        surahDao.resetProgress()
    }

    fun syncDataFromCloud() {
        val uid = auth.currentUser?.uid ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ==========================================
                // 1. DOWNLOAD PROFIL USER (Target, Streak, Total)
                // ==========================================
                val docSnapshot = firestore.collection("users").document(uid).get().await()
                if (docSnapshot.exists()) {
                    val cloudUser = docSnapshot.toObject(TargetUser::class.java)
                    if (cloudUser != null) {
                        // Simpan Profil ke HP
                        targetDao.saveTarget(cloudUser.copy(id = 1))
                        Log.d("SYNC_CLOUD", "Profil sukses didownload: ${cloudUser.namaUser}")
                    }
                } else {
                    // Jika User Baru di Cloud, upload data lokal
                    val localUser = targetDao.getUserTargetOneShot()
                    if (localUser != null) uploadUserToCloud(localUser)
                    return@launch // Stop disini kalau user baru
                }

                // ==========================================
                // 2. DOWNLOAD RIWAYAT SESI (Agar Progress Harian Muncul)
                // ==========================================
                val sesiSnapshot = firestore.collection("users").document(uid)
                    .collection("riwayat_sesi").get().await()

                if (!sesiSnapshot.isEmpty) {
                    val listSesi = sesiSnapshot.toObjects(SesiNgaji::class.java)
                    for (sesi in listSesi) {
                        sesiDao.insertSesi(sesi)
                    }
                    Log.d("SYNC_CLOUD", "Riwayat Sesi sukses didownload: ${listSesi.size} data")
                }

                // ==========================================
                // 3. DOWNLOAD BOOKMARK SURAH (Agar Last Read Muncul)
                // ==========================================
                val bookmarkSnapshot = firestore.collection("users").document(uid)
                    .collection("bookmark_surah").get().await()

                if (!bookmarkSnapshot.isEmpty) {
                    for (doc in bookmarkSnapshot.documents) {
                        val nomorSurah = doc.getLong("nomorSurah")?.toInt() ?: 0
                        val ayat = doc.getLong("ayatTerakhirDibaca")?.toInt() ?: 0
                        val isKhatam = doc.getBoolean("isKhatam") ?: false
                        val lastRead = doc.getLong("lastRead") ?: 0L

                        if (nomorSurah > 0) {
                            surahDao.updateProgress(nomorSurah, ayat, lastRead, isKhatam)
                        }
                    }
                    Log.d("SYNC_CLOUD", "Bookmark sukses didownload")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("SYNC_CLOUD", "Gagal Sync: ${e.message}")
            }
        }
    }
}