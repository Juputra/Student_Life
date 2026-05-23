package com.example.studentlife.ui

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlife.R
import com.example.studentlife.adapter.ChecklistAdapter
import com.example.studentlife.adapter.JadwalAdapter
import com.example.studentlife.database.Jadwal
import com.example.studentlife.viewmodel.JadwalViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class JadwalFragment : Fragment() {

    private lateinit var viewModel: JadwalViewModel
    private lateinit var adapterJadwal: JadwalAdapter
    private lateinit var adapterChecklist: ChecklistAdapter

    private var hariAktif = "Senin"
    private var tanggalAktif = Date()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_jadwal, container, false)
        viewModel = ViewModelProvider(this)[JadwalViewModel::class.java]

        setupUI(view)
        setupObservers(view)

        // Panggil Logika Chip Hari
        setupDayChips(view.findViewById(R.id.chip_group_hari), view.findViewById(R.id.tv_tanggal_aktif))

        view.findViewById<ExtendedFloatingActionButton>(R.id.fab_tambah_jadwal).setOnClickListener {
            tampilDialogForm(null)
        }

        return view
    }

    private fun setupUI(view: View) {
        val rvJadwal = view.findViewById<RecyclerView>(R.id.rv_jadwal)
        val rvChecklist = view.findViewById<RecyclerView>(R.id.rv_checklist)

        val inDadakan = view.findViewById<EditText>(R.id.in_barang_dadakan)
        view.findViewById<Button>(R.id.btn_tambah_dadakan).setOnClickListener {
            val barang = inDadakan.text.toString()
            if (barang.isNotEmpty()) {
                viewModel.tambahBarangDadakan(hariAktif, barang, tanggalAktif)
                inDadakan.text.clear()
            }
        }

        view.findViewById<LinearLayout>(R.id.card_seragam).setOnClickListener {
            tampilDialogSeragam()
        }

        adapterJadwal = JadwalAdapter(arrayListOf(),
            onItemClick = { jadwal -> tampilDialogDetailJadwal(jadwal) },
            onEditClick = { jadwalYangDipilih -> tampilDialogForm(jadwalYangDipilih) },
            onDeleteClick = { jadwalYangDihapus ->
                AlertDialog.Builder(requireContext()).setTitle("Hapus Jadwal").setMessage("Hapus kelas ${jadwalYangDihapus.mata_pelajaran}?").setPositiveButton("Ya") { _, _ ->
                    viewModel.hapusJadwal(jadwalYangDihapus, hariAktif, tanggalAktif)
                }.setNegativeButton("Batal", null).show()
            }
        )

        val tanggalStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(tanggalAktif)
        adapterChecklist = ChecklistAdapter(arrayListOf(), requireContext(), tanggalStr)

        rvJadwal.layoutManager = LinearLayoutManager(context)
        rvJadwal.adapter = adapterJadwal

        rvChecklist.layoutManager = LinearLayoutManager(context)
        rvChecklist.adapter = adapterChecklist
    }

    private fun setupObservers(view: View) {
        val tvEmpty = view.findViewById<TextView>(R.id.tv_empty_jadwal)
        val sectionTugas = view.findViewById<LinearLayout>(R.id.section_tugas)
        val tvListTugas = view.findViewById<TextView>(R.id.tv_list_tugas)

        viewModel.listJadwalHariIni.observe(viewLifecycleOwner) { jadwal ->
            adapterJadwal.updateData(jadwal)
            tvEmpty.visibility = if (jadwal.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.listChecklistTas.observe(viewLifecycleOwner) { checklist ->
            val tanggalStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(tanggalAktif)
            adapterChecklist.updateData(checklist, tanggalStr)
        }

        viewModel.listTugasMepet.observe(viewLifecycleOwner) { tugas ->
            if (tugas.isNotEmpty()) {
                sectionTugas.visibility = View.VISIBLE
                tvListTugas.text = tugas.joinToString(separator = "\n") { "• $it" }
            } else {
                sectionTugas.visibility = View.GONE
            }
        }

        val tvSeragam = view.findViewById<TextView>(R.id.tv_seragam_harian)
        viewModel.seragamHariIni.observe(viewLifecycleOwner) { teksSeragam ->
            tvSeragam.text = teksSeragam
            if (teksSeragam.contains("Belum diatur")) {
                tvSeragam.setTypeface(null, android.graphics.Typeface.ITALIC)
            } else {
                tvSeragam.setTypeface(null, android.graphics.Typeface.NORMAL)
            }
        }
    }

    private fun setupDayChips(chipGroup: ChipGroup, tvTanggal: TextView) {
        val days = arrayOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")
        val calendar = Calendar.getInstance()

        var indexHariIni = calendar.get(Calendar.DAY_OF_WEEK) - 2
        if (indexHariIni < 0 || indexHariIni > 4) {
            indexHariIni = 0
            while(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        days.forEachIndexed { i, day ->
            val chip = Chip(requireContext()).apply {
                text = if (i == indexHariIni) "HARI INI ($day)" else day
                isCheckable = true
                isChecked = i == indexHariIni
            }

            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    hariAktif = day
                    val selisihHari = i - indexHariIni
                    val targetCal = Calendar.getInstance()
                    targetCal.add(Calendar.DAY_OF_YEAR, selisihHari)
                    tanggalAktif = targetCal.time

                    tvTanggal.text = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID")).format(tanggalAktif)
                    viewModel.muatDataHarian(hariAktif, tanggalAktif)
                }
            }
            chipGroup.addView(chip)
        }

        hariAktif = days[indexHariIni]
        tvTanggal.text = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID")).format(tanggalAktif)
        viewModel.muatDataHarian(hariAktif, tanggalAktif)
    }

    private fun tampilDialogSeragam() {
        val inputForm = EditText(requireContext())
        inputForm.hint = "Cth: Putih Abu-abu / Batik"

        val seragamLama = viewModel.seragamHariIni.value ?: ""
        if (!seragamLama.contains("Belum diatur")) inputForm.setText(seragamLama)

        AlertDialog.Builder(requireContext())
            .setTitle("Atur Seragam $hariAktif")
            .setView(inputForm)
            .setPositiveButton("Simpan") { _, _ ->
                viewModel.simpanSeragam(hariAktif, inputForm.text.toString().trim(), tanggalAktif)
                Toast.makeText(context, "Seragam tersimpan!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hapus") { _, _ -> viewModel.simpanSeragam(hariAktif, "", tanggalAktif) }
            .setNeutralButton("Batal", null)
            .show()
    }

    private fun tampilDialogDetailJadwal(jadwal: Jadwal) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_detail_jadwal, null)
        dialog.setContentView(view)

        view.findViewById<TextView>(R.id.det_jadwal_hari).text = "Jadwal ${jadwal.hari}"
        view.findViewById<TextView>(R.id.det_jadwal_matpel).text = jadwal.mata_pelajaran
        view.findViewById<TextView>(R.id.det_jadwal_jam).text = "🕒 ${jadwal.jam_mulai} - ${jadwal.jam_selesai}"
        view.findViewById<TextView>(R.id.det_jadwal_ruangan).text = "Ruang: " + jadwal.ruangan.ifEmpty { "-" }
        view.findViewById<TextView>(R.id.det_jadwal_bawaan).text = jadwal.bawaan_spesifik.ifEmpty { "Tidak ada bawaan khusus" }

        dialog.show()
    }

    private fun tampilDialogForm(jadwalEdit: Jadwal?) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_form_jadwal, null)
        dialog.setContentView(view)

        val tvTitle = view.findViewById<TextView>(R.id.tv_dialog_title)
        val inMatpel = view.findViewById<TextInputEditText>(R.id.in_matpel_jadwal)
        val inRuangan = view.findViewById<TextInputEditText>(R.id.in_ruangan_jadwal)
        val inCatatan = view.findViewById<TextInputEditText>(R.id.in_catatan_jadwal)

        val inJamMulai = view.findViewById<TextInputEditText>(R.id.in_jam_mulai)
        val inJamSelesai = view.findViewById<TextInputEditText>(R.id.in_jam_selesai)
        val spinnerWarna = view.findViewById<Spinner>(R.id.spinner_warna)
        val btnSimpan = view.findViewById<Button>(R.id.btn_simpan_jadwal_dialog)

        val daftarNamaWarna = arrayOf("Merah", "Biru", "Hijau", "Kuning", "Ungu", "Abu-abu")
        val daftarHexWarna = arrayOf("#EF4444", "#3B82F6", "#10B981", "#F59E0B", "#8B5CF6", "#6B7280")
        spinnerWarna.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, daftarNamaWarna)

        fun showTimePicker(editText: TextInputEditText) {
            val cal = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, jam, menit ->
                editText.setText(String.format("%02d:%02d", jam, menit))
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        inJamMulai.setOnClickListener { showTimePicker(inJamMulai) }
        inJamSelesai.setOnClickListener { showTimePicker(inJamSelesai) }

        if (jadwalEdit != null) {
            tvTitle.text = "Edit Jadwal"
            inMatpel.setText(jadwalEdit.mata_pelajaran)
            inRuangan.setText(jadwalEdit.ruangan)
            inCatatan.setText(jadwalEdit.bawaan_spesifik)
            inJamMulai.setText(jadwalEdit.jam_mulai)
            inJamSelesai.setText(jadwalEdit.jam_selesai)
            btnSimpan.text = "Perbarui Jadwal"
        }

        btnSimpan.setOnClickListener {
            val matpel = inMatpel.text.toString().trim()
            val ruangan = inRuangan.text.toString().trim()
            val jamMulaiInput = inJamMulai.text.toString()
            val jamSelesaiInput = inJamSelesai.text.toString()

            if (matpel.isNotEmpty() && jamMulaiInput.isNotEmpty() && jamSelesaiInput.isNotEmpty()) {

                fun keMenit(waktu: String): Int {
                    val parts = waktu.split(":")
                    if (parts.size != 2) return 0
                    return parts[0].toInt() * 60 + parts[1].toInt()
                }

                val startBaru = keMenit(jamMulaiInput)
                val endBaru = keMenit(jamSelesaiInput)

                if (startBaru >= endBaru) {
                    Toast.makeText(context, "Jam selesai harus lebih besar!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val jadwalSaatIni = viewModel.listJadwalHariIni.value ?: emptyList()
                val isBentrok = jadwalSaatIni.any { kelasEksisting ->
                    val startEksisting = keMenit(kelasEksisting.jam_mulai)
                    val endEksisting = keMenit(kelasEksisting.jam_selesai)
                    kelasEksisting.id != (jadwalEdit?.id ?: 0) && (startBaru < endEksisting && endBaru > startEksisting)
                }

                if (isBentrok) {
                    Toast.makeText(context, "🚨 Gagal: Waktu bentrok dengan kelas lain!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val hexWarnaPilihan = daftarHexWarna[spinnerWarna.selectedItemPosition]
                val dataJadwal = Jadwal(
                    id = jadwalEdit?.id ?: 0,
                    hari = hariAktif, // SELALU GUNAKAN HARI AKTIF DI LAYAR
                    mata_pelajaran = matpel,
                    jam_mulai = jamMulaiInput,
                    jam_selesai = jamSelesaiInput,
                    ruangan = ruangan,
                    bawaan_spesifik = inCatatan.text.toString().trim(),
                    warna_hex = hexWarnaPilihan
                )

                if (jadwalEdit != null) {
                    viewModel.updateJadwal(dataJadwal)
                } else {
                    viewModel.tambahJadwal(dataJadwal, hariAktif, tanggalAktif)
                }

                dialog.dismiss()
                Toast.makeText(context, "Jadwal Berhasil Disimpan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Harap lengkapi semua kolom wajib!", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }
}