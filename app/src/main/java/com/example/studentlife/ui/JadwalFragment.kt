package com.example.studentlife.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlife.R
import com.example.studentlife.adapter.JadwalAdapter
import com.example.studentlife.database.Jadwal
import com.example.studentlife.viewmodel.JadwalViewModel

class JadwalFragment : Fragment() {

    private lateinit var viewModel: JadwalViewModel
    private lateinit var adapter: JadwalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_jadwal, container, false)

        val inputHari = view.findViewById<EditText>(R.id.input_hari)
        val inputMatpel = view.findViewById<EditText>(R.id.input_matpel)
        val inputRuangan = view.findViewById<EditText>(R.id.input_ruangan)
        val inputCatatan = view.findViewById<EditText>(R.id.input_catatan)
        val btnLihatHari = view.findViewById<Button>(R.id.btn_lihat_hari)
        val btnSimpan = view.findViewById<Button>(R.id.btn_simpan_jadwal)
        val rvJadwal = view.findViewById<RecyclerView>(R.id.rv_jadwal)

        adapter = JadwalAdapter(arrayListOf())
        rvJadwal.layoutManager = LinearLayoutManager(context)
        rvJadwal.adapter = adapter

        viewModel = ViewModelProvider(this)[JadwalViewModel::class.java]

        // Pantau perubahan data dari ViewModel
        viewModel.listJadwal.observe(viewLifecycleOwner) { daftarJadwal ->
            adapter.updateData(daftarJadwal)
        }

        // Tombol untuk melihat jadwal di hari tertentu
        btnLihatHari.setOnClickListener {
            val hari = inputHari.text.toString().trim()
            if (hari.isNotEmpty()) {
                viewModel.muatJadwalHari(hari)
            } else {
                Toast.makeText(context, "Ketik nama hari dulu!", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol untuk menyimpan jadwal baru
        btnSimpan.setOnClickListener {
            val hari = inputHari.text.toString().trim()
            val matpel = inputMatpel.text.toString().trim()
            val ruangan = inputRuangan.text.toString().trim()
            val catatan = inputCatatan.text.toString().trim()

            if (hari.isNotEmpty() && matpel.isNotEmpty() && ruangan.isNotEmpty()) {
                val jadwalBaru = Jadwal(
                    hari = hari,
                    mata_pelajaran = matpel,
                    ruangan = ruangan,
                    catatan_barang = catatan
                )
                viewModel.tambahJadwal(jadwalBaru)

                // Kosongkan form setelah berhasil simpan (kecuali hari agar mudah input beruntun)
                inputMatpel.text.clear()
                inputRuangan.text.clear()
                inputCatatan.text.clear()
                Toast.makeText(context, "Jadwal ditambahkan!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Hari, Matpel, dan Ruangan wajib diisi!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}