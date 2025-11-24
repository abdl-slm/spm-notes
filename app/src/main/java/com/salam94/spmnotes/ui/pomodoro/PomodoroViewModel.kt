package com.salam94.spmnotes.ui.pomodoro

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Define the states
enum class TimerStatus { IDLE, RUNNING, PAUSED, COMPLETED }

class PomodoroViewModel : ViewModel() {

    // 1. State - Single source of truth
    private val _timeLeft = MutableStateFlow(25 * 60 * 1000L) // Default 25min in ms
    val timeLeft = _timeLeft.asStateFlow()

    private val _status = MutableStateFlow(TimerStatus.IDLE)
    val status = _status.asStateFlow()

    // 2. Internals for precision
    private var timerJob: Job? = null
    private var endTime = 0L

    // 3. Actions
    fun startTimer(durationMinutes: Int = 25) {
        if (_status.value == TimerStatus.RUNNING) return

        val durationMs = durationMinutes * 60 * 1000L

        // CORE LOGIC: We calculate the exact time the timer SHOULD end.
        // This makes it robust even if the main thread hangs or lags.
        endTime = SystemClock.elapsedRealtime() + durationMs

        _status.value = TimerStatus.RUNNING
        startTicking()
    }

    private fun startTicking() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_status.value == TimerStatus.RUNNING) {
                val currentTime = SystemClock.elapsedRealtime()
                val remaining = endTime - currentTime

                if (remaining <= 0) {
                    _timeLeft.value = 0
                    _status.value = TimerStatus.COMPLETED
                    // trigger notification here
                    break
                } else {
                    _timeLeft.value = remaining
                    // Update UI every second, but the math relies on system clock
                    delay(1000)
                }
            }
        }
    }

    fun stopTimer() {
        _status.value = TimerStatus.IDLE
        timerJob?.cancel()
        _timeLeft.value = 25 * 60 * 1000L // Reset
    }
}