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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.studentlife.R
import com.example.studentlife.adapter.NoteAdapter
import com.example.studentlife.database.Note
import com.example.studentlife.viewmodel.NoteViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteFragment : Fragment() {

    private lateinit var viewModel: NoteViewModel
    private lateinit var adapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvNotes = view.findViewById<RecyclerView>(R.id.rv_notes)
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fab_add_note)

        viewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        adapter = NoteAdapter(
            list = arrayListOf(),
            onDeleteClick = { viewModel.hapusNote(it) }
        )

        rvNotes.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvNotes.adapter = adapter

        viewModel.listNotes.observe(viewLifecycleOwner) { notes ->
            adapter.updateData(notes)
        }

        fabAdd.setOnClickListener {
            tampilDialogForm()
        }
    }

    private fun tampilDialogForm() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_form_note, null)
        dialog.setContentView(view)

        val inJudul = view.findViewById<EditText>(R.id.in_judul_note)
        val inIsi = view.findViewById<EditText>(R.id.in_isi_note)
        val btnSimpan = view.findViewById<Button>(R.id.btn_simpan_note)

        btnSimpan.setOnClickListener {
            val judul = inJudul.text.toString().trim()
            val isi = inIsi.text.toString().trim()

            if (judul.isNotEmpty() && isi.isNotEmpty()) {
                val tanggal = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                val newNote = Note(
                    judul = judul,
                    isi = isi,
                    tanggal = tanggal
                )
                viewModel.tambahNote(newNote)
                dialog.dismiss()
                Toast.makeText(context, "Catatan disimpan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Judul dan isi tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }
}