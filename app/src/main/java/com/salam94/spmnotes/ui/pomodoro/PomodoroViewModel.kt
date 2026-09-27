package com.salam94.spmnotes.ui.pomodoro

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TimerStatus { IDLE, RUNNING, PAUSED, COMPLETED }

data class PomodoroUiState(
    val totalDurationMs: Long = 25 * 60 * 1000L,
    val timeLeftMs: Long = 25 * 60 * 1000L,
    val status: TimerStatus = TimerStatus.IDLE
) {
    val progress: Float
        get() = if (totalDurationMs > 0) timeLeftMs.toFloat() / totalDurationMs.toFloat() else 0f
}

class PomodoroViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PomodoroUiState())
    val uiState: StateFlow<PomodoroUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var endTime = 0L

    fun startOrResumeTimer(durationMinutes: Int = 25) {
        if (_uiState.value.status == TimerStatus.RUNNING) return

        val currentRemaining = if (_uiState.value.status == TimerStatus.PAUSED) {
            _uiState.value.timeLeftMs
        } else {
            val totalMs = durationMinutes * 60 * 1000L
            _uiState.update { it.copy(totalDurationMs = totalMs, timeLeftMs = totalMs) }
            totalMs
        }

        endTime = SystemClock.elapsedRealtime() + currentRemaining
        _uiState.update { it.copy(status = TimerStatus.RUNNING) }

        startTicking()
    }

    fun pauseTimer() {
        if (_uiState.value.status != TimerStatus.RUNNING) return
        timerJob?.cancel()
        _uiState.update { it.copy(status = TimerStatus.PAUSED) }
    }

    fun stopTimer(isPenalty: Boolean = false) {
        timerJob?.cancel()
        val defaultDuration = 25 * 60 * 1000L
        _uiState.update {
            it.copy(
                status = TimerStatus.IDLE,
                totalDurationMs = defaultDuration,
                timeLeftMs = defaultDuration
            )
        }
    }

    private fun startTicking() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.status == TimerStatus.RUNNING) {
                val remaining = endTime - SystemClock.elapsedRealtime()

                if (remaining <= 0) {
                    _uiState.update {
                        it.copy(
                            timeLeftMs = 0L,
                            status = TimerStatus.COMPLETED
                        )
                    }
                    break
                } else {
                    _uiState.update { it.copy(timeLeftMs = remaining) }
                    delay(500) // Lower tick delay for smoother UI responsiveness
                }
            }
        }
    }
}