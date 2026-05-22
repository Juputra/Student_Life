package com.example.studentlife.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TugasDao {

    // Tambahkan OnConflictStrategy.REPLACE di sini
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTugas(tugas: Tugas)

    @Update
    suspend fun updateTugas(tugas: Tugas)

    @Delete
    suspend fun deleteTugas(tugas: Tugas)

    // Mengurutkan berdasarkan deadline terdekat seperti yang kita bahas
    @Query("SELECT * FROM tabel_tugas ORDER BY deadline ASC")
    suspend fun getAllTugas(): List<Tugas>
}