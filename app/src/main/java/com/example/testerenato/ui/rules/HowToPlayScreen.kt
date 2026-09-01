package com.example.testerenato.ui.rules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.testerenato.ui.components.BodyText
import com.example.testerenato.ui.components.InfoCard
import com.example.testerenato.ui.components.ReadableColumn
import com.example.testerenato.ui.components.TentoScaffold

@Composable
fun HowToPlayScreen(
    onBack: () -> Unit,
    onOpenFullRules: () -> Unit,
) {
    TentoScaffold(title = "Como jogar", onBack = onBack) { padding ->
        ReadableColumn(padding) {
            InfoCard("O essencial") {
                BodyText("A partida vai até 12 tentos.")
                ValueRow("Mão normal", "1")
                ValueRow("Truco", "3")
                ValueRow("Seis", "6")
                ValueRow("Nove", "9")
                ValueRow("Doze", "12")
            }
            InfoCard("Série (melhor de N)") {
                BodyText(
                    "Ao criar uma partida você escolhe quantas partidas vencem a série " +
                        "(1, 2 ou 3). Ex.: escolhendo 3, vence a série a primeira dupla a " +
                        "ganhar 3 partidas de 12 tentos.",
                )
                BodyText(
                    "Cada partida encerrada fica registrada no histórico e o placar de " +
                        "partidas aparece embaixo do nome de cada dupla.",
                )
            }
            InfoCard("Marcando no app") {
                BodyText("Toque no + de uma dupla para somar 1 tento.")
                BodyText("Toque no botão TRUCO para somar 3 de uma vez.")
                BodyText("Toque no número (ou segure o +) para escolher +1, +3, +6, +9 ou +12.")
                BodyText("Errou? Use \"Desfazer\" no histórico — ele até reabre a última partida encerrada.")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onOpenFullRules) {
                    Text("Ver regras completas")
                }
            }
        }
    }
}

@Composable
private fun ValueRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
