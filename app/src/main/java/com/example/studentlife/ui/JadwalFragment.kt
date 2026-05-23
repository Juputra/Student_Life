package com.example.studentlife.ui

import android.app.AlertDialog
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
import com.example.studentlife.adapter.JadwalAdapter
import com.example.studentlife.database.Jadwal
import com.example.studentlife.viewmodel.JadwalViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class JadwalFragment : Fragment() {

    private lateinit var viewModel: JadwalViewModel
    private lateinit var adapter: JadwalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_jadwal, container, false)

        val inputHariSearch = view.findViewById<EditText>(R.id.input_hari)
        val btnLihatHari = view.findViewById<Button>(R.id.btn_lihat_hari)
        val rvJadwal = view.findViewById<RecyclerView>(R.id.rv_jadwal)
        val fabTambah = view.findViewById<ExtendedFloatingActionButton>(R.id.fab_tambah_jadwal)

        adapter = JadwalAdapter(
            list = arrayListOf(),
            onEditClick = { tampilDialogForm(it) },
            onDeleteClick = { konfirmasiHapus(it) }
        )
        rvJadwal.layoutManager = LinearLayoutManager(context)
        rvJadwal.adapter = adapter

        viewModel = ViewModelProvider(this)[JadwalViewModel::class.java]

        viewModel.listJadwal.observe(viewLifecycleOwner) { daftarJadwal ->
            adapter.updateData(daftarJadwal)
        }

        btnLihatHari.setOnClickListener {
            val hari = inputHariSearch.text.toString().trim()
            if (hari.isNotEmpty()) {
                viewModel.muatJadwalHari(hari)
            } else {
                Toast.makeText(context, "Ketik nama hari dulu!", Toast.LENGTH_SHORT).show()
            }
        }

        fabTambah.setOnClickListener {
            tampilDialogForm(null)
        }

        return view
    }

    private fun tampilDialogForm(jadwalUntukDiedit: Jadwal?) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_form_jadwal, null)
        dialog.setContentView(view)

        val tvTitle = view.findViewById<TextView>(R.id.tv_dialog_title)
        val inHari = view.findViewById<EditText>(R.id.in_hari_jadwal)
        val inMatpel = view.findViewById<EditText>(R.id.in_matpel_jadwal)
        val inRuangan = view.findViewById<EditText>(R.id.in_ruangan_jadwal)
        val inCatatan = view.findViewById<EditText>(R.id.in_catatan_jadwal)
        val btnSimpan = view.findViewById<Button>(R.id.btn_simpan_jadwal_dialog)

        if (jadwalUntukDiedit != null) {
            tvTitle.text = "Edit Jadwal"
            inHari.setText(jadwalUntukDiedit.hari)
            inMatpel.setText(jadwalUntukDiedit.mata_pelajaran)
            inRuangan.setText(jadwalUntukDiedit.ruangan)
            inCatatan.setText(jadwalUntukDiedit.catatan_barang)
            btnSimpan.text = "Perbarui Jadwal"
        } else {
            tvTitle.text = "Tambah Jadwal Baru"
            btnSimpan.text = "Simpan Jadwal"
        }

        btnSimpan.setOnClickListener {
            val hari = inHari.text.toString().trim()
            val matpel = inMatpel.text.toString().trim()
            val ruangan = inRuangan.text.toString().trim()

            if (hari.isNotEmpty() && matpel.isNotEmpty() && ruangan.isNotEmpty()) {
                val dataJadwal = Jadwal(
                    id = jadwalUntukDiedit?.id ?: 0,
                    hari = hari,
                    mata_pelajaran = matpel,
                    ruangan = ruangan,
                    catatan_barang = inCatatan.text.toString().trim()
                )

                if (jadwalUntukDiedit != null) {
                    viewModel.updateJadwal(dataJadwal)
                    Toast.makeText(context, "Jadwal Diperbarui", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.tambahJadwal(dataJadwal)
                    Toast.makeText(context, "Jadwal Disimpan", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Lengkapi data wajib!", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun konfirmasiHapus(jadwal: Jadwal) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Jadwal")
            .setMessage("Hapus jadwal ${jadwal.mata_pelajaran} di hari ${jadwal.hari}?")
            .setPositiveButton("Hapus") { _, _ ->
                viewModel.hapusJadwal(jadwal)
                Toast.makeText(context, "Jadwal dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}