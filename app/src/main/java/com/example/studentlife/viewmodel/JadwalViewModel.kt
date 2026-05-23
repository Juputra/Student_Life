package com.example.studentlife.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.studentlife.database.AppDatabase
import com.example.studentlife.database.Jadwal
import com.example.studentlife.repository.StudentRepository
import kotlinx.coroutines.launch

class JadwalViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: StudentRepository
    val listJadwal = MutableLiveData<List<Jadwal>>()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = StudentRepository(db)
        // Default saat pertama buka, tampilkan jadwal hari Senin
        muatJadwalHari("Senin")
    }

    fun muatJadwalHari(hari: String) {
        viewModelScope.launch {
            listJadwal.postValue(repository.getJadwalByHari(hari))
        }
    }

    fun tambahJadwal(jadwal: Jadwal) {
        viewModelScope.launch {
            repository.insertJadwal(jadwal)
            muatJadwalHari(jadwal.hari)
        }
    }

    fun updateJadwal(jadwal: Jadwal) {
        viewModelScope.launch {
            repository.updateJadwal(jadwal)
            muatJadwalHari(jadwal.hari)
        }
    }

    fun hapusJadwal(jadwal: Jadwal) {
        viewModelScope.launch {
            repository.deleteJadwal(jadwal)
            muatJadwalHari(jadwal.hari)
        }
    }
}