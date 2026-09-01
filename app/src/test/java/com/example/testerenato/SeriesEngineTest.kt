package com.example.testerenato

import com.example.testerenato.engine.SeriesEngine
import com.example.testerenato.model.GameResult
import com.example.testerenato.model.ScoreState
import com.example.testerenato.model.SeriesState
import com.example.testerenato.model.Truco
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SeriesEngineTest {

    private lateinit var engine: SeriesEngine
    private var clock = 0L
    private fun now() = ++clock

    @Before
    fun setUp() {
        clock = 0L
        engine = SeriesEngine(SeriesState(gamesToWin = 3))
    }

    /** Leva um time a 12 tentos (a partida encerra mas continua à vista). */
    private fun winGame(team: Int) {
        repeat(4) { engine.addPoints(team, 3, now()) }
    }

    /** Vence a partida e já avança para a próxima. */
    private fun winGameAndAdvance(team: Int) {
        winGame(team)
        engine.startNextGame()
    }

    @Test
    fun `serie comeca zerada`() {
        assertEquals(0, engine.state.team1Games)
        assertEquals(0, engine.state.team2Games)
        assertTrue(engine.state.completedGames.isEmpty())
        assertNull(engine.state.seriesWinner)
        assertEquals(1, engine.state.currentGameNumber)
    }

    @Test
    fun `partida encerrada e registrada mas continua a vista ate avancar`() {
        winGame(Truco.TEAM_ONE)
        assertEquals(1, engine.state.completedGames.size)
        assertEquals(12, engine.state.completedGames.first().team1Score)
        assertEquals(Truco.TEAM_ONE, engine.state.completedGames.first().winner)
        // 12 x 0 continua à vista (diálogo e placar mostram o mesmo número)
        assertTrue(engine.state.currentGame.isFinished)
        assertEquals(12, engine.state.boardScoreOf(Truco.TEAM_ONE))
        assertEquals(1, engine.state.team1Games)

        engine.startNextGame()
        assertEquals(0, engine.state.currentGame.team1Score)
        assertEquals(0, engine.state.currentGame.team2Score)
        assertEquals(2, engine.state.currentGameNumber)
        assertFalse(engine.state.isSeriesFinished)
    }

    @Test
    fun `startNextGame nao faz nada com partida em andamento`() {
        engine.addPoints(Truco.TEAM_ONE, 3, now())
        engine.startNextGame()
        assertEquals(3, engine.state.currentGame.team1Score)
    }

    @Test
    fun `pontos ignorados enquanto a partida encerrada aguarda avancar`() {
        winGame(Truco.TEAM_ONE)
        engine.addPoints(Truco.TEAM_TWO, 3, now())
        assertEquals(0, engine.state.currentGame.team2Score)
        assertEquals(1, engine.state.completedGames.size)
    }

    @Test
    fun `serie termina quando um time vence gamesToWin partidas`() {
        winGameAndAdvance(Truco.TEAM_ONE)
        winGameAndAdvance(Truco.TEAM_TWO)
        winGameAndAdvance(Truco.TEAM_ONE)
        assertNull(engine.state.seriesWinner)
        winGame(Truco.TEAM_ONE)
        assertEquals(Truco.TEAM_ONE, engine.state.seriesWinner)
        assertTrue(engine.state.isSeriesFinished)
        assertEquals(3, engine.state.team1Games)
        assertEquals(1, engine.state.team2Games)
        assertEquals(12, engine.state.boardScoreOf(Truco.TEAM_ONE))
        assertEquals(12, engine.state.lastGame?.team1Score)
    }

    @Test
    fun `pontos sao ignorados depois da serie encerrada`() {
        engine = SeriesEngine(SeriesState(gamesToWin = 1))
        winGame(Truco.TEAM_ONE)
        assertTrue(engine.state.isSeriesFinished)
        val before = engine.state
        engine.addPoints(Truco.TEAM_TWO, 3, now())
        assertEquals(before, engine.state)
    }

    @Test
    fun `partida unica termina a serie`() {
        engine = SeriesEngine(SeriesState(gamesToWin = 1))
        winGame(Truco.TEAM_TWO)
        assertEquals(Truco.TEAM_TWO, engine.state.seriesWinner)
        assertEquals(1, engine.state.completedGames.size)
    }

    @Test
    fun `desfazer dentro da partida atual`() {
        engine.addPoints(Truco.TEAM_ONE, 3, now())
        engine.addPoints(Truco.TEAM_TWO, 1, now())
        engine.undoLastAction()
        assertEquals(3, engine.state.currentGame.team1Score)
        assertEquals(0, engine.state.currentGame.team2Score)
    }

    @Test
    fun `desfazer partida encerrada a vista tira o tento e o registro`() {
        engine.addPoints(Truco.TEAM_ONE, 9, now())
        engine.addPoints(Truco.TEAM_ONE, 3, now()) // 9 -> 12, encerra
        assertEquals(1, engine.state.completedGames.size)
        assertTrue(engine.state.currentGame.isFinished)

        engine.undoLastAction()
        assertEquals(0, engine.state.completedGames.size)
        assertEquals(9, engine.state.currentGame.team1Score)
        assertFalse(engine.state.currentGame.isFinished)
        assertFalse(engine.state.isSeriesFinished)
    }

    @Test
    fun `desfazer depois de avancar reabre a ultima partida encerrada`() {
        engine.addPoints(Truco.TEAM_ONE, 9, now())
        engine.addPoints(Truco.TEAM_ONE, 3, now())
        engine.startNextGame()
        assertEquals(0, engine.state.currentGame.team1Score)

        engine.undoLastAction()
        assertEquals(0, engine.state.completedGames.size)
        assertEquals(12, engine.state.currentGame.team1Score)
        assertFalse(engine.state.isSeriesFinished)

        engine.removePoint(Truco.TEAM_ONE, now())
        assertEquals(11, engine.state.currentGame.team1Score)
    }

    @Test
    fun `desfazer a partida decisiva des-encerra a serie`() {
        winGameAndAdvance(Truco.TEAM_ONE)
        winGameAndAdvance(Truco.TEAM_ONE)
        winGame(Truco.TEAM_ONE) // decisiva, à vista
        assertTrue(engine.state.isSeriesFinished)

        engine.undoLastAction()
        assertFalse(engine.state.isSeriesFinished)
        assertEquals(2, engine.state.team1Games)
        assertEquals(9, engine.state.currentGame.team1Score)
    }

    @Test
    fun `resetar serie mantem nomes e formato`() {
        engine.updateTeamName(Truco.TEAM_ONE, "João & Pedro")
        winGame(Truco.TEAM_ONE)
        engine.resetSeries()
        assertEquals(0, engine.state.completedGames.size)
        assertEquals(0, engine.state.currentGame.team1Score)
        assertEquals("João & Pedro", engine.state.team1Name)
        assertEquals(3, engine.state.gamesToWin)
    }

    @Test
    fun `nova serie define nomes e formato`() {
        winGame(Truco.TEAM_ONE)
        engine.newSeries("A", "B", 2)
        assertEquals("A", engine.state.team1Name)
        assertEquals("B", engine.state.team2Name)
        assertEquals(2, engine.state.gamesToWin)
        assertTrue(engine.state.completedGames.isEmpty())
    }

    @Test
    fun `load sanea gamesToWin`() {
        engine.load(SeriesState(gamesToWin = 99))
        assertEquals(SeriesState.MAX_GAMES_TO_WIN, engine.state.gamesToWin)
        engine.load(SeriesState(gamesToWin = 0))
        assertEquals(1, engine.state.gamesToWin)
    }

    @Test
    fun `load restaura serie completa (salvar e recuperar)`() {
        val saved = SeriesState(
            gamesToWin = 2,
            currentGame = ScoreState(team1Name = "Nós", team2Name = "Eles", team1Score = 7),
            completedGames = listOf(GameResult(12, 5, Truco.TEAM_ONE, 100L)),
        )
        val e = SeriesEngine()
        e.load(saved)
        assertEquals(1, e.state.team1Games)
        assertEquals(7, e.state.currentGame.team1Score)
        assertEquals("Nós", e.state.team1Name)
        // uma vitória a mais fecha a série (gamesToWin = 2)
        repeat(4) { e.addPoints(Truco.TEAM_ONE, 3, it.toLong()) }
        assertEquals(Truco.TEAM_ONE, e.state.seriesWinner)
    }

    @Test
    fun `alterar nome durante a serie nao afeta placar`() {
        engine.addPoints(Truco.TEAM_ONE, 6, now())
        engine.updateTeamName(Truco.TEAM_ONE, "  Nova  ")
        assertEquals("Nova", engine.state.team1Name)
        assertEquals(6, engine.state.currentGame.team1Score)
    }
}
