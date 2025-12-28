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
    entities = [SesiNgaji::class, TargetUser::class, SurahProgress::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sesiDao(): SesiDao
    abstract fun targetDao(): TargetDao
    abstract fun surahDao(): SurahDao
}