package com.example.ngajitime.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ngajitime.data.local.dao.SesiDao
import com.example.ngajitime.data.local.dao.SurahDao
import com.example.ngajitime.data.local.dao.TargetDao
import com.example.ngajitime.data.local.entity.SesiNgaji
import com.example.ngajitime.data.local.entity.SurahProgress
import com.example.ngajitime.data.local.entity.TargetUser

@Database(
    entities = [SesiNgaji::class, TargetUser::class, SurahProgress::class], // <-- Tambah SurahProgress
    version = 3, // <-- WAJIB: Naikkan jadi 2 karena struktur berubah
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sesiDao(): SesiDao
    abstract fun targetDao(): TargetDao
    abstract fun surahDao(): SurahDao // <-- Tambah ini
}