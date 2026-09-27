package com.salam94.spmnotes.ui.pomodoro.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salam94.spmnotes.R
import com.salam94.spmnotes.ui.pomodoro.PomodoroViewModel
import com.salam94.spmnotes.ui.pomodoro.TimerStatus

// 1. Define custom FontFamily using assets in res/font
val AppFontFamily = FontFamily(
    Font(R.font.regular, FontWeight.Normal),
    Font(R.font.bold, FontWeight.Bold),
    Font(R.font.alt_bold, FontWeight.ExtraBold),
    Font(R.font.thin, FontWeight.Thin)
)

// 2. Define App Blue Colors
val PrimaryBlue = Color(0xFF1565C0)
val DarkBlue = Color(0xFF0D47A1)
val LightBlueTrack = Color(0xFFE3F2FD)

@SuppressLint("DefaultLocale")
@Composable
fun PomodoroScreen(viewModel: PomodoroViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    val minutes = (uiState.timeLeftMs / 1000) / 60
    val seconds = (uiState.timeLeftMs / 1000) % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = when (uiState.status) {
                TimerStatus.RUNNING -> "Stay Focused!"
                TimerStatus.PAUSED -> "Session Paused"
                TimerStatus.COMPLETED -> "Well Done! 🎉"
                TimerStatus.IDLE -> "Ready to Study?"
            },
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = AppFontFamily,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        )

        Spacer(modifier = Modifier.height(48.dp))

        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { uiState.progress },
                modifier = Modifier.size(240.dp),
                strokeWidth = 12.dp,
                color = PrimaryBlue,
                trackColor = LightBlueTrack // Light background track so progress ring is visible
            )

            Text(
                text = timeString,
                fontSize = 44.sp,
                style = MaterialTheme.typography.displayMedium,
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (uiState.status) {
                TimerStatus.RUNNING -> {
                    Button(
                        onClick = { viewModel.pauseTimer() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Pause",
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    OutlinedButton(
                        onClick = { viewModel.stopTimer(isPenalty = true) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PrimaryBlue
                        )
                    ) {
                        Text(
                            text = "Give Up",
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                TimerStatus.PAUSED -> {
                    Button(
                        onClick = { viewModel.startOrResumeTimer() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Resume",
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    OutlinedButton(
                        onClick = { viewModel.stopTimer() },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PrimaryBlue
                        )
                    ) {
                        Text(
                            text = "Reset",
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                TimerStatus.IDLE, TimerStatus.COMPLETED -> {
                    Button(
                        onClick = { viewModel.startOrResumeTimer(25) },
                        modifier = Modifier.height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Start Focus (25m)",
                            fontSize = 16.sp,
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}