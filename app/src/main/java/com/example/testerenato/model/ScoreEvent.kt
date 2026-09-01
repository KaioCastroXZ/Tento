package com.example.testerenato.model

/**
 * Registro de uma alteração de pontuação no histórico da partida.
 *
 * @param team    time afetado ([Truco.TEAM_ONE] ou [Truco.TEAM_TWO]).
 * @param points  variação aplicada de fato ao placar (positiva ao adicionar,
 *                negativa ao remover). Já considera os limites de 0 e 12.
 * @param timestamp momento em que o evento ocorreu (epoch millis).
 */
data class ScoreEvent(
    val team: Int,
    val points: Int,
    val timestamp: Long,
)
