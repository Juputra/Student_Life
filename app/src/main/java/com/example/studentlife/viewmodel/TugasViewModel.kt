package com.example.studentlife.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.studentlife.database.AppDatabase
import com.example.studentlife.database.Tugas
import com.example.studentlife.repository.StudentRepository
import kotlinx.coroutines.launch

class TugasViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: StudentRepository
    val listTugas = MutableLiveData<List<Tugas>>()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = StudentRepository(db)
        muatTugas()
    }

    fun muatTugas() {
        viewModelScope.launch {
            listTugas.postValue(repository.getAllTugas())
        }
    }

    // Ganti fungsi tambahTugas yang lama dengan ini:
    fun tambahTugas(tugasBaru: Tugas) {
        viewModelScope.launch {
            repository.insertTugas(tugasBaru)
            muatTugas()
        }
    }
    fun hapusTugas(tugas: Tugas) {
        viewModelScope.launch {
            repository.deleteTugas(tugas)
            muatTugas() // Muat ulang daftar tugas setelah dihapus agar layarnya update
        }
    }
}