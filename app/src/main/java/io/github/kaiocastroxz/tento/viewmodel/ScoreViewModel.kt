package io.github.kaiocastroxz.tento.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.github.kaiocastroxz.tento.data.PreferencesRepository
import io.github.kaiocastroxz.tento.engine.SeriesEngine
import io.github.kaiocastroxz.tento.model.SeriesState
import io.github.kaiocastroxz.tento.model.SettingsState
import io.github.kaiocastroxz.tento.model.ThemeMode
import io.github.kaiocastroxz.tento.model.Truco
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Controla a lógica da série (melhor de N partidas) e faz a ponte com a persistência.
 */
class ScoreViewModel(private val repository: PreferencesRepository) : ViewModel() {

    private val engine = SeriesEngine()

    private val _seriesState = MutableStateFlow(SeriesState())
    val seriesState: StateFlow<SeriesState> = _seriesState.asStateFlow()

    private val _restored = MutableStateFlow(false)
    val restored: StateFlow<Boolean> = _restored.asStateFlow()

    val settings: StateFlow<SettingsState> = repository.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, SettingsState())

    init {
        viewModelScope.launch {
            val saved = repository.seriesState.first()
            _seriesState.value = engine.load(saved)
            _restored.value = true
        }
    }

    // --- Série / placar ---------------------------------------------------

    fun addPoints(team: Int, points: Int) = commit(engine.addPoints(team, points))

    fun removePoint(team: Int) = commit(engine.removePoint(team))

    fun undoLastAction() = commit(engine.undoLastAction())

    fun startNextGame() = commit(engine.startNextGame())

    fun resetSeries() = commit(engine.resetSeries())

    fun startNewSeries(team1Name: String, team2Name: String, gamesToWin: Int) =
        commit(engine.newSeries(team1Name, team2Name, gamesToWin))

    fun updateTeamName(team: Int, name: String) = commit(engine.updateTeamName(team, name))

    private fun commit(newState: SeriesState) {
        _seriesState.value = newState
        viewModelScope.launch { repository.saveSeriesState(newState) }
    }

    // --- Preferências ----------------------------------------------------

    fun setThemeMode(mode: ThemeMode) = saveSettings(settings.value.copy(themeMode = mode))

    fun setHaptics(enabled: Boolean) = saveSettings(settings.value.copy(hapticsEnabled = enabled))

    fun setSound(enabled: Boolean) = saveSettings(settings.value.copy(soundEnabled = enabled))

    private fun saveSettings(newSettings: SettingsState) {
        viewModelScope.launch { repository.saveSettings(newSettings) }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.clearAll()
            _seriesState.value = engine.load(SeriesState())
        }
    }

    companion object {
        fun factory(repository: PreferencesRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ScoreViewModel(repository) as T
            }

        val QUICK_ADD_VALUES = Truco.HAND_VALUES
    }
}
