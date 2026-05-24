package com.example.studentlife.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabel_note")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val judul: String,
    val isi: String,
    val tanggal: String,
    val kategori: String = "Umum",
    val warna_hex: String = "#FFFFFF",
    val isPinned: Boolean = false
)