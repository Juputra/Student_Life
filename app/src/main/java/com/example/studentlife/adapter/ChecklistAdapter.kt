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
    private var tanggalKunci: String
) : RecyclerView.Adapter<ChecklistAdapter.ViewHolder>() {

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
        val kunciCentang = "TAS_${tanggalKunci}_${item}"

        holder.cbItem.setOnCheckedChangeListener(null)
        holder.cbItem.text = item

        val sudahDiTas = prefs.getBoolean(kunciCentang, false)
        holder.cbItem.isChecked = sudahDiTas

        if (sudahDiTas) {
            holder.cbItem.paintFlags = holder.cbItem.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.cbItem.setTextColor(android.graphics.Color.parseColor("#9CA3AF"))
        } else {
            holder.cbItem.paintFlags = holder.cbItem.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.cbItem.setTextColor(android.graphics.Color.parseColor("#374151"))
        }

        holder.cbItem.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(kunciCentang, isChecked).apply()
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = listBarang.size

    fun updateData(newList: List<String>, tanggalBaru: String) {
        listBarang = newList
        tanggalKunci = tanggalBaru
        notifyDataSetChanged()
    }
}