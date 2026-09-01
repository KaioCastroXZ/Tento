package com.example.testerenato.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.testerenato.engine.GameResultCodec
import com.example.testerenato.engine.ScoreHistoryCodec
import com.example.testerenato.model.ScoreState
import com.example.testerenato.model.SeriesState
import com.example.testerenato.model.SettingsState
import com.example.testerenato.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tento_prefs")

/**
 * Fonte única de persistência do app. Guarda a série (formato, partidas
 * encerradas e a partida atual), nomes das duplas e preferências usando
 * DataStore Preferences.
 */
class PreferencesRepository(private val context: Context) {

    val seriesState: Flow<SeriesState> = context.dataStore.data.map { prefs ->
        val currentGame = ScoreState(
            team1Name = prefs[Keys.TEAM1_NAME] ?: ScoreState.DEFAULT_TEAM_1,
            team2Name = prefs[Keys.TEAM2_NAME] ?: ScoreState.DEFAULT_TEAM_2,
            team1Score = prefs[Keys.TEAM1_SCORE] ?: 0,
            team2Score = prefs[Keys.TEAM2_SCORE] ?: 0,
            history = ScoreHistoryCodec.decode(prefs[Keys.HISTORY]),
        )
        SeriesState(
            gamesToWin = prefs[Keys.GAMES_TO_WIN] ?: SeriesState.DEFAULT_GAMES_TO_WIN,
            currentGame = currentGame,
            completedGames = GameResultCodec.decode(prefs[Keys.COMPLETED_GAMES]),
        )
    }

    val settings: Flow<SettingsState> = context.dataStore.data.map { prefs ->
        SettingsState(
            themeMode = runCatching { ThemeMode.valueOf(prefs[Keys.THEME_MODE] ?: "") }
                .getOrDefault(ThemeMode.SYSTEM),
            hapticsEnabled = prefs[Keys.HAPTICS] ?: true,
            soundEnabled = prefs[Keys.SOUND] ?: false,
        )
    }

    suspend fun saveSeriesState(series: SeriesState) {
        context.dataStore.edit { prefs ->
            prefs[Keys.GAMES_TO_WIN] = series.gamesToWin
            prefs[Keys.COMPLETED_GAMES] = GameResultCodec.encode(series.completedGames)
            prefs[Keys.TEAM1_NAME] = series.currentGame.team1Name
            prefs[Keys.TEAM2_NAME] = series.currentGame.team2Name
            prefs[Keys.TEAM1_SCORE] = series.currentGame.team1Score
            prefs[Keys.TEAM2_SCORE] = series.currentGame.team2Score
            prefs[Keys.HISTORY] = ScoreHistoryCodec.encode(series.currentGame.history)
        }
    }

    suspend fun saveSettings(settings: SettingsState) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = settings.themeMode.name
            prefs[Keys.HAPTICS] = settings.hapticsEnabled
            prefs[Keys.SOUND] = settings.soundEnabled
        }
    }

    /** Apaga tudo (série, histórico e preferências). */
    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }

    private object Keys {
        val GAMES_TO_WIN = intPreferencesKey("games_to_win")
        val COMPLETED_GAMES = stringPreferencesKey("completed_games")
        val TEAM1_NAME = stringPreferencesKey("team1_name")
        val TEAM2_NAME = stringPreferencesKey("team2_name")
        val TEAM1_SCORE = intPreferencesKey("team1_score")
        val TEAM2_SCORE = intPreferencesKey("team2_score")
        val HISTORY = stringPreferencesKey("history")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val HAPTICS = booleanPreferencesKey("haptics_enabled")
        val SOUND = booleanPreferencesKey("sound_enabled")
    }
}
