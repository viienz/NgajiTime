package com.example.ngajitime.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
// Import Entity & DAO Notifikasi yang baru dibuat
import com.example.ngajitime.data.local.dao.NotifikasiDao
import com.example.ngajitime.data.local.dao.SesiDao
import com.example.ngajitime.data.local.dao.SurahDao
import com.example.ngajitime.data.local.dao.TargetDao
import com.example.ngajitime.data.local.entity.NotifikasiEntity
import com.example.ngajitime.data.local.entity.SesiNgaji
import com.example.ngajitime.data.local.entity.SurahProgress
import com.example.ngajitime.data.local.entity.TargetUser

@Database(
    entities = [
        SesiNgaji::class,
        TargetUser::class,
        SurahProgress::class,
        NotifikasiEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sesiDao(): SesiDao
    abstract fun targetDao(): TargetDao
    abstract fun surahDao(): SurahDao
    abstract fun notifikasiDao(): NotifikasiDao
}