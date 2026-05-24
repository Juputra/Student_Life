package com.example.studentlife.ui

import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.studentlife.R
import com.example.studentlife.adapter.NoteAdapter
import com.example.studentlife.database.Note
import com.example.studentlife.viewmodel.NoteViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteFragment : Fragment() {

    private lateinit var viewModel: NoteViewModel
    private lateinit var adapter: NoteAdapter
    private lateinit var notificationManager: NotificationManager
    private lateinit var vibrator: Vibrator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_note, container, false)
        viewModel = ViewModelProvider(requireActivity())[NoteViewModel::class.java]
        notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        setupTimerUI(view)
        setupNotesUI(view)

        return view
    }

    private fun setupTimerUI(view: View) {
        val tvDisplay = view.findViewById<TextView>(R.id.tv_timer_display)
        val tvStatus = view.findViewById<TextView>(R.id.tv_status_pomodoro)
        val cpTimer = view.findViewById<CircularProgressIndicator>(R.id.cp_timer)
        val btnStart = view.findViewById<MaterialButton>(R.id.btn_start_timer)
        val btnReset = view.findViewById<TextView>(R.id.btn_reset_timer)

        viewModel.waktuTimerTampil.observe(viewLifecycleOwner) { tvDisplay.text = it }
        viewModel.progressTimer.observe(viewLifecycleOwner) { cpTimer.progress = it }
        viewModel.statusTimer.observe(viewLifecycleOwner) { tvStatus.text = it }

        viewModel.isRunning.observe(viewLifecycleOwner) { isRunning ->
            if (isRunning) {
                btnStart.text = "Berhenti"
                btnStart.setBackgroundColor(android.graphics.Color.parseColor("#6B7280"))
                if (notificationManager.isNotificationPolicyAccessGranted) {
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE) // Blokir
                }
            } else {
                btnStart.text = "Mulai Belajar"
                btnStart.setBackgroundColor(android.graphics.Color.parseColor("#6200EE"))
                if (notificationManager.isNotificationPolicyAccessGranted) {
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL) // Buka
                }
            }
        }

        // Observer untuk Memicu Getaran HP
        viewModel.getaranEvent.observe(viewLifecycleOwner) { harusGetar ->
            if (harusGetar) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(1500) // Getar 1.5 Detik
                }
                viewModel.getaranEvent.value = false // Reset agar tidak getar terus
            }
        }

        btnStart.setOnClickListener {
            if (!notificationManager.isNotificationPolicyAccessGranted) {
                Toast.makeText(context, "Izinkan aplikasi membungkam notifikasi (DND) dulu!", Toast.LENGTH_LONG).show()
                startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
                return@setOnClickListener
            }
            viewModel.toggleTimer()
        }

        btnReset.setOnClickListener { viewModel.resetTimer() }
    }

    private fun setupNotesUI(view: View) {
        val rvNotes = view.findViewById<RecyclerView>(R.id.rv_notes)
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fab_add_note)
        val searchBar = view.findViewById<TextInputEditText>(R.id.in_search_note)

        adapter = NoteAdapter(arrayListOf()) { note -> bukaEditorCatatan(note) }
        rvNotes.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvNotes.adapter = adapter

        viewModel.listNotes.observe(viewLifecycleOwner) { notes -> adapter.updateData(notes) }

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { viewModel.cariNote(s.toString().trim()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        fabAdd.setOnClickListener { bukaEditorCatatan(null) }
    }

    private fun bukaEditorCatatan(noteEdit: Note?) {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_form_note)

        val inJudul = dialog.findViewById<EditText>(R.id.in_judul_note)
        val inIsi = dialog.findViewById<EditText>(R.id.in_isi_note)
        val spinnerKategori = dialog.findViewById<Spinner>(R.id.spinner_kategori)
        val btnBack = dialog.findViewById<ImageView>(R.id.btn_back_note)
        val btnHapus = dialog.findViewById<ImageView>(R.id.btn_hapus_note)
        val btnPin = dialog.findViewById<ImageView>(R.id.btn_pin_note)
        val tvTitle = dialog.findViewById<TextView>(R.id.tv_dialog_title)

        val kategoriData = arrayOf("Umum", "Kuliah", "Penting", "Ide")
        val warnaHexData = arrayOf("#FFFFFF", "#EFF6FF", "#FEF2F2", "#FFFBEB")
        spinnerKategori.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, kategoriData)

        var isPinnedNow = noteEdit?.isPinned ?: false

        if (noteEdit != null) {
            tvTitle.text = "Edit Catatan"
            inJudul.setText(noteEdit.judul)
            inIsi.setText(noteEdit.isi)
            val indexCat = kategoriData.indexOf(noteEdit.kategori)
            if (indexCat >= 0) spinnerKategori.setSelection(indexCat)
            btnHapus.visibility = View.VISIBLE
        } else {
            tvTitle.text = "Tulis Catatan Baru"
            btnHapus.visibility = View.GONE
        }

        fun perbaruiIconPin() {
            if(isPinnedNow) btnPin.setColorFilter(android.graphics.Color.parseColor("#F59E0B"))
            else btnPin.setColorFilter(android.graphics.Color.parseColor("#9CA3AF"))
        }
        perbaruiIconPin()

        btnPin.setOnClickListener {
            isPinnedNow = !isPinnedNow
            perbaruiIconPin()
        }

        var isDeleted = false
        btnHapus.setOnClickListener {
            AlertDialog.Builder(requireContext()).setTitle("Hapus").setMessage("Hapus catatan ini?")
                .setPositiveButton("Ya") { _, _ ->
                    viewModel.hapusNote(noteEdit!!)
                    isDeleted = true
                    dialog.dismiss()
                }.setNegativeButton("Batal", null).show()
        }

        btnBack.setOnClickListener { dialog.dismiss() }

        dialog.setOnDismissListener {
            if (isDeleted) return@setOnDismissListener

            var judul = inJudul.text.toString().trim()
            val isi = inIsi.text.toString().trim()

            if (judul.isEmpty() && isi.isEmpty()) return@setOnDismissListener

            if (judul.isEmpty()) {
                val kata = isi.split("\\s+".toRegex())
                judul = kata.take(4).joinToString(" ") + "..."
            }

            val warnaPilihan = warnaHexData[spinnerKategori.selectedItemPosition]
            val tglNow = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date())

            val noteBaru = Note(
                id = noteEdit?.id ?: 0,
                judul = judul,
                isi = isi,
                kategori = kategoriData[spinnerKategori.selectedItemPosition],
                warna_hex = warnaPilihan,
                tanggal = tglNow,
                isPinned = isPinnedNow
            )

            viewModel.tambahNote(noteBaru)
        }

        dialog.show()
    }
}