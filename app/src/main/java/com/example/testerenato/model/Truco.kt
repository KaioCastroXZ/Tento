package com.example.testerenato.model

/**
 * Constantes de regra do Truco Paulista usadas pelo placar.
 *
 * O app não simula o jogo de cartas: ele apenas conta os tentos.
 */
object Truco {
    /** Pontuação que encerra a partida. */
    const val WINNING_SCORE = 12

    /** Sequência de valores possíveis para uma mão: 1 → 3 → 6 → 9 → 12. */
    val HAND_VALUES = listOf(1, 3, 6, 9, 12)

    const val TEAM_ONE = 1
    const val TEAM_TWO = 2

    fun isValidTeam(team: Int): Boolean = team == TEAM_ONE || team == TEAM_TWO
}
