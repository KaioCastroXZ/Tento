package io.github.kaiocastroxz.tento.model

/**
 * Resultado de uma partida (até 12 tentos) já encerrada dentro de uma série.
 *
 * @param team1Score tentos finais da dupla 1
 * @param team2Score tentos finais da dupla 2
 * @param winner     dupla vencedora da partida ([Truco.TEAM_ONE] ou [Truco.TEAM_TWO])
 * @param timestamp  momento em que a partida terminou (epoch millis)
 */
data class GameResult(
    val team1Score: Int,
    val team2Score: Int,
    val winner: Int,
    val timestamp: Long,
) {
    fun scoreOf(team: Int): Int = if (team == Truco.TEAM_ONE) team1Score else team2Score
}
