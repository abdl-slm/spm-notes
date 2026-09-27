package com.salam94.spmnotes.ui.pomodoro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.salam94.spmnotes.ui.pomodoro.components.PomodoroScreen

class PomodoroFragment : Fragment() {

    private val viewModel: PomodoroViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            keepScreenOn = true

            setContent {
                PomodoroScreen(viewModel = viewModel)
            }
        }
    }

    override fun onPause() {
        super.onPause()

        // Safely check if the app was backgrounded vs device rotation
        val isChangingConfig = activity?.isChangingConfigurations ?: false
        if (!isChangingConfig && viewModel.uiState.value.status == TimerStatus.RUNNING) {
            viewModel.stopTimer(isPenalty = true)
            Toast.makeText(context, "Focus Broken! You left the app.", Toast.LENGTH_LONG).show()
        }
    }
}