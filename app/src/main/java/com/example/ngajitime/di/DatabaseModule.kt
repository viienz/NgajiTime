package com.example.ngajitime.di

import android.content.Context
import androidx.room.Room
import com.example.ngajitime.data.local.AppDatabase
import com.example.ngajitime.data.local.dao.SesiDao
import com.example.ngajitime.data.local.dao.SurahDao
import com.example.ngajitime.data.local.dao.TargetDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // --- 1. PABRIK DATABASE UTAMA ---
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ngaji_database"
        )
            // INI OBAT ANTI-CRASH YANG KITA DISKUSIKAN TADI
            .fallbackToDestructiveMigration()
            .build()
    }

    // --- 2. PENYEDIA DAO (ALAT AMBIL DATA) ---

    @Provides
    fun provideTargetDao(database: AppDatabase): TargetDao {
        return database.targetDao()
    }

    @Provides
    fun provideSesiDao(database: AppDatabase): SesiDao {
        return database.sesiDao()
    }

    @Provides
    fun provideSurahDao(database: AppDatabase): SurahDao {
        return database.surahDao()
    }
}