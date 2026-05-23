package com.example.studentlife.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlife.R
import com.example.studentlife.database.Absensi

class AbsensiAdapter(
    private var list: List<Absensi>,
    private val onHadirClick: (Absensi) -> Unit
) : RecyclerView.Adapter<AbsensiAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMatkul: TextView = view.findViewById(R.id.txt_matkul)
        val tvPersentase: TextView = view.findViewById(R.id.txt_persentase)
        val btnHadir: Button = view.findViewById(R.id.btn_hadir)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_absensi, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvMatkul.text = item.mata_pelajaran
        
        // Kalkulasi sederhana: hadir / 14 pertemuan
        val persen = (item.hadir.toDouble() / 14.0 * 100).toInt()
        holder.tvPersentase.text = "Kehadiran: $persen%"
        
        holder.btnHadir.setOnClickListener { onHadirClick(item) }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<Absensi>) {
        list = newList
        notifyDataSetChanged()
    }
}