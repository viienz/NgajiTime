# 📖 NgajiYuk - Your Personal Spiritual Assistant

**NgajiYuk** (sebelumnya *NgajiTime*) adalah aplikasi produktivitas spiritual yang dirancang untuk membantu umat Muslim menjaga konsistensi (istiqomah) dalam membaca Al-Qur'an. 
Berbeda dengan aplikasi Al-Qur'an biasa, NgajiYuk berfokus pada **Manajemen Target** dan **Gamifikasi**, didukung dengan teknologi **Offline-First** yang memastikan data progres tidak akan hilang meski berganti perangkat.

## ✨ Fitur Unggulan

### 1. 🎯 Smart Target Calculation
Bingung harus baca berapa ayat hari ini? Cukup tentukan tanggal target khatam, dan algoritma kami akan menghitung **beban harian** secara otomatis dan presisi.

### 2. 🔥 Gamification System
Menjaga motivasi dengan pendekatan psikologis:
* **Day Streak:** Pertahankan api semangat agar tidak padam.
* **Badges:** Dapatkan penghargaan visual untuk setiap pencapaian.

### 3. ☁️ Hybrid Offline-First Architecture
Fitur teknis andalan kami. Aplikasi menggunakan **Room Database** sebagai *Single Source of Truth* di sisi klien.
* **No Internet? No Problem.** Aplikasi tetap responsif 100% saat offline.
* **Auto Sync.** Data akan disinkronisasi ke **Firebase Firestore** secara otomatis saat koneksi kembali tersedia.

### 4. 🕌 Real-time Prayer Awareness
Header dinamis yang menyesuaikan dengan waktu sholat saat ini (Subuh, Dhuha, Maghrib, dll), mengingatkan pengguna untuk menyempatkan waktu mengaji.

---

## 🛠️ Tech Stack & Libraries

Project ini dibangun dengan standar industri Android modern:

* **Language:** [Kotlin](https://kotlinlang.org/)
* **UI Framework:** [Jetpack Compose](https://developer.android.com/jetbrains/compose) (Material Design 3)
* **Architecture:** MVVM (Model-View-ViewModel) with Repository Pattern
* **Dependency Injection:** [Hilt](https://dagger.dev/hilt/)
* **Local Database:** [Room Database](https://developer.android.com/training/data-storage/room) (SQLite)
* **Cloud Database:** [Firebase Firestore](https://firebase.google.com/docs/firestore)
* **Authentication:** Firebase Auth
* **Concurrency:** Coroutines & Flow
* **Network Monitoring:** Connectivity Manager (Live Network Observer)

---

## 🏗️ Architecture Overview

Aplikasi ini menerapkan prinsip **Offline-First**:

1.  **UI Layer:** Hanya mengamati (observe) data dari Room Database (Local). Tidak pernah meminta data langsung ke Cloud.
2.  **Repository Layer:** Mengelola logika sinkronisasi. Saat user menyimpan data, data masuk ke Room (agar UI update instan) dan dikirim ke Firebase via *Background Worker*.
3.  **Data Layer:** Terdiri dari Room (Local Source) dan Firestore (Remote Source).

---

## 🚀 Cara Menjalankan Project

1.  **Clone Repository**
    ```bash
    git clone [https://github.com/username/NgajiYuk.git](https://github.com/username/NgajiYuk.git)
    ```
2.  **Buka di Android Studio** (Disarankan versi Hedgehog atau terbaru).
3.  **Setup Firebase**
    * Buat project baru di Firebase Console.
    * Download `google-services.json`.
    * Letakkan file tersebut di folder `app/`.
4.  **Sync Gradle & Run**
    * Pastikan koneksi internet lancar untuk download dependency pertama kali.
    * Jalankan di Emulator atau Device Fisik.

---

## 👤 Author

Developed with:
* **NIM:**
  1. 23523147 - Devin Pandya Subarkah
  2. 23523250 - Agil Seno Adjie
  3. 23523258 - Muhammad Kaffa Radya Farabi
  4. 23523150 - Reza Ramadhani H
* **Universitas:** Universitas Islam Indonesia
