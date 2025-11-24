package com.salam94.spmnotes.ui.pomodoro

import com.salam94.spmnotes.ui.pomodoro.components.PomodoroScreen
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

class PomodoroFragment : Fragment() {

    private val viewModel: PomodoroViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Create a ComposeView programmatically
        return ComposeView(requireContext()).apply {

            // CRITICAL: This ensures the Compose UI cleans up properly when the Fragment dies
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            // This prevents the screen from sleeping (Anti-Cheat feature)
            keepScreenOn = true

            setContent {
                Box(
                    modifier = Modifier.fillMaxSize(), // 1. Take up all available space
                    contentAlignment = Alignment.Center // 2. Force child to center
                ) {
                    PomodoroScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // If timer is running and user leaves app -> FAIL THEM
        if (viewModel.status.value == TimerStatus.RUNNING) {
            viewModel.stopTimer()
            Toast.makeText(context, "Focus Broken! You left the app.", Toast.LENGTH_LONG).show()
        }
    }
}