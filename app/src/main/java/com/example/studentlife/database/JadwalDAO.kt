package com.example.studentlife.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface JadwalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJadwal(jadwal: Jadwal)

    @Delete
    suspend fun deleteJadwal(jadwal: Jadwal)

    // Mengambil jadwal berdasarkan hari yang dipilih, diurutkan dari jam paling pagi
    @Query("SELECT * FROM tabel_jadwal WHERE hari = :hari ORDER BY jam_mulai ASC")
    suspend fun getJadwalByHari(hari: String): List<Jadwal>
}