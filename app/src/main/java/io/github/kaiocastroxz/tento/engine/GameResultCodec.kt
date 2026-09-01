package io.github.kaiocastroxz.tento.engine

import io.github.kaiocastroxz.tento.model.GameResult

/**
 * Serialização simples da lista de partidas encerradas para uma única String.
 * Formato: `s1,s2,winner,timestamp` separados por `|`.
 */
object GameResultCodec {

    fun encode(games: List<GameResult>): String =
        games.joinToString(separator = "|") {
            "${it.team1Score},${it.team2Score},${it.winner},${it.timestamp}"
        }

    fun decode(raw: String?): List<GameResult> {
        if (raw.isNullOrBlank()) return emptyList()
        return raw.split("|").mapNotNull { chunk ->
            val parts = chunk.split(",")
            if (parts.size != 4) return@mapNotNull null
            val s1 = parts[0].toIntOrNull() ?: return@mapNotNull null
            val s2 = parts[1].toIntOrNull() ?: return@mapNotNull null
            val winner = parts[2].toIntOrNull() ?: return@mapNotNull null
            val ts = parts[3].toLongOrNull() ?: return@mapNotNull null
            GameResult(s1, s2, winner, ts)
        }
    }
}
