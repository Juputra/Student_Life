package com.example.studentlife.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlife.R
import com.example.studentlife.database.Jadwal

class JadwalAdapter(private var list: List<Jadwal>) : RecyclerView.Adapter<JadwalAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val matpel: TextView = view.findViewById(R.id.txt_matpel)
        val ruangan: TextView = view.findViewById(R.id.txt_ruangan)
        val catatan: TextView = view.findViewById(R.id.txt_catatan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_jadwal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.matpel.text = item.mata_pelajaran
        holder.ruangan.text = "Ruang: ${item.ruangan}"
        holder.catatan.text = "Bawa: ${item.catatan_barang}"
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<Jadwal>) {
        list = newList
        notifyDataSetChanged()
    }
}