package com.example.studentlife.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.studentlife.R

class AbsensiFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Kita hanya butuh ini saja untuk menampilkan layoutnya
        return inflater.inflate(R.layout.fragment_absensi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Di sinilah nanti kita akan mengisi logika Absensi
        // seperti menghubungkan ViewModel dan Adapter.
    }
}