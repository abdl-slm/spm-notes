package com.salam94.spmnotes.ui.pomodoro.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salam94.spmnotes.ui.pomodoro.PomodoroViewModel
import com.salam94.spmnotes.ui.pomodoro.TimerStatus
import androidx.compose.runtime.getValue
import androidx.compose.material3.Text

import androidx.compose.ui.graphics.Color // <--- Add this import
import androidx.compose.foundation.background // <--- Add this import
import androidx.compose.foundation.layout.fillMaxSize

@Composable
fun PomodoroScreen(viewModel: PomodoroViewModel) {
    val timeLeft by viewModel.timeLeft.collectAsState()
    val status by viewModel.status.collectAsState()

    // Define your custom color
    // Note: In Compose, we use 0xFF for opacity (100%), followed by the hex code
    val primaryColor = Color(0xFF1565c0)
    val whiteColor = Color.White // Text needs to be white to read it on blue

    val minutes = (timeLeft / 1000) / 60
    val seconds = (timeLeft / 1000) % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        // 1. SET BACKGROUND HERE & FILL SIZE
        modifier = Modifier
            .fillMaxSize()
            .background(primaryColor),
        // 2. Center content vertically (since we are filling max size now)
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { timeLeft / (25f * 60 * 1000) },
                modifier = Modifier.size(200.dp),
                strokeWidth = 8.dp,
                // Make the progress indicator White so it pops against the blue
                color = whiteColor,
                trackColor = whiteColor.copy(alpha = 0.3f) // Faint white track
            )

            Text(
                text = timeString,
                style = MaterialTheme.typography.headlineLarge,
                // Make text White
                color = whiteColor
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // For the button, we usually want it to contrast (e.g., White button with Blue text)
        // or keep standard styling. Here is a high-contrast version:
        androidx.compose.material3.Button(
            onClick = {
                if (status == TimerStatus.RUNNING) viewModel.stopTimer()
                else viewModel.startTimer(25)
            },
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = whiteColor,
                contentColor = primaryColor
            )
        ) {
            Text(if (status == TimerStatus.RUNNING) "Give Up" else "Start Focus")
        }
    }
}