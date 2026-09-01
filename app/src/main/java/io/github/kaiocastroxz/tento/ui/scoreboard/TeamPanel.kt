package io.github.kaiocastroxz.tento.ui.scoreboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.kaiocastroxz.tento.model.Truco
import io.github.kaiocastroxz.tento.ui.theme.ScoreDigitStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val CardShape = RoundedCornerShape(22.dp)

@Composable
fun TeamPanel(
    teamName: String,
    score: Int,
    accent: Color,
    onAccent: Color,
    onCard: Color,
    isWinner: Boolean,
    highlightWinner: Boolean,
    gamesWon: Int,
    gamesToWin: Int,
    onQuickAdd: () -> Unit,
    onIncrement: () -> Unit,
    onTruco: () -> Unit,
    onDecrement: () -> Unit,
    onEditName: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val winnerHighlight = isWinner && highlightWinner

    var previousScore by remember { mutableIntStateOf(score) }
    var floatingDelta by remember { mutableStateOf<Int?>(null) }
    val pulse = remember { Animatable(1f) }

    // Uma única animação no número: um "pulso" de escala a cada mudança.
    LaunchedEffect(score) {
        val delta = score - previousScore
        previousScore = score
        // Define o badge conforme o sinal — uma queda logo em seguida limpa o "+N"
        // mesmo que esta corrotina seja cancelada pela próxima mudança de score.
        floatingDelta = delta.takeIf { it > 0 }
        if (delta != 0) {
            launch {
                pulse.snapTo(1.12f)
                pulse.animateTo(
                    1f,
                    spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
                )
            }
        }
        if (floatingDelta != null) {
            delay(900)
            floatingDelta = null
        }
    }

    Card(
        modifier = modifier.shadow(
            elevation = if (winnerHighlight) 14.dp else 3.dp,
            shape = CardShape,
            spotColor = accent,
            ambientColor = accent,
        ),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        // Friso da cor da dupla no topo do card.
        Box(
            Modifier
                .fillMaxWidth()
                .height(if (winnerHighlight) 6.dp else 3.dp)
                .background(accent),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // --- Nível 2: identificação -------------------------------------
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = teamName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = onCard,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = onEditName,
                    modifier = Modifier
                        .size(36.dp)
                        .semantics { contentDescription = "Editar o nome de $teamName" },
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            if (gamesToWin > 1) {
                Spacer(Modifier.height(2.dp))
                GamesWonRow(
                    teamName = teamName,
                    gamesWon = gamesWon,
                    gamesToWin = gamesToWin,
                    accent = accent,
                    onAccent = onAccent,
                )
            }

            Spacer(Modifier.height(18.dp))

            // --- Nível 1: o número (toque = menu rápido) -------------------
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(
                        onClickLabel = "Adicionar tentos para $teamName",
                        onClick = onQuickAdd,
                    )
                    .semantics {
                        contentDescription = "$teamName tem $score de ${Truco.WINNING_SCORE} tentos."
                    },
            ) {
                Text(
                    text = score.toString(),
                    style = ScoreDigitStyle,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.graphicsLayer {
                        scaleX = pulse.value
                        scaleY = pulse.value
                    },
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 4.dp),
                ) {
                    FloatingDelta(floatingDelta, accent, onAccent)
                }
            }

            Spacer(Modifier.height(16.dp))

            // --- Nível 3: controles (recuado) -----------------------------
            ProgressToTwelve(score = score, accent = accent, onCard = onCard)

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedIconButton(
                    onClick = onDecrement,
                    enabled = score > 0,
                    modifier = Modifier
                        .size(52.dp)
                        .semantics { contentDescription = "Remover um tento de $teamName" },
                ) {
                    Icon(Icons.Default.Remove, contentDescription = null)
                }

                BigAddButton(
                    accent = accent,
                    onAccent = onAccent,
                    contentDescription = "Adicionar um tento para $teamName",
                    onClick = onIncrement,
                    onLongPress = onQuickAdd,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(8.dp))

            TrucoButton(
                accent = accent,
                onCard = onCard,
                contentDescription = "Truco: adicionar três tentos para $teamName",
                onClick = onTruco,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun FloatingDelta(delta: Int?, accent: Color, onAccent: Color) {
    var lastShown by remember { mutableIntStateOf(0) }
    if (delta != null && delta != lastShown) lastShown = delta

    AnimatedVisibility(
        visible = delta != null,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { -it / 2 } + fadeOut(animationSpec = tween(400)),
    ) {
        Text(
            text = "+$lastShown",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = onAccent,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(accent)
                .padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}

@Composable
private fun BigAddButton(
    accent: Color,
    onAccent: Color,
    contentDescription: String,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(accent)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress,
                onClickLabel = contentDescription,
                onLongClickLabel = "Escolher quantos tentos adicionar",
            )
            .semantics { role = Role.Button },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = null,
            tint = onAccent,
            modifier = Modifier.size(34.dp),
        )
    }
}

@Composable
private fun TrucoButton(
    accent: Color,
    onCard: Color,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(shape)
            .background(accent.copy(alpha = 0.16f))
            .border(1.5.dp, accent, shape)
            .clickable(onClickLabel = contentDescription, onClick = onClick)
            .semantics { role = Role.Button },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "TRUCO   ·   +3",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = onCard,
        )
    }
}

@Composable
private fun GamesWonRow(
    teamName: String,
    gamesWon: Int,
    gamesToWin: Int,
    accent: Color,
    onAccent: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "$teamName venceu $gamesWon de $gamesToWin partidas da série"
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "PARTIDAS  $gamesWon/$gamesToWin",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.size(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            repeat(gamesToWin) { index ->
                val filled = index < gamesWon
                Canvas(Modifier.size(11.dp)) {
                    drawCircle(
                        color = if (filled) accent else accent.copy(alpha = 0.20f),
                    )
                    if (filled) {
                        drawLine(
                            color = onAccent,
                            start = Offset(size.width * 0.28f, size.height * 0.72f),
                            end = Offset(size.width * 0.72f, size.height * 0.28f),
                            strokeWidth = size.width * 0.16f,
                            cap = StrokeCap.Round,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressToTwelve(score: Int, accent: Color, onCard: Color) {
    val progress by animateFloatAsState(
        targetValue = score.toFloat() / Truco.WINNING_SCORE,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "progress",
    )
    val nearEnd = score >= Truco.WINNING_SCORE - 1
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = accent,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        Text(
            text = "$score / ${Truco.WINNING_SCORE} tentos",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (nearEnd) FontWeight.Bold else FontWeight.Normal,
            color = if (nearEnd) onCard else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
