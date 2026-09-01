package com.example.testerenato.ui.scoreboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.testerenato.engine.ScoreEngine
import com.example.testerenato.model.SeriesState
import com.example.testerenato.model.Truco

/**
 * Diálogo mostrado quando uma partida (12 tentos) termina. Cobre três casos:
 * partida única encerrada, série encerrada, e partida encerrada com a série em andamento.
 */
@Composable
fun GameOverDialog(
    series: SeriesState,
    onNextGame: () -> Unit,
    onNewSeries: () -> Unit,
    onUndo: () -> Unit,
    onDismiss: () -> Unit,
) {
    val lastGame = series.completedGames.lastOrNull() ?: return
    val gameWinnerName = series.nameOf(lastGame.winner)
    val gameScore = "${lastGame.team1Score} x ${lastGame.team2Score}"

    val title: String
    val lines: List<String>
    val confirmLabel: String
    val confirmAction: () -> Unit
    val dismissLabel: String
    val dismissAction: () -> Unit

    when {
        series.isSingleGame -> {
            title = "🏆 Fim de jogo"
            lines = listOf("$gameWinnerName venceu!", gameScore)
            confirmLabel = "Nova partida"
            confirmAction = onNewSeries
            dismissLabel = "Continuar"
            dismissAction = onDismiss
        }
        series.isSeriesFinished -> {
            val sName = series.nameOf(series.seriesWinner ?: Truco.TEAM_ONE)
            title = "🏆 Fim da série"
            lines = listOf(
                "$sName venceu a série!",
                "Partidas: ${series.team1Games} x ${series.team2Games}",
                "Última partida: $gameScore",
            )
            confirmLabel = "Nova série"
            confirmAction = onNewSeries
            dismissLabel = "Ver placar"
            dismissAction = onDismiss
        }
        else -> {
            val nextNumber = series.completedGames.size + 1
            title = "Partida ${series.completedGames.size} encerrada"
            lines = listOf(
                "$gameWinnerName venceu $gameScore.",
                "Série: ${series.team1Games} x ${series.team2Games} — próxima é a partida $nextNumber.",
            )
            confirmLabel = "Próxima partida"
            confirmAction = onNextGame
            dismissLabel = "Desfazer partida"
            dismissAction = onUndo
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = false),
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                lines.forEachIndexed { index, line ->
                    Text(
                        text = line,
                        style = if (index == 0) {
                            MaterialTheme.typography.titleLarge
                        } else {
                            MaterialTheme.typography.bodyLarge
                        },
                        textAlign = TextAlign.Center,
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = confirmAction) { Text(confirmLabel) }
        },
        dismissButton = {
            TextButton(onClick = dismissAction) { Text(dismissLabel) }
        },
    )
}

@Composable
fun ResetSeriesDialog(
    isSingleGame: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isSingleGame) "Zerar partida?" else "Zerar série?") },
        text = {
            Text(
                if (isSingleGame) {
                    "Todo o placar e o histórico serão apagados. Os nomes das duplas continuam."
                } else {
                    "Todas as partidas e o histórico da série serão apagados. " +
                        "Os nomes das duplas e o formato continuam."
                },
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) { Text("Zerar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
    )
}

@Composable
fun EditNameDialog(
    currentName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var value by remember {
        mutableStateOf(TextFieldValue(currentName, TextRange(currentName.length)))
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nome da dupla") },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = {
                    if (it.text.length <= ScoreEngine.MAX_NAME_LENGTH) value = it
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("${value.text.length}/${ScoreEngine.MAX_NAME_LENGTH}") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onConfirm(value.text) }),
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(value.text) }) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
    )
}
