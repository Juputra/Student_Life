package com.example.studentlife.repository

import com.example.studentlife.database.AppDatabase
import com.example.studentlife.database.Tugas
import com.example.studentlife.database.Jadwal
import com.example.studentlife.database.Note
import com.example.studentlife.database.Absensi

class StudentRepository(private val db: AppDatabase) {

    // --- FITUR TUGAS ---
    suspend fun insertTugas(tugas: Tugas) = db.tugasDao().insertTugas(tugas)
    suspend fun updateTugas(tugas: Tugas) = db.tugasDao().updateTugas(tugas)
    suspend fun deleteTugas(tugas: Tugas) = db.tugasDao().deleteTugas(tugas)
    suspend fun getAllTugas(): List<Tugas> = db.tugasDao().getAllTugas()

    // --- FITUR JADWAL ---
    suspend fun insertJadwal(jadwal: Jadwal) = db.jadwalDao().insertJadwal(jadwal)
    suspend fun updateJadwal(jadwal: Jadwal) = db.jadwalDao().insertJadwal(jadwal)
    suspend fun deleteJadwal(jadwal: Jadwal) = db.jadwalDao().deleteJadwal(jadwal)
    suspend fun getJadwalByHari(hari: String) = db.jadwalDao().getJadwalByHari(hari)

    // --- FITUR NOTE ---
    suspend fun insertNote(note: Note) = db.noteDao().insertNote(note)
    suspend fun updateNote(note: Note) = db.noteDao().updateNote(note)
    suspend fun deleteNote(note: Note) = db.noteDao().deleteNote(note)
    suspend fun getAllNotes(): List<Note> = db.noteDao().getAllNotes()
    suspend fun searchNotes(keyword: String): List<Note> = db.noteDao().searchNotes(keyword)

    // --- FITUR ABSENSI ---
    suspend fun insertAbsensi(absensi: Absensi) = db.absensiDao().insertAbsensi(absensi)
    suspend fun updateAbsensi(absensi: Absensi) = db.absensiDao().updateAbsensi(absensi)
    suspend fun deleteAbsensi(absensi: Absensi) = db.absensiDao().deleteAbsensi(absensi)
    suspend fun getAllAbsensi(): List<Absensi> = db.absensiDao().getAllAbsensi()
}
