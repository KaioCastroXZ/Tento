package io.github.kaiocastroxz.tento.engine

import io.github.kaiocastroxz.tento.model.ScoreEvent
import io.github.kaiocastroxz.tento.model.ScoreState
import io.github.kaiocastroxz.tento.model.Truco

/**
 * Regras de negócio do contador de tentos. Sem dependências de Android para
 * permitir testes unitários simples.
 *
 * O estado é imutável e cada operação devolve (e guarda) o novo [ScoreState].
 */
class ScoreEngine(initial: ScoreState = ScoreState()) {

    var state: ScoreState = initial
        private set

    /** Substitui todo o estado (usado ao restaurar da persistência). */
    fun load(newState: ScoreState): ScoreState {
        state = newState.sanitized()
        return state
    }

    /**
     * Adiciona (ou remove, se negativo) tentos de um time, respeitando os
     * limites de 0 e 12. Registra no histórico apenas a variação aplicada de fato.
     */
    fun addPoints(team: Int, points: Int, now: Long = System.currentTimeMillis()): ScoreState {
        require(Truco.isValidTeam(team)) { "Time inválido: $team" }
        if (points == 0) return state

        val current = state.scoreOf(team)
        val target = (current + points).coerceIn(0, Truco.WINNING_SCORE)
        val applied = target - current
        if (applied == 0) return state

        val event = ScoreEvent(team = team, points = applied, timestamp = now)
        state = state.copy(
            team1Score = if (team == Truco.TEAM_ONE) target else state.team1Score,
            team2Score = if (team == Truco.TEAM_TWO) target else state.team2Score,
            history = state.history + event,
        )
        return state
    }

    /** Remove um tento do time (nunca abaixo de 0). */
    fun removePoint(team: Int, now: Long = System.currentTimeMillis()): ScoreState =
        addPoints(team, -1, now)

    /** Desfaz a última alteração de pontuação. */
    fun undoLastAction(): ScoreState {
        val last = state.history.lastOrNull() ?: return state
        val restored = (state.scoreOf(last.team) - last.points).coerceIn(0, Truco.WINNING_SCORE)
        state = state.copy(
            team1Score = if (last.team == Truco.TEAM_ONE) restored else state.team1Score,
            team2Score = if (last.team == Truco.TEAM_TWO) restored else state.team2Score,
            history = state.history.dropLast(1),
        )
        return state
    }

    /** Zera placar e histórico, mantendo os nomes das duplas. */
    fun resetGame(): ScoreState {
        state = ScoreState(team1Name = state.team1Name, team2Name = state.team2Name)
        return state
    }

    /** Inicia uma nova partida definindo os nomes das duplas. */
    fun newGame(team1Name: String, team2Name: String): ScoreState {
        state = ScoreState(
            team1Name = team1Name.normalizedName(ScoreState.DEFAULT_TEAM_1),
            team2Name = team2Name.normalizedName(ScoreState.DEFAULT_TEAM_2),
        )
        return state
    }

    /** Atualiza o nome de uma das duplas sem afetar o placar. */
    fun updateTeamName(team: Int, name: String): ScoreState {
        require(Truco.isValidTeam(team)) { "Time inválido: $team" }
        val fallback = if (team == Truco.TEAM_ONE) ScoreState.DEFAULT_TEAM_1 else ScoreState.DEFAULT_TEAM_2
        val clean = name.normalizedName(fallback)
        state = if (team == Truco.TEAM_ONE) state.copy(team1Name = clean) else state.copy(team2Name = clean)
        return state
    }

    private fun ScoreState.sanitized(): ScoreState = copy(
        team1Name = team1Name.normalizedName(ScoreState.DEFAULT_TEAM_1),
        team2Name = team2Name.normalizedName(ScoreState.DEFAULT_TEAM_2),
        team1Score = team1Score.coerceIn(0, Truco.WINNING_SCORE),
        team2Score = team2Score.coerceIn(0, Truco.WINNING_SCORE),
    )

    private fun String.normalizedName(fallback: String): String {
        val trimmed = trim().take(MAX_NAME_LENGTH)
        return trimmed.ifBlank { fallback }
    }

    companion object {
        const val MAX_NAME_LENGTH = 24
    }
}
