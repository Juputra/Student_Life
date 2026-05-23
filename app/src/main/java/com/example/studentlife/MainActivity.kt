package com.example.studentlife

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.studentlife.ui.AbsensiFragment
import com.example.studentlife.ui.DashboardFragment
import com.example.studentlife.ui.JadwalFragment
import com.example.studentlife.ui.TimerFragment
import com.example.studentlife.ui.TugasFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Tampilan awal Dashboard saat aplikasi pertama kali dijalankan
        if (savedInstanceState == null) {
            gantiFragment(DashboardFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_dashboard -> gantiFragment(DashboardFragment())
                R.id.menu_tugas -> gantiFragment(TugasFragment())
                R.id.menu_timer -> gantiFragment(TimerFragment())
                R.id.menu_jadwal -> gantiFragment(JadwalFragment())
                R.id.menu_absensi -> gantiFragment(AbsensiFragment())
            }
            true
        }
    }

    private fun gantiFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
