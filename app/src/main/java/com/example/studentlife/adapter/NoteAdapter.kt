package com.example.studentlife.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlife.R
import com.example.studentlife.database.Note

class NoteAdapter(
    private var list: List<Note>,
    private val onDeleteClick: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvJudul: TextView = view.findViewById(R.id.txt_judul_note)
        val tvIsi: TextView = view.findViewById(R.id.txt_isi_note)
        val tvTanggal: TextView = view.findViewById(R.id.txt_tanggal_note)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete_note)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvJudul.text = item.judul
        holder.tvIsi.text = item.isi
        holder.tvTanggal.text = item.tanggal
        holder.btnDelete.setOnClickListener { onDeleteClick(item) }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<Note>) {
        list = newList
        notifyDataSetChanged()
    }
}