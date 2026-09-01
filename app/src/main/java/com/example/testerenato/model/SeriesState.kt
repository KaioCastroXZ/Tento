package com.example.testerenato.model

/**
 * Estado de uma **série** (o "jogo" completo): um conjunto de partidas em que
 * vence a dupla que ganhar primeiro [gamesToWin] partidas.
 *
 * - Cada partida vai até 12 tentos ([currentGame]).
 * - Partidas encerradas ficam registradas em [completedGames].
 * - `gamesToWin == 1` representa uma partida única (sem série).
 */
data class SeriesState(
    val gamesToWin: Int = DEFAULT_GAMES_TO_WIN,
    val currentGame: ScoreState = ScoreState(),
    val completedGames: List<GameResult> = emptyList(),
) {
    val team1Name: String get() = currentGame.team1Name
    val team2Name: String get() = currentGame.team2Name

    val team1Games: Int get() = completedGames.count { it.winner == Truco.TEAM_ONE }
    val team2Games: Int get() = completedGames.count { it.winner == Truco.TEAM_TWO }

    /** Dupla que venceu a série (1 ou 2), ou `null` se ainda está em disputa. */
    val seriesWinner: Int? = run {
        val g1 = completedGames.count { it.winner == Truco.TEAM_ONE }
        val g2 = completedGames.count { it.winner == Truco.TEAM_TWO }
        when {
            g1 >= gamesToWin -> Truco.TEAM_ONE
            g2 >= gamesToWin -> Truco.TEAM_TWO
            else -> null
        }
    }

    val isSeriesFinished: Boolean = seriesWinner != null

    /** `true` quando a série é de uma única partida. */
    val isSingleGame: Boolean get() = gamesToWin <= 1

    /** Número da partida atual (1-based). */
    val currentGameNumber: Int
        get() = if (currentGame.isFinished) completedGames.size.coerceAtLeast(1)
        else completedGames.size + 1

    val hasProgress: Boolean
        get() = completedGames.isNotEmpty() || currentGame.hasProgress

    fun gamesOf(team: Int): Int = if (team == Truco.TEAM_ONE) team1Games else team2Games

    fun nameOf(team: Int): String = currentGame.nameOf(team)

    /**
     * Tentos a exibir no placar. A partida atual sempre carrega o placar "vivo"
     * (ou o placar final 12 × N enquanto o diálogo de fim de partida está aberto).
     */
    fun boardScoreOf(team: Int): Int = currentGame.scoreOf(team)

    /** Placar da última partida encerrada (`null` se nenhuma terminou ainda). */
    val lastGame: GameResult? get() = completedGames.lastOrNull()

    companion object {
        const val DEFAULT_GAMES_TO_WIN = 3
        const val MAX_GAMES_TO_WIN = 3
        val GAMES_TO_WIN_OPTIONS = listOf(1, 2, 3)
    }
}
