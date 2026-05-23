package com.example.studentlife.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studentlife.R
import com.example.studentlife.database.AppDatabase
import com.example.studentlife.database.Tugas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class TugasActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private var tugasId: Int = 0
    private var isEditMode = false
    private var isSelesaiStatus = false
    private var tanggalSelesaiStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Memakai layout form tugas buatan Saverio
        setContentView(R.layout.dialog_form_tugas)

        db = AppDatabase.getDatabase(this)

        val tvTitle = findViewById<TextView>(R.id.tv_dialog_title)
        val inJudul = findViewById<EditText>(R.id.in_judul)
        val inMatpel = findViewById<EditText>(R.id.in_matpel)
        val inDeadline = findViewById<EditText>(R.id.in_deadline)
        val inDeskripsi = findViewById<EditText>(R.id.in_deskripsi)
        val inInstruksi = findViewById<EditText>(R.id.in_instruksi)
        val inFormat = findViewById<EditText>(R.id.in_format)
        val inKetentuan = findViewById<EditText>(R.id.in_ketentuan)
        val inPengajar = findViewById<EditText>(R.id.in_pengajar)
        val btnSimpan = findViewById<Button>(R.id.btn_simpan_dialog)

        // Cek apakah Activity dibuka untuk EDIT data (melewati intent extra)
        if (intent.hasExtra("EXTRA_TUGAS_ID")) {
            isEditMode = true
            tvTitle.text = "Edit Tugas Kuliah"
            tugasId = intent.getIntExtra("EXTRA_TUGAS_ID", 0)
            inJudul.setText(intent.getStringExtra("EXTRA_JUDUL"))
            inMatpel.setText(intent.getStringExtra("EXTRA_MATPEL"))
            inDeadline.setText(intent.getStringExtra("EXTRA_DEADLINE"))
            inDeskripsi.setText(intent.getStringExtra("EXTRA_DESKRIPSI"))
            inInstruksi.setText(intent.getStringExtra("EXTRA_INSTRUKSI"))
            inFormat.setText(intent.getStringExtra("EXTRA_FORMAT"))
            inKetentuan.setText(intent.getStringExtra("EXTRA_KETENTUAN"))
            inPengajar.setText(intent.getStringExtra("EXTRA_PENGAJAR"))
            isSelesaiStatus = intent.getBooleanExtra("EXTRA_ISELESAI", false)
            tanggalSelesaiStatus = intent.getStringExtra("EXTRA_TANGGAL_SELESAI")
        }

        // Pop-up Tanggal & Waktu otomatis saat kolom deadline diklik
        inDeadline.setOnClickListener {
            val kalender = Calendar.getInstance()
            DatePickerDialog(this, { _, tahun, bulan, hari ->
                TimePickerDialog(this, { _, jam, menit ->
                    val formatDeadline = String.format("%04d-%02d-%02d %02d:%02d", tahun, bulan + 1, hari, jam, menit)
                    inDeadline.setText(formatDeadline)
                }, kalender.get(Calendar.HOUR_OF_DAY), kalender.get(Calendar.MINUTE), true).show()
            }, kalender.get(Calendar.YEAR), kalender.get(Calendar.MONTH), kalender.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Proses menangkap ketikan pengguna dan mengirimkannya ke Tugas DAO buatan Mishael
        btnSimpan.setOnClickListener {
            val judul = inJudul.text.toString().trim()
            val matpel = inMatpel.text.toString().trim()
            val deadline = inDeadline.text.toString().trim()

            if (judul.isEmpty() || matpel.isEmpty() || deadline.isEmpty()) {
                Toast.makeText(this, "Judul, Matpel, dan Deadline wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dataTugas = Tugas(
                id = if (isEditMode) tugasId else 0,
                judul = judul,
                mata_pelajaran = matpel,
                deadline = deadline,
                deskripsi = inDeskripsi.text.toString().trim(),
                instruksi = inInstruksi.text.toString().trim(),
                format_pengumpulan = inFormat.text.toString().trim(),
                ketentuan = inKetentuan.text.toString().trim(),
                nama_pengajar = inPengajar.text.toString().trim(),
                isSelesai = isSelesaiStatus,
                tanggalSelesai = tanggalSelesaiStatus
            )

            // Mengirim ke DAO menggunakan Coroutine di Background Thread
            CoroutineScope(Dispatchers.IO).launch {
                db.tugasDao().insertTugas(dataTugas) // Insert Room otomatis menjadi REPLACE jika ID sama (Edit)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TugasActivity, "Tugas berhasil disimpan!", Toast.LENGTH_SHORT).show()
                    finish() // Tutup activity setelah berhasil simpan
                }
            }
        }
    }
}