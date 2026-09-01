package io.github.kaiocastroxz.tento.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.kaiocastroxz.tento.ui.newgame.NewGameScreen
import io.github.kaiocastroxz.tento.ui.rules.HowToPlayScreen
import io.github.kaiocastroxz.tento.ui.rules.RulesScreen
import io.github.kaiocastroxz.tento.ui.scoreboard.ScoreboardScreen
import io.github.kaiocastroxz.tento.ui.settings.SettingsScreen
import io.github.kaiocastroxz.tento.ui.start.StartScreen
import io.github.kaiocastroxz.tento.viewmodel.ScoreViewModel

sealed interface Screen {
    data object Start : Screen
    data object NewGame : Screen
    data object Scoreboard : Screen
    data object Rules : Screen
    data object HowToPlay : Screen
    data object Settings : Screen
}

@Composable
fun TentoApp(viewModel: ScoreViewModel) {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Start) }
    val current = backStack.last()

    fun navigateTo(screen: Screen) {
        if (backStack.last() != screen) backStack.add(screen)
    }

    fun pop() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }

    /** Vai para o placar deixando "Início" como única tela anterior. */
    fun openScoreboardFresh() {
        backStack.clear()
        backStack.add(Screen.Start)
        backStack.add(Screen.Scoreboard)
    }

    BackHandler(enabled = backStack.size > 1) { pop() }

    val seriesState by viewModel.seriesState.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        AnimatedContent(
            targetState = current,
            transitionSpec = {
                (fadeIn() togetherWith fadeOut()) using SizeTransform(clip = false)
            },
            label = "screen",
        ) { screen ->
            Box(Modifier.fillMaxSize()) {
                when (screen) {
                    Screen.Start -> StartScreen(
                        series = seriesState,
                        onNewGame = { navigateTo(Screen.NewGame) },
                        onContinue = { navigateTo(Screen.Scoreboard) },
                        onHowToPlay = { navigateTo(Screen.HowToPlay) },
                        onRules = { navigateTo(Screen.Rules) },
                        onSettings = { navigateTo(Screen.Settings) },
                    )

                    Screen.NewGame -> NewGameScreen(
                        series = seriesState,
                        onBack = { pop() },
                        onStart = { name1, name2, gamesToWin ->
                            viewModel.startNewSeries(name1, name2, gamesToWin)
                            openScoreboardFresh()
                        },
                    )

                    Screen.Scoreboard -> ScoreboardScreen(
                        series = seriesState,
                        settings = settings,
                        onAddPoints = viewModel::addPoints,
                        onRemovePoint = viewModel::removePoint,
                        onUndo = viewModel::undoLastAction,
                        onStartNextGame = viewModel::startNextGame,
                        onResetSeries = viewModel::resetSeries,
                        onRenameTeam = viewModel::updateTeamName,
                        onNewSeries = { viewModel.resetSeries() },
                        onOpenRules = { navigateTo(Screen.Rules) },
                        onOpenSettings = { navigateTo(Screen.Settings) },
                        onBack = { pop() },
                    )

                    Screen.Rules -> RulesScreen(onBack = { pop() })

                    Screen.HowToPlay -> HowToPlayScreen(
                        onBack = { pop() },
                        onOpenFullRules = { navigateTo(Screen.Rules) },
                    )

                    Screen.Settings -> SettingsScreen(
                        settings = settings,
                        onBack = { pop() },
                        onThemeMode = viewModel::setThemeMode,
                        onHaptics = viewModel::setHaptics,
                        onSound = viewModel::setSound,
                        onResetData = viewModel::resetAllData,
                    )
                }
            }
        }
    }
}
