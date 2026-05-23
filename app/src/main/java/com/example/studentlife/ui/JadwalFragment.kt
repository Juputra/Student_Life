package com.example.studentlife.ui

import android.app.AlertDialog
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
import com.example.studentlife.adapter.ChecklistAdapter // BUAT ADAPTER INI (Hanya CheckBox sederhana)
import com.example.studentlife.adapter.JadwalAdapter // BUAT ADAPTER INI (Desain Kartu)
import com.example.studentlife.database.Jadwal
import com.example.studentlife.viewmodel.JadwalViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class JadwalFragment : Fragment() {

    private lateinit var viewModel: JadwalViewModel
    private lateinit var adapterJadwal: JadwalAdapter
    private lateinit var adapterChecklist: ChecklistAdapter

    // Track hari dan tanggal yang sedang dipilih user di layar
    private var hariAktif = "Senin"
    private var tanggalAktif = Date()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_jadwal, container, false)
        viewModel = ViewModelProvider(this)[JadwalViewModel::class.java]

        setupUI(view)
        setupObservers(view)
        setupDayChips(view.findViewById(R.id.chip_group_hari), view.findViewById(R.id.tv_tanggal_aktif))

        view.findViewById<FloatingActionButton>(R.id.fab_tambah_jadwal).setOnClickListener {
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
            onItemClick = { jadwal ->
                tampilDialogDetailJadwal(jadwal)
            },
            onEditClick = { jadwalYangDipilih ->
                tampilDialogForm(jadwalYangDipilih) // Buka form mode edit
            },
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
            // Hilangkan efek italic jika seragam sudah diisi
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

        // Cari hari ini ada di index berapa
        var indexHariIni = calendar.get(Calendar.DAY_OF_WEEK) - 2
        if (indexHariIni < 0 || indexHariIni > 4) {
            indexHariIni = 0
            // Majukan kalender fisik ke hari Senin depan
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
                    // Kalkulasi tanggal asli berdasarkan tab hari yang dipencet
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

        // Load pertama kali
        hariAktif = days[indexHariIni]
        tvTanggal.text = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID")).format(Date())
        viewModel.muatDataHarian(hariAktif, tanggalAktif)
    }

    private fun tampilDialogSeragam() {
        val inputForm = EditText(requireContext())
        inputForm.hint = "Cth: Putih Abu-abu / Batik / Pramuka"

        // Jika sudah ada seragam sebelumnya, tampilkan di kolom ketik agar bisa di-Edit
        val seragamLama = viewModel.seragamHariIni.value ?: ""
        if (!seragamLama.contains("Belum diatur")) {
            inputForm.setText(seragamLama)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Atur Seragam $hariAktif")
            .setView(inputForm)
            .setPositiveButton("Simpan") { _, _ ->
                val seragamBaru = inputForm.text.toString().trim()
                viewModel.simpanSeragam(hariAktif, seragamBaru, tanggalAktif) // CREATE / UPDATE
                Toast.makeText(context, "Seragam tersimpan!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hapus Data") { _, _ ->
                viewModel.simpanSeragam(hariAktif, "", tanggalAktif) // DELETE
            }
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
        view.findViewById<TextView>(R.id.det_jadwal_ruangan).text = jadwal.ruangan.ifEmpty { "Tidak ada info ruangan" }
        view.findViewById<TextView>(R.id.det_jadwal_bawaan).text = jadwal.bawaan_spesifik.ifEmpty { "Tidak ada bawaan khusus" }

        dialog.show()
    }
    private fun tampilDialogForm(jadwalEdit: Jadwal?) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_form_jadwal, null)
        dialog.setContentView(view)

        val inMatpel = view.findViewById<EditText>(R.id.in_matpel)
        val inJamMulai = view.findViewById<EditText>(R.id.in_jam_mulai)
        val inJamSelesai = view.findViewById<EditText>(R.id.in_jam_selesai)
        val inRuangan = view.findViewById<EditText>(R.id.in_ruangan)
        val inBawaan = view.findViewById<EditText>(R.id.in_bawaan)
        val spinnerWarna = view.findViewById<Spinner>(R.id.spinner_warna)
        val btnSimpan = view.findViewById<Button>(R.id.btn_simpan_jadwal)

        // Setup Spinner Warna
        val daftarNamaWarna = arrayOf("Merah", "Biru", "Hijau", "Kuning", "Ungu", "Abu-abu")
        val daftarHexWarna = arrayOf("#EF4444", "#3B82F6", "#10B981", "#F59E0B", "#8B5CF6", "#6B7280")

        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, daftarNamaWarna)
        spinnerWarna.adapter = spinnerAdapter

        // Fungsi bantu untuk memunculkan Jam
        fun showTimePicker(editText: EditText) {
            val cal = Calendar.getInstance()
            android.app.TimePickerDialog(requireContext(), { _, jam, menit ->
                editText.setText(String.format("%02d:%02d", jam, menit))
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        inJamMulai.setOnClickListener { showTimePicker(inJamMulai) }
        inJamSelesai.setOnClickListener { showTimePicker(inJamSelesai) }

        if (jadwalEdit != null) {
            inMatpel.setText(jadwalEdit.mata_pelajaran)
            inJamMulai.setText(jadwalEdit.jam_mulai)
            inJamSelesai.setText(jadwalEdit.jam_selesai)
            // (Isi juga Ruangan dan Bawaan Spesifik)
            view.findViewById<TextView>(R.id.tv_dialog_title).text = "Edit Jadwal"
        }

        btnSimpan.setOnClickListener {
            val matpel = inMatpel.text.toString()
            val jamMulaiInput = inJamMulai.text.toString()
            val jamSelesaiInput = inJamSelesai.text.toString()

            if (matpel.isNotEmpty() && jamMulaiInput.isNotEmpty() && jamSelesaiInput.isNotEmpty()) {

                // --- RUMUS MATEMATIKA IRISAN WAKTU (TIME OVERLAP) ---
                // Fungsi bantu mengubah "08:30" menjadi 510 menit (agar gampang dihitung)
                fun keMenit(waktu: String): Int {
                    val parts = waktu.split(":")
                    if (parts.size != 2) return 0
                    return parts[0].toInt() * 60 + parts[1].toInt()
                }

                val startBaru = keMenit(jamMulaiInput)
                val endBaru = keMenit(jamSelesaiInput)

                // Jika user salah input (jam mulai lebih besar dari jam selesai)
                if (startBaru >= endBaru) {
                    Toast.makeText(context, "Jam selesai harus lebih besar dari jam mulai!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val jadwalSaatIni = viewModel.listJadwalHariIni.value ?: emptyList()

                // Rumus Overlap: Start A < End B && End A > Start B
                val isBentrok = jadwalSaatIni.any { kelasEksisting ->
                    val startEksisting = keMenit(kelasEksisting.jam_mulai)
                    val endEksisting = keMenit(kelasEksisting.jam_selesai)

                    // Lewati pengecekan pada kelas yang sedang di-edit itu sendiri
                    kelasEksisting.id != (jadwalEdit?.id ?: 0) &&
                            (startBaru < endEksisting && endBaru > startEksisting)
                }

                if (isBentrok) {
                    Toast.makeText(context, "🚨 Gagal: Waktu bentrok dengan kelas lain!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener // Batalkan simpan
                }

                val hexWarnaPilihan = daftarHexWarna[spinnerWarna.selectedItemPosition]
                val jadwalBaru = Jadwal(
                    id = jadwalEdit?.id ?: 0,
                    hari = hariAktif,
                    mata_pelajaran = matpel,
                    jam_mulai = jamMulaiInput,
                    jam_selesai = jamSelesaiInput,
                    ruangan = inRuangan.text.toString(),
                    bawaan_spesifik = inBawaan.text.toString(),
                    warna_hex = hexWarnaPilihan
                )

                viewModel.tambahJadwal(jadwalBaru, hariAktif, tanggalAktif)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Matpel, Jam Mulai, dan Jam Selesai wajib diisi!", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }
}