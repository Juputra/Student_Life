package com.example.studentlife.viewmodel

import android.app.Application
import android.content.Context
import android.os.PowerManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.studentlife.database.AppDatabase
import com.example.studentlife.database.Note
import com.example.studentlife.repository.StudentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: StudentRepository
    val listNotes = MutableLiveData<List<Note>>()

    val waktuTimerTampil = MutableLiveData<String>("25:00")
    val progressTimer = MutableLiveData<Int>(100)
    val isRunning = MutableLiveData<Boolean>(false)
    val statusTimer = MutableLiveData<String>("Siap Belajar!")
    val getaranEvent = MutableLiveData<Boolean>(false)

    // Pengganti CountDownTimer yang anti-badai
    private var timerJob: Job? = null
    private var wakeLock: PowerManager.WakeLock? = null

    // 1000L = 1 detik
    private val WAKTU_FOKUS = 5 * 1000L      // Test 5 Detik Fokus
    private val WAKTU_ISTIRAHAT = 3 * 1000L  // Test 3 Detik Istirahat

    private var isModeFokus = true
    private var timeLeftInMillis = WAKTU_FOKUS
    private var initialTimeInMillis = WAKTU_FOKUS
    private var targetEndTime = 0L // Menyimpan titik waktu asli di masa depan

    init {
        val db = AppDatabase.getDatabase(application)
        repository = StudentRepository(db)
        muatSemuaNotes()
    }

    fun muatSemuaNotes() {
        viewModelScope.launch { listNotes.postValue(repository.getAllNotes()) }
    }
    fun tambahNote(note: Note) {
        viewModelScope.launch { repository.insertNote(note); muatSemuaNotes() }
    }
    fun hapusNote(note: Note) {
        viewModelScope.launch { repository.deleteNote(note); muatSemuaNotes() }
    }
    fun cariNote(keyword: String) {
        viewModelScope.launch {
            if (keyword.isEmpty()) muatSemuaNotes() else listNotes.postValue(repository.searchNotes(keyword))
        }
    }

    // --- INFINITE POMODORO BACKGROUND LOGIC ---
    fun toggleTimer() {
        if (isRunning.value == true) stopTimer() else startTimer()
    }

    private fun startTimer() {
        // 1. Tahan HP agar CPU tidak tidur lelap saat layar mati
        val powerManager = getApplication<Application>().getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "StudentLife::PomodoroWakeLock")
        wakeLock?.acquire(35 * 60 * 1000L) // Tahan maksimal 35 menit per sesi sebagai pengaman

        isRunning.postValue(true)
        statusTimer.postValue(if (isModeFokus) "🔥 Fokus Belajar!" else "☕ Waktu Istirahat!")

        // 2. Kalkulasi waktu asli di dunia nyata (Anti Doze Mode)
        targetEndTime = System.currentTimeMillis() + timeLeftInMillis

        // 3. Jalankan Coroutine Loop (Menempel pada Activity)
        timerJob = viewModelScope.launch(Dispatchers.Main) {
            while (isActive && System.currentTimeMillis() < targetEndTime) {
                // Selalu hitung sisa waktu berdasarkan waktu asli, bukan dikurangi manual
                timeLeftInMillis = targetEndTime - System.currentTimeMillis()

                updateCountDownText()
                val progress = (timeLeftInMillis.toDouble() / initialTimeInMillis.toDouble() * 100).toInt()
                progressTimer.postValue(progress)

                delay(1000) // Tunggu 1 detik
            }

            // JIKA WAKTU HABIS MENCAPAI TITIK INI:
            if (isActive) {
                wakeLock?.release() // Lepaskan penahan CPU sejenak

                getaranEvent.postValue(true) // Getarkan HP

                // Putar balik mode (Infinite Loop)
                isModeFokus = !isModeFokus
                initialTimeInMillis = if (isModeFokus) WAKTU_FOKUS else WAKTU_ISTIRAHAT
                timeLeftInMillis = initialTimeInMillis

                startTimer() // Lanjut putaran berikutnya!
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        wakeLock?.let { if (it.isHeld) it.release() }
        isRunning.postValue(false)
        statusTimer.postValue("Timer Dihentikan")
    }

    fun resetTimer() {
        stopTimer()
        isModeFokus = true
        initialTimeInMillis = WAKTU_FOKUS
        timeLeftInMillis = WAKTU_FOKUS
        updateCountDownText()
        progressTimer.postValue(100)
        statusTimer.postValue("Siap Belajar!")
    }

    private fun updateCountDownText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        waktuTimerTampil.postValue(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds))
    }

    override fun onCleared() {
        super.onCleared()
        // Fungsi ini akan dipanggil otomatis oleh Android JIKA APPLIKASI DIHAPUS DARI RECENT APPS
        stopTimer()
    }
}