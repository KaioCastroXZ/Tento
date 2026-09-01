package io.github.kaiocastroxz.tento.ui.scoreboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.kaiocastroxz.tento.model.Truco

/**
 * Menu rápido de pontuação: +1, +3, +6, +9, +12.
 * Pede confirmação quando o valor escolhido passaria de 12 (será limitado a 12).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPointsSheet(
    teamName: String,
    currentScore: Int,
    accent: Color,
    onCard: Color,
    onDismiss: () -> Unit,
    onConfirmValue: (Int) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var pendingOverflowValue by remember { mutableStateOf<Int?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Adicionar tentos",
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = "$teamName • $currentScore / ${Truco.WINNING_SCORE}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Truco.HAND_VALUES.forEach { value ->
                    val wouldOverflow = currentScore + value > Truco.WINNING_SCORE
                    OutlinedButton(
                        onClick = {
                            if (wouldOverflow) {
                                pendingOverflowValue = value
                            } else {
                                onConfirmValue(value)
                                onDismiss()
                            }
                        },
                        shape = RoundedCornerShape(percent = 50),
                        border = BorderStroke(1.5.dp, accent),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = accent.copy(alpha = 0.14f),
                            contentColor = onCard,
                        ),
                        modifier = Modifier
                            .size(width = 96.dp, height = 60.dp)
                            .semantics {
                                contentDescription = plural(value) + " para $teamName"
                            },
                    ) {
                        Text(
                            text = "+$value",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            Text(
                text = labelForValues(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start,
            )
        }
    }

    pendingOverflowValue?.let { value ->
        AlertDialog(
            onDismissRequest = { pendingOverflowValue = null },
            title = { Text("Passa de ${Truco.WINNING_SCORE}") },
            text = {
                Text(
                    "$teamName está com $currentScore. Adicionar +$value ultrapassaria " +
                        "${Truco.WINNING_SCORE}. O placar será limitado a ${Truco.WINNING_SCORE}. Continuar?",
                )
            },
            confirmButton = {
                Button(onClick = {
                    onConfirmValue(value)
                    pendingOverflowValue = null
                    onDismiss()
                }) { Text("Aplicar (${Truco.WINNING_SCORE})") }
            },
            dismissButton = {
                TextButton(onClick = { pendingOverflowValue = null }) { Text("Cancelar") }
            },
        )
    }
}

private fun plural(value: Int): String =
    if (value == 1) "Adicionar um tento" else "Adicionar $value tentos"

private fun labelForValues(): String =
    "Mão normal +1  •  Truco +3  •  Seis +6  •  Nove +9  •  Doze +12"
