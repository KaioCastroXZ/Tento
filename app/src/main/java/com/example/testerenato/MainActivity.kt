package com.example.testerenato

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testerenato.data.PreferencesRepository
import com.example.testerenato.ui.TentoApp
import com.example.testerenato.ui.theme.TentoTheme
import com.example.testerenato.viewmodel.ScoreViewModel

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
