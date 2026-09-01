package io.github.kaiocastroxz.tento

import io.github.kaiocastroxz.tento.engine.ScoreEngine
import io.github.kaiocastroxz.tento.model.ScoreState
import io.github.kaiocastroxz.tento.model.Truco
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ScoreEngineTest {

    private lateinit var engine: ScoreEngine
    private var clock = 0L

    private fun now(): Long = ++clock

    @Before
    fun setUp() {
        engine = ScoreEngine()
        clock = 0L
    }

    @Test
    fun `comeca em zero a zero`() {
        assertEquals(0, engine.state.team1Score)
        assertEquals(0, engine.state.team2Score)
        assertTrue(engine.state.history.isEmpty())
        assertNull(engine.state.winner)
    }

    @Test
    fun `adicionar 1 tento`() {
        engine.addPoints(Truco.TEAM_ONE, 1, now())
        assertEquals(1, engine.state.team1Score)
        assertEquals(1, engine.state.history.size)
        assertEquals(1, engine.state.history.first().points)
    }

    @Test
    fun `adicionar 3 tentos (truco)`() {
        engine.addPoints(Truco.TEAM_TWO, 3, now())
        assertEquals(3, engine.state.team2Score)
    }

    @Test
    fun `adicionar 6 tentos`() {
        engine.addPoints(Truco.TEAM_ONE, 6, now())
        assertEquals(6, engine.state.team1Score)
    }

    @Test
    fun `adicionar 9 tentos`() {
        engine.addPoints(Truco.TEAM_ONE, 9, now())
        assertEquals(9, engine.state.team1Score)
    }

    @Test
    fun `exemplo do enunciado 4x2 mais 3 vira 7x2`() {
        engine.load(ScoreState(team1Score = 4, team2Score = 2))
        engine.addPoints(Truco.TEAM_ONE, 3, now())
        assertEquals(7, engine.state.team1Score)
        assertEquals(2, engine.state.team2Score)
    }

    @Test
    fun `nao ultrapassa 12 e registra apenas a variacao aplicada`() {
        engine.load(ScoreState(team1Score = 10))
        engine.addPoints(Truco.TEAM_ONE, 3, now())
        assertEquals(12, engine.state.team1Score)
        assertEquals(2, engine.state.history.last().points)
    }

    @Test
    fun `nao fica abaixo de zero`() {
        engine.removePoint(Truco.TEAM_ONE, now())
        assertEquals(0, engine.state.team1Score)
        assertTrue("nada aplicado nao gera evento", engine.state.history.isEmpty())
    }

    @Test
    fun `remover um tento`() {
        engine.load(ScoreState(team1Score = 5))
        engine.removePoint(Truco.TEAM_ONE, now())
        assertEquals(4, engine.state.team1Score)
        assertEquals(-1, engine.state.history.last().points)
    }

    @Test
    fun `desfazer ultima acao`() {
        engine.addPoints(Truco.TEAM_ONE, 3, now())
        engine.addPoints(Truco.TEAM_TWO, 1, now())
        engine.undoLastAction()
        assertEquals(3, engine.state.team1Score)
        assertEquals(0, engine.state.team2Score)
        assertEquals(1, engine.state.history.size)
    }

    @Test
    fun `desfazer sem historico nao quebra`() {
        engine.undoLastAction()
        assertEquals(0, engine.state.team1Score)
    }

    @Test
    fun `desfazer restaura pontuacao limitada`() {
        engine.load(ScoreState(team1Score = 10))
        engine.addPoints(Truco.TEAM_ONE, 6, now()) // aplica só +2 -> 12
        engine.undoLastAction()
        assertEquals(10, engine.state.team1Score)
        assertTrue(engine.state.history.isEmpty())
    }

    @Test
    fun `resetar partida zera placar e historico mantendo nomes`() {
        engine.updateTeamName(Truco.TEAM_ONE, "João & Pedro")
        engine.addPoints(Truco.TEAM_ONE, 6, now())
        engine.resetGame()
        assertEquals(0, engine.state.team1Score)
        assertTrue(engine.state.history.isEmpty())
        assertEquals("João & Pedro", engine.state.team1Name)
    }

    @Test
    fun `detecta vencedor ao chegar em 12`() {
        engine.load(ScoreState(team1Score = 9))
        assertNull(engine.state.winner)
        engine.addPoints(Truco.TEAM_ONE, 3, now())
        assertEquals(Truco.TEAM_ONE, engine.state.winner)
        assertTrue(engine.state.isFinished)
    }

    @Test
    fun `alterar nome das equipes`() {
        engine.updateTeamName(Truco.TEAM_ONE, "  Nós  ")
        engine.updateTeamName(Truco.TEAM_TWO, "Eles")
        assertEquals("Nós", engine.state.team1Name)
        assertEquals("Eles", engine.state.team2Name)
    }

    @Test
    fun `nome vazio volta ao padrao`() {
        engine.updateTeamName(Truco.TEAM_ONE, "   ")
        assertEquals(ScoreState.DEFAULT_TEAM_1, engine.state.team1Name)
    }

    @Test
    fun `nova partida define nomes e zera`() {
        engine.addPoints(Truco.TEAM_ONE, 6, now())
        engine.newGame("A", "B")
        assertEquals("A", engine.state.team1Name)
        assertEquals("B", engine.state.team2Name)
        assertEquals(0, engine.state.team1Score)
        assertFalse(engine.state.isFinished)
    }

    @Test
    fun `load restaura estado completo (salvar e recuperar)`() {
        val saved = ScoreState(
            team1Name = "Carlos & Lucas",
            team2Name = "Ana & Bia",
            team1Score = 7,
            team2Score = 11,
        )
        val restoredEngine = ScoreEngine()
        restoredEngine.load(saved)
        assertEquals(7, restoredEngine.state.team1Score)
        assertEquals(11, restoredEngine.state.team2Score)
        assertEquals("Carlos & Lucas", restoredEngine.state.team1Name)
        // continua marcando a partir do estado restaurado
        restoredEngine.addPoints(Truco.TEAM_TWO, 1, now())
        assertEquals(Truco.TEAM_TWO, restoredEngine.state.winner)
    }

    @Test
    fun `load sanea valores invalidos`() {
        val engine = ScoreEngine()
        engine.load(ScoreState(team1Score = 99, team2Score = -3, team1Name = ""))
        assertEquals(Truco.WINNING_SCORE, engine.state.team1Score)
        assertEquals(0, engine.state.team2Score)
        assertEquals(ScoreState.DEFAULT_TEAM_1, engine.state.team1Name)
    }
}
