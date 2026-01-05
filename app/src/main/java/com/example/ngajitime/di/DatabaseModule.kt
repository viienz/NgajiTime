package com.example.ngajitime.di

import android.content.Context
import androidx.room.Room
import com.example.ngajitime.data.local.AppDatabase
import com.example.ngajitime.data.local.dao.NotifikasiDao
import com.example.ngajitime.data.local.dao.SesiDao
import com.example.ngajitime.data.local.dao.SurahDao
import com.example.ngajitime.data.local.dao.TargetDao
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.google.firebase.firestore.FirebaseFirestore

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    //untuk database utama
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ngaji_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    //untuk ambil data (dao) dari database
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

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    fun provideNotifikasiDao(database: AppDatabase): NotifikasiDao {
        return database.notifikasiDao()
    }
}
