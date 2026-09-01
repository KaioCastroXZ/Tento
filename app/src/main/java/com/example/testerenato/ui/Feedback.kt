package com.example.testerenato.ui

import android.media.AudioManager
import android.view.HapticFeedbackConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

/** Dá feedback tátil e sonoro leve ao marcar tentos, respeitando as preferências. */
class Feedback(
    private val performHaptic: () -> Unit,
    private val playClick: () -> Unit,
) {
    fun onScore(hapticsEnabled: Boolean, soundEnabled: Boolean) {
        if (hapticsEnabled) performHaptic()
        if (soundEnabled) playClick()
    }
}

@Composable
fun rememberFeedback(): Feedback {
    val view = LocalView.current
    val context = LocalContext.current
    return remember(view, context) {
        val audioManager = context.getSystemService(AudioManager::class.java)
        Feedback(
            performHaptic = {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            },
            playClick = {
                audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK, 0.4f)
            },
        )
    }
}
