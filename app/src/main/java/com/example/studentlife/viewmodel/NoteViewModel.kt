package com.example.studentlife.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.studentlife.database.AppDatabase
import com.example.studentlife.database.Note
import com.example.studentlife.repository.StudentRepository
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: StudentRepository
    val listNotes = MutableLiveData<List<Note>>()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = StudentRepository(db)
        muatSemuaNotes()
    }

    fun muatSemuaNotes() {
        viewModelScope.launch {
            listNotes.postValue(repository.getAllNotes())
        }
    }

    fun tambahNote(note: Note) {
        viewModelScope.launch {
            repository.insertNote(note)
            muatSemuaNotes()
        }
    }

    fun hapusNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
            muatSemuaNotes()
        }
    }
}