package com.example.ngajitime.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName

@Entity(tableName = "tabel_sesi_ngaji")
data class SesiNgaji(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tanggalSesi: Long = System.currentTimeMillis(),
    val durasiDetik: Long = 0,
    val halamanSelesai: Int = 0,

    val mulaiHalaman: Int = 0,
    val akhirHalaman: Int = 0,

    @get:PropertyName("fokus")
    val isFokus: Boolean = true,

    val catatan: String = ""
) {
    constructor() : this(0, System.currentTimeMillis(), 0, 0, 0, 0, true, "")
}