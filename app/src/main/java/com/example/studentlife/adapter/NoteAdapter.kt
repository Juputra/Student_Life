package com.example.studentlife.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlife.R
import com.example.studentlife.database.Note

class NoteAdapter(
    private var list: List<Note>,
    private val onItemClick: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val garisWarna: View = view.findViewById(R.id.garis_warna_note)
        val tvKategori: TextView = view.findViewById(R.id.txt_kategori_note)
        val tvJudul: TextView = view.findViewById(R.id.txt_judul_note)
        val tvIsi: TextView = view.findViewById(R.id.txt_isi_note)
        val tvTanggal: TextView = view.findViewById(R.id.txt_tanggal_note)
        val imgPin: ImageView = view.findViewById(R.id.img_pin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvJudul.text = item.judul
        holder.tvIsi.text = item.isi
        holder.tvKategori.text = item.kategori
        holder.tvTanggal.text = item.tanggal

        holder.imgPin.visibility = if (item.isPinned) View.VISIBLE else View.GONE

        try {
            holder.garisWarna.setBackgroundColor(Color.parseColor(item.warna_hex))
        } catch (e: Exception) {
            holder.garisWarna.setBackgroundColor(Color.parseColor("#6200EE"))
        }

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<Note>) {
        list = newList
        notifyDataSetChanged()
    }
}