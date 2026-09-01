package io.github.kaiocastroxz.tento

import io.github.kaiocastroxz.tento.engine.ScoreHistoryCodec
import io.github.kaiocastroxz.tento.model.ScoreEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScoreHistoryCodecTest {

    @Test
    fun `lista vazia`() {
        assertEquals("", ScoreHistoryCodec.encode(emptyList()))
        assertTrue(ScoreHistoryCodec.decode("").isEmpty())
        assertTrue(ScoreHistoryCodec.decode(null).isEmpty())
    }

    @Test
    fun `round trip preserva eventos`() {
        val events = listOf(
            ScoreEvent(team = 1, points = 1, timestamp = 1000L),
            ScoreEvent(team = 2, points = 3, timestamp = 2000L),
            ScoreEvent(team = 1, points = -1, timestamp = 3000L),
        )
        val decoded = ScoreHistoryCodec.decode(ScoreHistoryCodec.encode(events))
        assertEquals(events, decoded)
    }

    @Test
    fun `ignora pedacos malformados`() {
        val decoded = ScoreHistoryCodec.decode("1,1,1000|lixo|2,3,x|2,3,4000")
        assertEquals(2, decoded.size)
        assertEquals(4000L, decoded.last().timestamp)
    }
}
