package com.example.testerenato.model

/**
 * Estado completo de uma partida de placar.
 *
 * É imutável: toda alteração produz uma nova cópia (ver [com.example.testerenato.engine.ScoreEngine]).
 */
data class ScoreState(
    val team1Name: String = DEFAULT_TEAM_1,
    val team2Name: String = DEFAULT_TEAM_2,
    val team1Score: Int = 0,
    val team2Score: Int = 0,
    val history: List<ScoreEvent> = emptyList(),
) {
    /** Time vencedor (1 ou 2) ou `null` se a partida ainda está em andamento. */
    val winner: Int? = when {
        team1Score >= Truco.WINNING_SCORE -> Truco.TEAM_ONE
        team2Score >= Truco.WINNING_SCORE -> Truco.TEAM_TWO
        else -> null
    }

    /** `true` quando algum time alcançou 12 tentos. */
    val isFinished: Boolean = winner != null

    /** `true` quando a partida já teve alguma pontuação registrada. */
    val hasProgress: Boolean = team1Score > 0 || team2Score > 0 || history.isNotEmpty()

    fun scoreOf(team: Int): Int = if (team == Truco.TEAM_ONE) team1Score else team2Score

    fun nameOf(team: Int): String = if (team == Truco.TEAM_ONE) team1Name else team2Name

    companion object {
        const val DEFAULT_TEAM_1 = "Nossa Dupla"
        const val DEFAULT_TEAM_2 = "Adversários"
    }
}
