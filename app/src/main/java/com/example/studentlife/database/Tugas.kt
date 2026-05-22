package com.example.studentlife.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabel_tugas")
data class Tugas(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val judul: String,
    val mata_pelajaran: String,
    val deskripsi: String,
    val instruksi: String,
    val format_pengumpulan: String,
    val deadline: String,
    val ketentuan: String,
    val nama_pengajar: String,
    val isSelesai: Boolean = false,
    val tanggalSelesai: String? = null // Kolom baru untuk mencatat waktu centang
)