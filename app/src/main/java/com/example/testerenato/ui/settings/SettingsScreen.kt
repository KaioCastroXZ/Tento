package com.example.testerenato.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.testerenato.model.SettingsState
import com.example.testerenato.model.ThemeMode
import com.example.testerenato.ui.components.BodyText
import com.example.testerenato.ui.components.InfoCard
import com.example.testerenato.ui.components.ReadableColumn
import com.example.testerenato.ui.components.TentoScaffold

@Composable
fun SettingsScreen(
    settings: SettingsState,
    onBack: () -> Unit,
    onThemeMode: (ThemeMode) -> Unit,
    onHaptics: (Boolean) -> Unit,
    onSound: (Boolean) -> Unit,
    onResetData: () -> Unit,
) {
    var showResetData by remember { mutableStateOf(false) }

    TentoScaffold(title = "Configurações", onBack = onBack) { padding ->
        ReadableColumn(padding) {
            InfoCard("Tema") {
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    val options = listOf(
                        ThemeMode.SYSTEM to "Sistema",
                        ThemeMode.LIGHT to "Claro",
                        ThemeMode.DARK to "Escuro",
                    )
                    options.forEachIndexed { index, (mode, label) ->
                        SegmentedButton(
                            selected = settings.themeMode == mode,
                            onClick = { onThemeMode(mode) },
                            shape = SegmentedButtonDefaults.itemShape(index, options.size),
                        ) { Text(label) }
                    }
                }
            }

            InfoCard("Feedback") {
                ToggleRow(
                    title = "Vibração",
                    description = "Vibra levemente ao marcar tentos.",
                    checked = settings.hapticsEnabled,
                    onCheckedChange = onHaptics,
                )
                ToggleRow(
                    title = "Sons",
                    description = "Toca um clique curto ao marcar tentos.",
                    checked = settings.soundEnabled,
                    onCheckedChange = onSound,
                )
            }

            InfoCard("Dados") {
                BodyText("Apaga o placar atual, o histórico e as preferências.")
                Button(
                    onClick = { showResetData = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                ) { Text("Resetar dados") }
            }

            InfoCard("Sobre") {
                BodyText("Tento — Placar de Truco")
                BodyText("Versão 1.0")
                BodyText(
                    "Um contador de tentos para partidas presenciais de Truco Paulista. " +
                        "Funciona offline, sem cadastro e sem anúncios.",
                )
            }
        }
    }

    if (showResetData) {
        AlertDialog(
            onDismissRequest = { showResetData = false },
            title = { Text("Resetar dados?") },
            text = { Text("Placar, histórico e preferências voltam ao padrão. Não dá para desfazer.") },
            confirmButton = {
                Button(onClick = {
                    showResetData = false
                    onResetData()
                }) { Text("Resetar") }
            },
            dismissButton = {
                TextButton(onClick = { showResetData = false }) { Text("Cancelar") }
            },
        )
    }
}

@Composable
private fun ToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "$title, ${if (checked) "ligado" else "desligado"}"
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                description,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
