package com.example.studentlife.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabel_tugas")
data class Tugas(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val judul: String,
    val deadline: String,
    val isSelesai: Boolean = false
)