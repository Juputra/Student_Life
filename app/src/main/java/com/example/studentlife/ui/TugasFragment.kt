package com.example.studentlife.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlife.R
import com.example.studentlife.adapter.TugasAdapter
import com.example.studentlife.database.Tugas
import com.example.studentlife.viewmodel.TugasViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TugasFragment : Fragment() {

    private lateinit var viewModel: TugasViewModel
    private lateinit var adapterAktif: TugasAdapter
    private lateinit var adapterSelesai: TugasAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tugas, container, false)

        val rvAktif = view.findViewById<RecyclerView>(R.id.rv_tugas_aktif)
        val rvSelesai = view.findViewById<RecyclerView>(R.id.rv_tugas_selesai)
        val tvEmptyAktif = view.findViewById<TextView>(R.id.tv_empty_aktif)
        val tvEmptySelesai = view.findViewById<TextView>(R.id.tv_empty_selesai)
        val fabTambah = view.findViewById<FloatingActionButton>(R.id.fab_tambah_tugas)

        viewModel = ViewModelProvider(this)[TugasViewModel::class.java]

        // --- ADAPTER 1: TUGAS AKTIF ---
        adapterAktif = TugasAdapter(arrayListOf(),
            onItemClick = { tampilDialogDetail(it) },
            onEditClick = { konfirmasiAksi("Edit tugas ini?", { tampilDialogForm(it) }) },
            onDeleteClick = { konfirmasiAksi("Hapus tugas ini secara permanen?", { viewModel.hapusTugas(it) }) },
            onCheckChange = { tugas, isChecked ->
                konfirmasiAksi("Tandai tugas ini sebagai selesai?", {
                    val waktuSekarang = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                    viewModel.tambahTugas(tugas.copy(isSelesai = isChecked, tanggalSelesai = waktuSekarang))
                })
            }
        )

        // --- ADAPTER 2: TUGAS SELESAI ---
        adapterSelesai = TugasAdapter(arrayListOf(),
            onItemClick = { tampilDialogDetail(it) },
            onEditClick = { konfirmasiAksi("Edit tugas lama ini?", { tampilDialogForm(it) }) },
            onDeleteClick = { konfirmasiAksi("Hapus tugas lama ini secara permanen?", { viewModel.hapusTugas(it) }) },
            onCheckChange = { tugas, isChecked ->
                konfirmasiAksi("Kembalikan tugas ini ke daftar aktif?", {
                    viewModel.tambahTugas(tugas.copy(isSelesai = isChecked, tanggalSelesai = null))
                })
            }
        )

        rvAktif.layoutManager = LinearLayoutManager(context)
        rvAktif.adapter = adapterAktif

        rvSelesai.layoutManager = LinearLayoutManager(context)
        rvSelesai.adapter = adapterSelesai

        // --- PROSES PEMISAHAN DATA & EMPTY STATE ---
        viewModel.listTugas.observe(viewLifecycleOwner) { allTugas ->
            val listAktif = allTugas.filter { !it.isSelesai }
            val listSelesai = allTugas.filter { it.isSelesai }

            adapterAktif.updateData(listAktif)
            adapterSelesai.updateData(listSelesai)

            // Atur tulisan peringatan jika kosong otomatis
            tvEmptyAktif.visibility = if (listAktif.isEmpty()) View.VISIBLE else View.GONE
            tvEmptySelesai.visibility = if (listSelesai.isEmpty()) View.VISIBLE else View.GONE
        }

        fabTambah.setOnClickListener { tampilDialogForm(null) }

        return view
    }

    // --- DIALOG POP-UP KONFIRMASI (GLOBAL) ---
    private fun konfirmasiAksi(pesan: String, aksiPositif: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi")
            .setMessage(pesan)
            .setPositiveButton("Ya") { dialog, _ ->
                aksiPositif()
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                viewModel.muatTugas() // Refresh data untuk memulihkan visual checkbox jika batal centang
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    // --- DIALOG POP-UP DETAIL LENGKAP ---
    private fun tampilDialogDetail(tugas: Tugas) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_detail_tugas, null)
        dialog.setContentView(view)

        view.findViewById<TextView>(R.id.det_matpel).text = tugas.mata_pelajaran
        view.findViewById<TextView>(R.id.det_judul).text = tugas.judul
        view.findViewById<TextView>(R.id.det_deadline).text = if (tugas.isSelesai) "Status: Selesai" else "Tenggat: ${tugas.deadline}"
        view.findViewById<TextView>(R.id.det_deskripsi).text = tugas.deskripsi.ifEmpty { "-" }
        view.findViewById<TextView>(R.id.det_instruksi).text = tugas.instruksi.ifEmpty { "-" }
        view.findViewById<TextView>(R.id.det_format).text = tugas.format_pengumpulan.ifEmpty { "-" }
        view.findViewById<TextView>(R.id.det_ketentuan).text = tugas.ketentuan.ifEmpty { "-" }
        view.findViewById<TextView>(R.id.det_pengajar).text = tugas.nama_pengajar.ifEmpty { "-" }

        dialog.show()
    }

    // --- DIALOG POP-UP INPUT / EDIT FORM ---
    private fun tampilDialogForm(tugasUntukDiedit: Tugas?) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_form_tugas, null)
        dialog.setContentView(view)

        val tvTitle = view.findViewById<TextView>(R.id.tv_dialog_title)
        val inJudul = view.findViewById<EditText>(R.id.in_judul)
        val inMatpel = view.findViewById<EditText>(R.id.in_matpel)
        val inDeadline = view.findViewById<EditText>(R.id.in_deadline)
        val inDeskripsi = view.findViewById<EditText>(R.id.in_deskripsi)
        val inInstruksi = view.findViewById<EditText>(R.id.in_instruksi)
        val inFormat = view.findViewById<EditText>(R.id.in_format)
        val inKetentuan = view.findViewById<EditText>(R.id.in_ketentuan)
        val inPengajar = view.findViewById<EditText>(R.id.in_pengajar)
        val btnSimpan = view.findViewById<Button>(R.id.btn_simpan_dialog)

        if (tugasUntukDiedit != null) {
            tvTitle.text = "Edit Tugas"
            inJudul.setText(tugasUntukDiedit.judul)
            inMatpel.setText(tugasUntukDiedit.mata_pelajaran)
            inDeadline.setText(tugasUntukDiedit.deadline)
            inDeskripsi.setText(tugasUntukDiedit.deskripsi)
            inInstruksi.setText(tugasUntukDiedit.instruksi)
            inFormat.setText(tugasUntukDiedit.format_pengumpulan)
            inKetentuan.setText(tugasUntukDiedit.ketentuan)
            inPengajar.setText(tugasUntukDiedit.nama_pengajar)
        }

        inDeadline.setOnClickListener {
            val kalender = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, tahun, bulan, hari ->
                TimePickerDialog(requireContext(), { _, jam, menit ->
                    val formatDeadline = String.format("%04d-%02d-%02d %02d:%02d", tahun, bulan + 1, hari, jam, menit)
                    inDeadline.setText(formatDeadline)
                }, kalender.get(Calendar.HOUR_OF_DAY), kalender.get(Calendar.MINUTE), true).show()
            }, kalender.get(Calendar.YEAR), kalender.get(Calendar.MONTH), kalender.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnSimpan.setOnClickListener {
            val judul = inJudul.text.toString()
            val matpel = inMatpel.text.toString()
            val deadline = inDeadline.text.toString()

            if (judul.isNotEmpty() && matpel.isNotEmpty() && deadline.isNotEmpty()) {
                val dataTugas = Tugas(
                    id = tugasUntukDiedit?.id ?: 0,
                    judul = judul,
                    mata_pelajaran = matpel,
                    deskripsi = inDeskripsi.text.toString(),
                    instruksi = inInstruksi.text.toString(),
                    format_pengumpulan = inFormat.text.toString(),
                    deadline = deadline,
                    ketentuan = inKetentuan.text.toString(),
                    nama_pengajar = inPengajar.text.toString(),
                    isSelesai = tugasUntukDiedit?.isSelesai ?: false,
                    tanggalSelesai = tugasUntukDiedit?.tanggalSelesai
                )

                viewModel.tambahTugas(dataTugas)
                dialog.dismiss()
                Toast.makeText(context, "Tugas Disimpan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Judul, Matpel, dan Deadline wajib diisi!", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}