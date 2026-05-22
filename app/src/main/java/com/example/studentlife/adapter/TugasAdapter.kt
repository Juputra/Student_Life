package com.example.studentlife.adapter

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studentlife.R
import com.example.studentlife.database.Tugas
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TugasAdapter(
    private var list: List<Tugas>,
    private val onItemClick: (Tugas) -> Unit, // Klik kartu untuk detail
    private val onEditClick: (Tugas) -> Unit,
    private val onDeleteClick: (Tugas) -> Unit,
    private val onCheckChange: (Tugas, Boolean) -> Unit
) : RecyclerView.Adapter<TugasAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val matpel: TextView = view.findViewById(R.id.card_matpel)
        val judul: TextView = view.findViewById(R.id.card_judul)
        val deadline: TextView = view.findViewById(R.id.card_deadline)
        val cbSelesai: CheckBox = view.findViewById(R.id.cb_selesai)
        val btnEdit: ImageButton = view.findViewById(R.id.btn_edit)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tugas, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.matpel.text = item.mata_pelajaran
        holder.judul.text = item.judul

        holder.cbSelesai.setOnCheckedChangeListener(null)
        holder.cbSelesai.isChecked = item.isSelesai

        // Klik seluruh area item untuk melihat detail lengkap
        holder.itemView.setOnClickListener { onItemClick(item) }

        if (item.isSelesai) {
            holder.judul.paintFlags = holder.judul.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.deadline.setTextColor(Color.parseColor("#9CA3AF"))
            holder.deadline.text = "Selesai pada: ${item.tanggalSelesai ?: "-"}"
        } else {
            holder.judul.paintFlags = holder.judul.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val deadlineDate = sdf.parse(item.deadline)
                val currentDate = Date()

                if (deadlineDate != null && deadlineDate.before(currentDate)) {
                    holder.deadline.setTextColor(Color.parseColor("#EF4444")) // Merah (Terlewat)
                    holder.deadline.text = "Terlewat! (${item.deadline})"
                } else {
                    holder.deadline.setTextColor(Color.parseColor("#10B981")) // Hijau (Aman)
                    holder.deadline.text = "Tenggat: ${item.deadline}"
                }
            } catch (e: Exception) {
                holder.deadline.text = item.deadline
            }
        }

        holder.cbSelesai.setOnClickListener {
            val checked = holder.cbSelesai.isChecked
            // Kembalikan ke status semula dulu, perpindahan asli diatur setelah konfirmasi dialog sukses
            holder.cbSelesai.isChecked = !checked
            onCheckChange(item, checked)
        }

        holder.btnEdit.setOnClickListener { onEditClick(item) }
        holder.btnDelete.setOnClickListener { onDeleteClick(item) }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<Tugas>) {
        list = newList
        notifyDataSetChanged()
    }
}