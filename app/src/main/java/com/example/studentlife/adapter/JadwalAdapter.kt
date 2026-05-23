package com.example.studentlife.adapter

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
    private val onEditClick: (Jadwal) -> Unit = {},
    private val onDeleteClick: (Jadwal) -> Unit = {}
) : RecyclerView.Adapter<JadwalAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val matpel: TextView = view.findViewById(R.id.txt_matpel)
        val ruangan: TextView = view.findViewById(R.id.txt_ruangan)
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
        holder.ruangan.text = "Ruang: ${item.ruangan}"
        
        if (item.catatan_barang.isNotEmpty()) {
            holder.catatan.visibility = View.VISIBLE
            holder.catatan.text = "Bawa: ${item.catatan_barang}"
        } else {
            holder.catatan.visibility = View.GONE
        }

        holder.btnEdit.setOnClickListener { onEditClick(item) }
        holder.btnDelete.setOnClickListener { onDeleteClick(item) }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<Jadwal>) {
        list = newList
        notifyDataSetChanged()
    }
}