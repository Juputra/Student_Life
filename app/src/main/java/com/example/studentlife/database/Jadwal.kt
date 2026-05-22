package com.example.studentlife.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabel_jadwal")
data class Jadwal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val hari: String,
    val mata_pelajaran: String,
    val ruangan: String,
    val catatan_barang: String
)