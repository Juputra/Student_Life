package com.example.studentlife.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlife.R
import com.example.studentlife.adapter.JadwalAdapter
import com.example.studentlife.viewmodel.JadwalViewModel
import com.example.studentlife.viewmodel.TugasViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DashboardFragment : Fragment() {

    private lateinit var tugasViewModel: TugasViewModel
    private lateinit var jadwalViewModel: JadwalViewModel
    private lateinit var jadwalAdapter: JadwalAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val tvDate = view.findViewById<TextView>(R.id.tv_dashboard_date)
        val tvSummaryTugas = view.findViewById<TextView>(R.id.tv_summary_tugas)
        val rvJadwal = view.findViewById<RecyclerView>(R.id.rv_dashboard_jadwal)
        val cardTimer = view.findViewById<MaterialCardView>(R.id.card_timer_dashboard)
        val cardNotes = view.findViewById<MaterialCardView>(R.id.card_notes_dashboard)
        val btnCekTugas = view.findViewById<Button>(R.id.btn_cek_tugas)

        // Entry Animations
        cardTimer.alpha = 0f
        cardTimer.translationY = 50f
        cardTimer.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(200).start()

        cardNotes.alpha = 0f
        cardNotes.translationY = 50f
        cardNotes.animate().alpha(1f).translationY(0f).setDuration(500).setStartDelay(400).start()

        // Set Tanggal Hari Ini (Indonesian Locale)
        val localeID = Locale("in", "ID")
        val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", localeID)
        tvDate.text = sdf.format(Calendar.getInstance().time)

        // Setup Jadwal Adapter
        jadwalAdapter = JadwalAdapter(arrayListOf())
        rvJadwal.layoutManager = LinearLayoutManager(context)
        rvJadwal.adapter = jadwalAdapter

        // ViewModels
        tugasViewModel = ViewModelProvider(this)[TugasViewModel::class.java]
        jadwalViewModel = ViewModelProvider(this)[JadwalViewModel::class.java]

        // Observe Tugas Summary
        tugasViewModel.listTugas.observe(viewLifecycleOwner) { allTugas ->
            val aktif = allTugas.filter { !it.isSelesai }.size
            tvSummaryTugas.text = "Ada $aktif tugas aktif yang perlu kamu cek."
        }

        // Observe Jadwal Hari Ini
        val hariIni = SimpleDateFormat("EEEE", localeID).format(Calendar.getInstance().time)
        jadwalViewModel.muatJadwalHari(hariIni)
        jadwalViewModel.listJadwal.observe(viewLifecycleOwner) { list ->
            jadwalAdapter.updateData(list.take(2))
        }

        // Navigasi
        cardTimer.setOnClickListener { pindahMenu(R.id.menu_timer) }
        btnCekTugas.setOnClickListener { pindahMenu(R.id.menu_tugas) }
        cardNotes.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, NoteFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun pindahMenu(itemId: Int) {
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = itemId
    }
}