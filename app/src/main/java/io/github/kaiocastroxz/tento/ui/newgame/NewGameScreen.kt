package io.github.kaiocastroxz.tento.ui.newgame

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.kaiocastroxz.tento.engine.ScoreEngine
import io.github.kaiocastroxz.tento.model.SeriesState
import io.github.kaiocastroxz.tento.ui.components.ReadableColumn
import io.github.kaiocastroxz.tento.ui.components.TentoScaffold

@Composable
fun NewGameScreen(
    series: SeriesState,
    onBack: () -> Unit,
    onStart: (team1Name: String, team2Name: String, gamesToWin: Int) -> Unit,
) {
    var name1 by rememberSaveable { mutableStateOf(series.team1Name) }
    var name2 by rememberSaveable { mutableStateOf(series.team2Name) }
    var gamesToWin by rememberSaveable { mutableIntStateOf(series.gamesToWin) }

    TentoScaffold(title = "Nova partida", onBack = onBack) { padding ->
        ReadableColumn(padding) {
            Text(
                text = "Defina as duplas e o formato da série.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )

            OutlinedTextField(
                value = name1,
                onValueChange = { if (it.length <= ScoreEngine.MAX_NAME_LENGTH) name1 = it },
                label = { Text("Nome da sua dupla") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = name2,
                onValueChange = { if (it.length <= ScoreEngine.MAX_NAME_LENGTH) name2 = it },
                label = { Text("Nome dos adversários") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = "Partidas para vencer a série",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                val options = SeriesState.GAMES_TO_WIN_OPTIONS
                options.forEachIndexed { index, value ->
                    SegmentedButton(
                        selected = gamesToWin == value,
                        onClick = { gamesToWin = value },
                        shape = SegmentedButtonDefaults.itemShape(index, options.size),
                        modifier = Modifier.semantics {
                            contentDescription = if (value == 1) {
                                "Partida única"
                            } else {
                                "Vence a série quem ganhar $value partidas"
                            }
                        },
                    ) { Text(if (value == 1) "Única" else "$value") }
                }
            }
            Text(
                text = if (gamesToWin == 1) {
                    "Partida única: uma partida de 12 tentos e acabou."
                } else {
                    "A série termina quando uma dupla vence $gamesToWin partidas. " +
                        "Cada partida vai até 12 tentos."
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = "Variante: Truco Paulista, Goiano ou Mineiro — o placar é o mesmo. " +
                    "Veja os resumos em Regras.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )

            Button(
                onClick = { onStart(name1, name2, gamesToWin) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                ),
            ) {
                Text(
                    if (gamesToWin == 1) "Começar partida" else "Começar série",
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
