package com.example.studentlife.database

import androidx.room.*

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    // Catatan yang di-PIN akan selalu muncul paling atas
    @Query("SELECT * FROM tabel_note ORDER BY isPinned DESC, id DESC")
    suspend fun getAllNotes(): List<Note>

    @Query("SELECT * FROM tabel_note WHERE judul LIKE '%' || :keyword || '%' OR isi LIKE '%' || :keyword || '%' ORDER BY isPinned DESC, id DESC")
    suspend fun searchNotes(keyword: String): List<Note>
}