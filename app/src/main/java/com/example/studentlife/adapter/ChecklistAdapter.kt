package com.example.studentlife.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlife.R

class ChecklistAdapter(
    private var listBarang: List<String>,
    private val context: Context,
    private var tanggalKunci: String // Cth: "2026-05-23"
) : RecyclerView.Adapter<ChecklistAdapter.ViewHolder>() {

    // Akses memori internal HP
    private val prefs = context.getSharedPreferences("MemoriTasSekolah", Context.MODE_PRIVATE)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cbItem: CheckBox = view.findViewById(R.id.cb_item_tas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checklist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listBarang[position]

        // Buat kunci unik per barang di hari tersebut
        val kunciCentang = "TAS_${tanggalKunci}_${item}"

        holder.cbItem.setOnCheckedChangeListener(null) // Reset listener
        holder.cbItem.text = item

        // Cek ke memori HP apakah hari ini sudah dicentang
        val sudahDiTas = prefs.getBoolean(kunciCentang, false)
        holder.cbItem.isChecked = sudahDiTas

        // Efek Coret
        if (sudahDiTas) {
            holder.cbItem.paintFlags = holder.cbItem.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.cbItem.setTextColor(android.graphics.Color.parseColor("#9CA3AF"))
        } else {
            holder.cbItem.paintFlags = holder.cbItem.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.cbItem.setTextColor(android.graphics.Color.parseColor("#374151"))
        }

        // Kalau dicentang user, simpan ke memori HP
        holder.cbItem.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(kunciCentang, isChecked).apply()
            notifyItemChanged(position) // Render ulang coretannya
        }
    }

    override fun getItemCount(): Int = listBarang.size

    // Panggil ini dari Fragment tiap ganti tab hari
    fun updateData(newList: List<String>, tanggalBaru: String) {
        listBarang = newList
        tanggalKunci = tanggalBaru
        notifyDataSetChanged()
    }
}