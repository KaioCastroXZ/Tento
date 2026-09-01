package com.example.testerenato.ui.scoreboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.testerenato.model.SeriesState
import com.example.testerenato.model.SettingsState
import com.example.testerenato.model.Truco
import com.example.testerenato.ui.rememberFeedback
import com.example.testerenato.ui.theme.teamColors

@Composable
fun ScoreboardScreen(
    series: SeriesState,
    settings: SettingsState,
    onAddPoints: (team: Int, points: Int) -> Unit,
    onRemovePoint: (team: Int) -> Unit,
    onUndo: () -> Unit,
    onStartNextGame: () -> Unit,
    onResetSeries: () -> Unit,
    onRenameTeam: (team: Int, name: String) -> Unit,
    onNewSeries: () -> Unit,
    onOpenRules: () -> Unit,
    onOpenSettings: () -> Unit,
    onBack: () -> Unit,
) {
    val feedback = rememberFeedback()

    var showReset by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var editingTeam by remember { mutableStateOf<Int?>(null) }
    var quickAddTeam by remember { mutableStateOf<Int?>(null) }
    // O diálogo de fim de partida aparece enquanto a partida atual está fechada.
    // "Ver placar" (só quando a série acabou) apenas marca como visto.
    var gameOverAck by rememberSaveable { mutableStateOf(false) }
    if (!series.currentGame.isFinished && gameOverAck) gameOverAck = false
    val showGameOver = series.currentGame.isFinished && !gameOverAck

    fun add(team: Int, points: Int) {
        onAddPoints(team, points)
        feedback.onScore(settings.hapticsEnabled, settings.soundEnabled)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Cabeçalho
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar ao início",
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = "TENTO",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = if (series.isSingleGame) {
                        "Placar de Truco"
                    } else {
                        "Série até ${series.gamesToWin} partidas"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Mais opções")
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Regras do Truco") },
                        onClick = { showMenu = false; onOpenRules() },
                    )
                    DropdownMenuItem(
                        text = { Text("Configurações") },
                        onClick = { showMenu = false; onOpenSettings() },
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = {
                            Text(if (series.isSingleGame) "Zerar partida" else "Zerar série")
                        },
                        leadingIcon = {
                            Icon(Icons.Default.RestartAlt, contentDescription = null)
                        },
                        onClick = { showMenu = false; showReset = true },
                    )
                }
            }
        }

        val seriesFinished = series.isSeriesFinished

        // Duplas — dois painéis com um fio divisor central ("placar de dois lados").
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            listOf(Truco.TEAM_ONE, Truco.TEAM_TWO).forEachIndexed { index, team ->
                if (index == 1) {
                    Box(
                        Modifier
                            .align(Alignment.CenterVertically)
                            .width(1.5.dp)
                            .height(120.dp)
                            .background(MaterialTheme.colorScheme.outline),
                    )
                }
                val colors = teamColors(team)
                TeamPanel(
                    teamName = series.nameOf(team),
                    score = series.boardScoreOf(team),
                    accent = colors.accent,
                    onAccent = colors.onAccent,
                    onCard = colors.onCard,
                    isWinner = seriesFinished && series.seriesWinner == team,
                    highlightWinner = seriesFinished,
                    gamesWon = series.gamesOf(team),
                    gamesToWin = series.gamesToWin,
                    onQuickAdd = { quickAddTeam = team },
                    onIncrement = { add(team, 1) },
                    onTruco = { add(team, 3) },
                    onDecrement = { onRemovePoint(team) },
                    onEditName = { editingTeam = team },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        if (!seriesFinished) {
            Text(
                text = "toque no número de uma dupla para +6, +9 ou +12",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        SeriesStatusLine(series)

        HistoryPanel(
            series = series,
            onUndo = onUndo,
            modifier = Modifier.padding(bottom = 24.dp),
        )
    }

    // Overlays
    quickAddTeam?.let { team ->
        val colors = teamColors(team)
        AddPointsSheet(
            teamName = series.nameOf(team),
            currentScore = series.boardScoreOf(team),
            accent = colors.accent,
            onCard = colors.onCard,
            onDismiss = { quickAddTeam = null },
            onConfirmValue = { value -> add(team, value) },
        )
    }

    editingTeam?.let { team ->
        EditNameDialog(
            currentName = series.nameOf(team),
            onConfirm = { name ->
                onRenameTeam(team, name)
                editingTeam = null
            },
            onDismiss = { editingTeam = null },
        )
    }

    if (showReset) {
        ResetSeriesDialog(
            isSingleGame = series.isSingleGame,
            onConfirm = { showReset = false; onResetSeries() },
            onDismiss = { showReset = false },
        )
    }

    if (showGameOver) {
        GameOverDialog(
            series = series,
            onNextGame = onStartNextGame,
            onNewSeries = onNewSeries,
            onUndo = onUndo,
            onDismiss = { gameOverAck = true },
        )
    }
}

@Composable
private fun SeriesStatusLine(series: SeriesState) {
    val game = series.currentGame
    val leader = when {
        game.team1Score == game.team2Score ->
            "Empate em ${game.team1Score} / ${Truco.WINNING_SCORE}"
        game.team1Score > game.team2Score ->
            "${series.team1Name} na frente: ${game.team1Score} / ${Truco.WINNING_SCORE}"
        else ->
            "${series.team2Name} na frente: ${game.team2Score} / ${Truco.WINNING_SCORE}"
    }

    val text = when {
        series.isSeriesFinished -> {
            val w = series.nameOf(series.seriesWinner ?: Truco.TEAM_ONE)
            "$w venceu a série (${series.team1Games} x ${series.team2Games})"
        }
        series.isSingleGame -> leader
        else ->
            "Série ${series.team1Games} x ${series.team2Games} · partida ${series.currentGameNumber} — $leader"
    }

    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}
