package com.example.studentlife

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.studentlife.ui.AbsensiFragment
import com.example.studentlife.ui.JadwalFragment
import com.example.studentlife.ui.NoteFragment
import com.example.studentlife.ui.TugasFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Layar pertama kali dibuka langsung menampilkan Fitur Tugas
        gantiFragment(TugasFragment())

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_tugas -> gantiFragment(TugasFragment())
                R.id.menu_jadwal -> gantiFragment(JadwalFragment())
                R.id.menu_note -> gantiFragment(NoteFragment())
                R.id.menu_absensi -> gantiFragment(AbsensiFragment())
            }
            true
        }
    }

    private fun gantiFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}