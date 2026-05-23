package com.example.studentlife.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.studentlife.database.AppDatabase
import com.example.studentlife.database.Jadwal
import com.example.studentlife.repository.StudentRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class JadwalViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: StudentRepository
    val listJadwalHariIni = MutableLiveData<List<Jadwal>>()
    val listChecklistTas = MutableLiveData<List<String>>() // Auto-Generated
    val listTugasMepet = MutableLiveData<List<String>>() // Auto-Generated
    val seragamHariIni = MutableLiveData<String>()
    private val memoriSeragam = application.getSharedPreferences("DataSeragam", android.content.Context.MODE_PRIVATE)
    private val barangDadakanPerHari = mutableMapOf<String, MutableList<String>>()
    init {
        val db = AppDatabase.getDatabase(application)
        repository = StudentRepository(db)
        val hariIni = getNamaHariIni()
        muatDataHarian(hariIni, Date()) // Default load hari ini
    }

    fun muatDataHarian(namaHari: String, tanggalDipilih: Date) {
        viewModelScope.launch {
            // 1. Tarik jadwal statis (Contoh: Jadwal kelas Kecerdasan Buatan atau Tutor SD di hari Selasa)
            val jadwal = repository.getJadwalByHari(namaHari)
            listJadwalHariIni.postValue(jadwal)

            // 2. Tarik semua tugas
            val semuaTugas = repository.getAllTugas()

            // 3. Filter tugas yang belum selesai dan deadline-nya JATUH PADA TANGGAL YANG DIPILIH
            val sdfCek = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val tanggalTargetString = sdfCek.format(tanggalDipilih)

            val tugasHariIni = semuaTugas.filter { !it.isSelesai && it.deadline.startsWith(tanggalTargetString) }
            listTugasMepet.postValue(tugasHariIni.map { "Kumpul: ${it.judul}" })

            val seragamTersimpan = memoriSeragam.getString("SERAGAM_$namaHari", "") ?: ""
            if (seragamTersimpan.isNotEmpty()) {
                seragamHariIni.postValue(seragamTersimpan)
            } else {
                seragamHariIni.postValue("Belum diatur (Ketuk untuk isi)")
            }

            // 4. GENERATE AUTO-CHECKLIST TAS SEKOLAH
            val checklistOtomatis = mutableListOf<String>()

            // --- BARANG DEFAULT WAJIB ---
            if (seragamTersimpan.isNotEmpty()) {
                checklistOtomatis.add("Pakai Seragam: $seragamTersimpan")
            }
            checklistOtomatis.add("Kotak pensil beserta isinya (Alat tulis)")

            // --- GENERATE BUKU PER MATPEL ---
            jadwal.forEach {
                val mp = it.mata_pelajaran
                checklistOtomatis.add("Buku Cetak $mp")
                checklistOtomatis.add("Buku Catatan $mp")
                checklistOtomatis.add("Buku PS $mp")
                checklistOtomatis.add("Buku PR $mp")

                // Masukkan bawaan spesifik jika ada (dipisah koma)
                if (it.bawaan_spesifik.isNotBlank()) {
                    val bawaanExtra = it.bawaan_spesifik.split(",").map { b -> b.trim() }
                    checklistOtomatis.addAll(bawaanExtra)
                }
            }

            // --- MASUKKAN TUGAS/PR ---
            tugasHariIni.forEach {
                checklistOtomatis.add("Tugas/Print: ${it.judul}")
            }

            // --- MASUKKAN BARANG DADAKAN ---
            val dadakanHariIni = barangDadakanPerHari[namaHari] ?: emptyList()
            checklistOtomatis.addAll(dadakanHariIni)

            // Hapus duplikat dan hilangkan string kosong
            listChecklistTas.postValue(checklistOtomatis.filter { it.isNotEmpty() }.distinct())
        }
    }

    fun tambahJadwal(jadwal: Jadwal, hariAktif: String, tanggalAktif: Date) {
        viewModelScope.launch {
            repository.insertJadwal(jadwal)
            muatDataHarian(hariAktif, tanggalAktif)
        }
    }

    fun hapusJadwal(jadwal: Jadwal, hariAktif: String, tanggalAktif: Date) {
        viewModelScope.launch {
            repository.deleteJadwal(jadwal)
            muatDataHarian(hariAktif, tanggalAktif)
        }
    }

    private fun getNamaHariIni(): String {
        val calendar = Calendar.getInstance()
        val days = arrayOf("Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
        return days[calendar.get(Calendar.DAY_OF_WEEK) - 1]
    }
    fun tambahBarangDadakan(namaHari: String, barang: String, tanggalAktif: Date) {
        val listDadakan = barangDadakanPerHari[namaHari] ?: mutableListOf()
        listDadakan.add(barang)
        barangDadakanPerHari[namaHari] = listDadakan

        // Refresh UI
        muatDataHarian(namaHari, tanggalAktif)
    }
    fun simpanSeragam(namaHari: String, seragamBaru: String, tanggalAktif: Date) {
        // Simpan ke memori (Create / Update) atau Hapus jika string kosong (Delete)
        memoriSeragam.edit().putString("SERAGAM_$namaHari", seragamBaru).apply()

        // Refresh layar agar UI dan Checklist Tas langsung berubah
        muatDataHarian(namaHari, tanggalAktif)
    }
}