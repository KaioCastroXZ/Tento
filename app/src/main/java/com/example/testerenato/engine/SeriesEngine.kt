package com.example.testerenato.engine

import com.example.testerenato.model.GameResult
import com.example.testerenato.model.SeriesState
import com.example.testerenato.model.Truco

/**
 * Regras de negócio da **série** (melhor de N partidas). Sem dependências de
 * Android — testável diretamente.
 *
 * Compõe o [ScoreEngine], que cuida de uma partida individual (até 12 tentos).
 *
 * Quando uma partida chega a 12, ela é registrada em `completedGames` mas a
 * "partida atual" **continua mostrando o placar final** (12 × N) até que o
 * usuário toque em "Próxima partida" ([startNextGame]) — assim o diálogo de fim
 * de partida e o placar por baixo dele mostram o mesmo número.
 *
 * "Desfazer" ([undoLastAction]) tira o último tento; se a partida encerrada
 * ainda está à vista, também remove o registro dela (e "des-encerra" a série).
 */
class SeriesEngine(initial: SeriesState = SeriesState()) {

    private val gameEngine = ScoreEngine()

    var state: SeriesState = initial
        private set

    init {
        load(initial)
    }

    fun load(newState: SeriesState): SeriesState {
        val safeGamesToWin = newState.gamesToWin.coerceIn(1, SeriesState.MAX_GAMES_TO_WIN)
        gameEngine.load(newState.currentGame)
        state = newState.copy(
            gamesToWin = safeGamesToWin,
            currentGame = gameEngine.state,
        )
        return state
    }

    fun addPoints(team: Int, points: Int, now: Long = System.currentTimeMillis()): SeriesState {
        // Ignora quando a série acabou ou a partida atual já fechou (aguardando "Próxima partida").
        if (state.isSeriesFinished || state.currentGame.isFinished) return state
        gameEngine.addPoints(team, points, now)
        return afterGameChange(now)
    }

    /**
     * Remove um tento. Funciona mesmo com a partida em 12 — usado para corrigir
     * uma partida reaberta pelo "Desfazer".
     */
    fun removePoint(team: Int, now: Long = System.currentTimeMillis()): SeriesState {
        if (state.isSeriesFinished) return state
        gameEngine.removePoint(team, now)
        return afterGameChange(now)
    }

    fun undoLastAction(now: Long = System.currentTimeMillis()): SeriesState {
        val game = state.currentGame
        val lastCompleted = state.completedGames.lastOrNull()
        val finishedAwaitingAdvance = game.isFinished &&
            lastCompleted != null &&
            lastCompleted.team1Score == game.team1Score &&
            lastCompleted.team2Score == game.team2Score

        when {
            // Partida encerrada e ainda à vista: tira o último tento e apaga o registro.
            finishedAwaitingAdvance -> {
                state = state.copy(completedGames = state.completedGames.dropLast(1))
                gameEngine.undoLastAction()
                state = state.copy(currentGame = gameEngine.state)
            }
            // Partida em andamento com lançamentos.
            game.history.isNotEmpty() -> {
                gameEngine.undoLastAction()
                state = state.copy(currentGame = gameEngine.state)
            }
            // Partida atual zerada, mas há partidas encerradas (já avançou): reabre a última.
            state.completedGames.isNotEmpty() -> {
                val last = state.completedGames.last()
                state = state.copy(completedGames = state.completedGames.dropLast(1))
                gameEngine.load(
                    game.copy(
                        team1Score = last.team1Score,
                        team2Score = last.team2Score,
                        history = emptyList(),
                    ),
                )
                state = state.copy(currentGame = gameEngine.state)
            }
        }
        return state
    }

    /** Confirma a partida encerrada e começa a próxima 0×0. Sem efeito se a série acabou. */
    fun startNextGame(): SeriesState {
        if (!state.currentGame.isFinished || state.isSeriesFinished) return state
        gameEngine.resetGame()
        state = state.copy(currentGame = gameEngine.state)
        return state
    }

    /** Zera a série inteira, mantendo nomes e formato (gamesToWin). */
    fun resetSeries(): SeriesState {
        gameEngine.resetGame()
        state = SeriesState(gamesToWin = state.gamesToWin, currentGame = gameEngine.state)
        return state
    }

    /** Começa uma nova série definindo nomes e quantas partidas vencem. */
    fun newSeries(team1Name: String, team2Name: String, gamesToWin: Int): SeriesState {
        gameEngine.newGame(team1Name, team2Name)
        state = SeriesState(
            gamesToWin = gamesToWin.coerceIn(1, SeriesState.MAX_GAMES_TO_WIN),
            currentGame = gameEngine.state,
        )
        return state
    }

    fun updateTeamName(team: Int, name: String): SeriesState {
        gameEngine.updateTeamName(team, name)
        state = state.copy(currentGame = gameEngine.state)
        return state
    }

    private fun afterGameChange(now: Long): SeriesState {
        val game = gameEngine.state
        if (!game.isFinished) {
            state = state.copy(currentGame = game)
            return state
        }
        // Partida encerrada: registra o resultado, mas mantém 12 × N à vista.
        val result = GameResult(
            team1Score = game.team1Score,
            team2Score = game.team2Score,
            winner = game.winner ?: Truco.TEAM_ONE,
            timestamp = now,
        )
        state = state.copy(
            completedGames = state.completedGames + result,
            currentGame = game,
        )
        return state
    }
}
