package com.example.studentlife.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlife.R
import com.example.studentlife.database.Jadwal

class JadwalAdapter(
    private var list: List<Jadwal>,
    private val onItemClick: (Jadwal) -> Unit,
    private val onEditClick: (Jadwal) -> Unit,
    private val onDeleteClick: (Jadwal) -> Unit
) : RecyclerView.Adapter<JadwalAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val garisWarna: View = view.findViewById(R.id.garis_warna)
        val matpel: TextView = view.findViewById(R.id.txt_matpel)
        val ruanganDanJam: TextView = view.findViewById(R.id.txt_ruangan)
        val catatan: TextView = view.findViewById(R.id.txt_catatan)
        val btnEdit: ImageView = view.findViewById(R.id.btn_edit_jadwal)
        val btnDelete: ImageView = view.findViewById(R.id.btn_delete_jadwal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_jadwal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.matpel.text = item.mata_pelajaran
        // Menggabungkan waktu dan ruangan ke UI MAIN
        holder.ruanganDanJam.text = "🕒 ${item.jam_mulai} - ${item.jam_selesai}  |  📍 ${item.ruangan.ifEmpty { "-" }}"

        if (item.bawaan_spesifik.isNotEmpty()) {
            holder.catatan.visibility = View.VISIBLE
            holder.catatan.text = "Bawa: ${item.bawaan_spesifik}"
        } else {
            holder.catatan.visibility = View.GONE
        }

        try {
            holder.garisWarna.setBackgroundColor(Color.parseColor(item.warna_hex))
        } catch (e: Exception) {
            holder.garisWarna.setBackgroundColor(Color.parseColor("#4CAF50"))
        }

        holder.btnEdit.setOnClickListener { onEditClick(item) }
        holder.btnDelete.setOnClickListener { onDeleteClick(item) }
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<Jadwal>) {
        list = newList
        notifyDataSetChanged()
    }
}