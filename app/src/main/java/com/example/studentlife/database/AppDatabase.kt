package com.example.studentlife.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Tugas::class, Jadwal::class, Note::class, Absensi::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tugasDao(): TugasDao
    abstract fun jadwalDao(): JadwalDao
    abstract fun noteDao(): NoteDao
    abstract fun absensiDao(): AbsensiDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_life_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}