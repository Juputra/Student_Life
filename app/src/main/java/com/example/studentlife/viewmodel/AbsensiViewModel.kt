package com.example.studentlife.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.studentlife.database.AppDatabase
import com.example.studentlife.database.Absensi
import com.example.studentlife.repository.StudentRepository
import kotlinx.coroutines.launch

class AbsensiViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: StudentRepository
    val listAbsensi = MutableLiveData<List<Absensi>>()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = StudentRepository(db)
        muatSemuaAbsensi()
    }

    fun muatSemuaAbsensi() {
        viewModelScope.launch {
            listAbsensi.postValue(repository.getAllAbsensi())
        }
    }

    fun tambahAbsensi(absensi: Absensi) {
        viewModelScope.launch {
            repository.insertAbsensi(absensi)
            muatSemuaAbsensi()
        }
    }
}