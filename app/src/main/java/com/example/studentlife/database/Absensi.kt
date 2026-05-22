package com.example.studentlife.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabel_absensi")
data class Absensi(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val mata_pelajaran: String,
    val hadir: Int = 0,
    val izin: Int = 0,
    val sakit: Int = 0,
    val alpa: Int = 0
)