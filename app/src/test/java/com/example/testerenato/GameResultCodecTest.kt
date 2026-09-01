package com.example.testerenato

import com.example.testerenato.engine.GameResultCodec
import com.example.testerenato.model.GameResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameResultCodecTest {

    @Test
    fun `lista vazia`() {
        assertEquals("", GameResultCodec.encode(emptyList()))
        assertTrue(GameResultCodec.decode("").isEmpty())
        assertTrue(GameResultCodec.decode(null).isEmpty())
    }

    @Test
    fun `round trip preserva partidas`() {
        val games = listOf(
            GameResult(12, 8, 1, 1000L),
            GameResult(5, 12, 2, 2000L),
            GameResult(12, 0, 1, 3000L),
        )
        assertEquals(games, GameResultCodec.decode(GameResultCodec.encode(games)))
    }

    @Test
    fun `ignora pedacos malformados`() {
        val decoded = GameResultCodec.decode("12,8,1,1000|lixo|12,x,2,2000|5,12,2,4000")
        assertEquals(2, decoded.size)
        assertEquals(4000L, decoded.last().timestamp)
    }
}
