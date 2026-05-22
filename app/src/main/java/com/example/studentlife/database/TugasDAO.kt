package com.example.studentlife.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TugasDao {
    @Insert
    suspend fun insertTugas(tugas: Tugas)

    @Update
    suspend fun updateTugas(tugas: Tugas)

    @Delete
    suspend fun deleteTugas(tugas: Tugas)

    @Query("SELECT * FROM tabel_tugas ORDER BY id DESC")
    suspend fun getAllTugas(): List<Tugas>
}