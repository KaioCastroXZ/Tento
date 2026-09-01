package com.example.testerenato.engine

import com.example.testerenato.model.ScoreEvent

/**
 * Serialização simples do histórico para uma única String (sem dependências
 * externas de JSON). Formato: `team,points,timestamp` separados por `|`.
 */
object ScoreHistoryCodec {

    fun encode(events: List<ScoreEvent>): String =
        events.joinToString(separator = "|") { "${it.team},${it.points},${it.timestamp}" }

    fun decode(raw: String?): List<ScoreEvent> {
        if (raw.isNullOrBlank()) return emptyList()
        return raw.split("|").mapNotNull { chunk ->
            val parts = chunk.split(",")
            if (parts.size != 3) return@mapNotNull null
            val team = parts[0].toIntOrNull() ?: return@mapNotNull null
            val points = parts[1].toIntOrNull() ?: return@mapNotNull null
            val timestamp = parts[2].toLongOrNull() ?: return@mapNotNull null
            ScoreEvent(team, points, timestamp)
        }
    }
}
