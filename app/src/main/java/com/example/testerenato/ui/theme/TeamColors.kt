package com.example.testerenato.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.example.testerenato.model.Truco

/**
 * Cores de uma dupla no placar. Separadas em três papéis porque a mesma cor de
 * marca não serve para tudo:
 *
 * - [accent]  — preenchimentos e traços (friso, botão +, barra de progresso,
 *               bolinhas, badge "+N"). Cor de marca saturada.
 * - [onAccent] — o que fica *sobre* [accent] (ícone do +, texto do badge, "check"
 *               da bolinha). Alto contraste com [accent].
 * - [onCard]  — a cor de marca usada como *texto sobre o card* (nome da dupla,
 *               texto do botão TRUCO, aviso de "quase 12"). No tema claro precisa
 *               ser mais escura para passar contraste AA no marfim.
 */
data class TeamColors(
    val accent: Color,
    val onAccent: Color,
    val onCard: Color,
)

@Composable
@ReadOnlyComposable
fun teamColors(team: Int): TeamColors {
    val dark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    return if (team == Truco.TEAM_ONE) {
        TeamColors(
            accent = Gold,
            onAccent = InkGreen,
            onCard = if (dark) Gold else GoldInkLight,
        )
    } else {
        TeamColors(
            accent = if (dark) TrucoRedBright else TrucoRed,
            onAccent = OffWhite,
            onCard = if (dark) TrucoRedBright else Color(0xFF8A2820),
        )
    }
}
