package com.example.studentlife.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.studentlife.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.util.Locale

class TimerFragment : Fragment() {

    private var countDownTimer: CountDownTimer? = null
    private var isRunning = false
    private var timeLeftInMillis: Long = 1500000 // Default 25 Menit
    private var initialTimeInMillis: Long = 1500000

    private lateinit var tvDisplay: TextView
    private lateinit var btnStart: MaterialButton
    private lateinit var cpTimer: CircularProgressIndicator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_timer, container, false)

        tvDisplay = view.findViewById(R.id.tv_timer_display)
        btnStart = view.findViewById(R.id.btn_start_timer)
        cpTimer = view.findViewById(R.id.cp_timer)
        val btnReset = view.findViewById<TextView>(R.id.btn_reset_timer)

        // Preset Buttons
        val btn15 = view.findViewById<Button>(R.id.btn_15)
        val btn25 = view.findViewById<Button>(R.id.btn_25)
        val btn45 = view.findViewById<Button>(R.id.btn_45)
        val btn60 = view.findViewById<Button>(R.id.btn_60)

        btn15.setOnClickListener { setDuration(15) }
        btn25.setOnClickListener { setDuration(25) }
        btn45.setOnClickListener { setDuration(45) }
        btn60.setOnClickListener { setDuration(60) }

        btnStart.setOnClickListener {
            if (isRunning) stopTimer() else startTimer()
        }

        btnReset.setOnClickListener { resetTimer() }

        updateCountDownText()

        return view
    }

    private fun setDuration(minutes: Int) {
        if (isRunning) {
            Toast.makeText(context, "Berhentikan timer dulu untuk mengubah waktu", Toast.LENGTH_SHORT).show()
            return
        }
        initialTimeInMillis = minutes * 60 * 1000L
        timeLeftInMillis = initialTimeInMillis
        updateCountDownText()
        cpTimer.progress = 100
        btnStart.text = "Mulai Belajar"
    }

    private fun startTimer() {
        val animation = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.pulse)
        tvDisplay.startAnimation(animation)

        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
                
                val progress = (timeLeftInMillis.toDouble() / initialTimeInMillis.toDouble() * 100).toInt()
                cpTimer.progress = progress
            }

            override fun onFinish() {
                isRunning = false
                tvDisplay.clearAnimation()
                btnStart.text = "Selesai"
                Toast.makeText(context, "Waktu belajar habis! Istirahat sejenak.", Toast.LENGTH_LONG).show()
            }
        }.start()

        isRunning = true
        btnStart.text = "Berhenti"
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        tvDisplay.clearAnimation()
        isRunning = false
        btnStart.text = "Lanjut"
    }

    private fun resetTimer() {
        stopTimer()
        tvDisplay.clearAnimation()
        timeLeftInMillis = initialTimeInMillis
        updateCountDownText()
        cpTimer.progress = 100
        btnStart.text = "Mulai Belajar"
    }

    private fun updateCountDownText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        tvDisplay.text = timeFormatted
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}