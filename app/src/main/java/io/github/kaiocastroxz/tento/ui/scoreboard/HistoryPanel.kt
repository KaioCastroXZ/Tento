package io.github.kaiocastroxz.tento.ui.scoreboard

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.kaiocastroxz.tento.model.GameResult
import io.github.kaiocastroxz.tento.model.ScoreEvent
import io.github.kaiocastroxz.tento.model.SeriesState

@Composable
fun HistoryPanel(
    series: SeriesState,
    onUndo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val events = series.currentGame.history
    val completed = series.completedGames
    val canUndo = events.isNotEmpty() || completed.isNotEmpty()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .semantics {
                        contentDescription = if (expanded) {
                            "Histórico, tocar para recolher"
                        } else {
                            "Histórico: ${completed.size} partidas concluídas e " +
                                "${events.size} lançamentos nesta partida, tocar para expandir"
                        }
                    }
                    .padding(vertical = 4.dp),
            ) {
                Text(
                    text = "Histórico",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = if (completed.isEmpty()) "${events.size}" else "${completed.size}p · ${events.size}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextButton(
                    onClick = onUndo,
                    enabled = canUndo,
                    modifier = Modifier.semantics {
                        contentDescription = if (events.isEmpty() && completed.isNotEmpty()) {
                            "Desfazer: reabrir a última partida encerrada"
                        } else {
                            "Desfazer o último lançamento"
                        }
                    },
                ) {
                    Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = null)
                    Text("  Desfazer", fontWeight = FontWeight.Bold)
                }
            }

            if (expanded) {
                HorizontalDivider(Modifier.padding(vertical = 4.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    if (completed.isNotEmpty()) {
                        SectionLabel("Partidas da série")
                        completed.forEachIndexed { index, game ->
                            CompletedGameRow(
                                number = index + 1,
                                game = game,
                                team1Name = series.team1Name,
                                team2Name = series.team2Name,
                            )
                        }
                        SectionLabel("Lançamentos da partida atual")
                    }

                    if (events.isEmpty()) {
                        Text(
                            text = "Nenhum lançamento nesta partida ainda.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    } else {
                        events.asReversed().forEachIndexed { index, event ->
                            HistoryRow(
                                event = event,
                                teamName = series.nameOf(event.team),
                                number = events.size - index,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(top = 10.dp, bottom = 2.dp)
            .semantics { heading() },
    )
}

@Composable
private fun CompletedGameRow(
    number: Int,
    game: GameResult,
    team1Name: String,
    team2Name: String,
) {
    val winnerName = if (game.winner == io.github.kaiocastroxz.tento.model.Truco.TEAM_ONE) team1Name else team2Name
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .semantics {
                contentDescription =
                    "Partida $number: ${game.team1Score} a ${game.team2Score}, venceu $winnerName"
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "P$number",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            text = "$winnerName",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "${game.team1Score} × ${game.team2Score}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clearAndSetSemantics { },
        )
    }
}

@Composable
private fun HistoryRow(event: ScoreEvent, teamName: String, number: Int) {
    val sign = if (event.points > 0) "+${event.points}" else "${event.points}"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .semantics {
                contentDescription = "Lançamento $number: $teamName $sign " +
                    if (event.points == 1 || event.points == -1) "tento" else "tentos"
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "$number.",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            text = teamName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = sign,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (event.points > 0) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            },
            modifier = Modifier.clearAndSetSemantics { },
        )
    }
}
