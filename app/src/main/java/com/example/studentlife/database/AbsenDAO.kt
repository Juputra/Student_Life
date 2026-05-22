package com.example.studentlife.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AbsensiDao {
    @Insert
    suspend fun insertAbsensi(absensi: Absensi)

    @Update
    suspend fun updateAbsensi(absensi: Absensi)

    @Delete
    suspend fun deleteAbsensi(absensi: Absensi)

    @Query("SELECT * FROM tabel_absensi")
    suspend fun getAllAbsensi(): List<Absensi>
}