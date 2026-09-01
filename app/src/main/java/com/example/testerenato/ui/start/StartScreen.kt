package com.example.testerenato.ui.start

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testerenato.model.SeriesState

@Composable
fun StartScreen(
    series: SeriesState,
    onNewGame: () -> Unit,
    onContinue: () -> Unit,
    onHowToPlay: () -> Unit,
    onRules: () -> Unit,
    onSettings: () -> Unit,
) {
    val inProgress = series.hasProgress && !series.isSeriesFinished
    val continueLabel = when {
        !series.isSingleGame ->
            "  Continuar série  (${series.team1Games} x ${series.team2Games})"
        else ->
            "  Continuar partida  (${series.currentGame.team1Score} x ${series.currentGame.team2Score})"
    }
    val newLabel = if (series.isSingleGame) "Nova partida" else "Nova série"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            IconButton(onClick = onSettings) {
                Icon(Icons.Default.Settings, contentDescription = "Configurações")
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "TENTO",
            fontSize = 68.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "Placar de Truco",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "Rápido, bonito e feito para marcar com uma mão só.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )

        Spacer(Modifier.height(40.dp))

        if (inProgress) {
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                ),
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Text(continueLabel, fontWeight = FontWeight.Bold)
            }
            if (!series.isSingleGame) {
                Text(
                    text = "Série até ${series.gamesToWin} partidas · partida ${series.currentGameNumber}" +
                        "  (${series.currentGame.team1Score} x ${series.currentGame.team2Score})",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onNewGame,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) { Text(newLabel, fontWeight = FontWeight.Bold) }
        } else {
            Button(
                onClick = onNewGame,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                ),
            ) { Text("Nova partida", fontWeight = FontWeight.Bold, fontSize = 18.sp) }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onHowToPlay,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) { Text("Como jogar", fontWeight = FontWeight.Bold) }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onRules) {
            Text("Regras do Truco (Paulista, Goiano e Mineiro)")
        }

        Spacer(Modifier.height(24.dp))
    }
}
