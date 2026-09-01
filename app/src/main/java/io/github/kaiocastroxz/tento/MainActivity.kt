package io.github.kaiocastroxz.tento

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.kaiocastroxz.tento.data.PreferencesRepository
import io.github.kaiocastroxz.tento.ui.TentoApp
import io.github.kaiocastroxz.tento.ui.theme.TentoTheme
import io.github.kaiocastroxz.tento.viewmodel.ScoreViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = PreferencesRepository(applicationContext)
        setContent {
            val viewModel: ScoreViewModel = viewModel(
                factory = ScoreViewModel.factory(repository),
            )
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            TentoTheme(themeMode = settings.themeMode) {
                TentoApp(viewModel)
            }
        }
    }
}
