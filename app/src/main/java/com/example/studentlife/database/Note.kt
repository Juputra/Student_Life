package com.example.studentlife.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabel_note")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val judul: String,
    val isi_catatan: String
)