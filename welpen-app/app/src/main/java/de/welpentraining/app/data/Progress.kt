package de.welpentraining.app.data

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

enum class Level(val label: String, val hint: String) {
    NEW("Noch nicht trainiert", "Starte mit kurzen Einheiten in ruhiger Umgebung."),
    LEARNING("Lernphase", "Bleib dran – kleine Schritte und viel Belohnung."),
    PARTIAL("Klappt teilweise", "Gute Fortschritte! Übe weiter ohne Ablenkung."),
    ALMOST("Fast geschafft", "Jetzt langsam Ablenkung und neue Orte einbauen."),
    SOLID("Sitzt!", "Gefestigt. Gelegentlich wiederholen und weiter belohnen."),
}

/** Erfolgsquote einer einzelnen Einheit für ein Kommando. */
data class SessionPoint(val date: LocalDate, val rate: Float, val attempts: Int)

data class CommandProgress(
    val command: Command,
    val sessions: Int,
    val attempts: Int,
    val successes: Int,
    /** Erfolgsquote der letzten 5 Einheiten (0..1). */
    val recentRate: Float,
    val lastTrained: LocalDate?,
    val history: List<SessionPoint>,
    val level: Level,
) {
    val overallRate: Float get() = if (attempts == 0) 0f else successes.toFloat() / attempts

    /** Grober Fortschrittswert 0..1 für Balken: Quote gewichtet mit Übungsmenge. */
    val score: Float
        get() = if (sessions == 0) 0f else recentRate * (sessions.coerceAtMost(SOLID_SESSIONS).toFloat() / SOLID_SESSIONS)

    companion object {
        const val SOLID_SESSIONS = 5
    }
}

object Progress {

    fun forCommand(command: Command, logs: List<SessionLog>): CommandProgress {
        val points = logs
            .sortedBy { it.timestamp }
            .mapNotNull { log ->
                val entries = log.entries.filter { it.commandId == command.id && it.attempts > 0 }
                if (entries.isEmpty()) return@mapNotNull null
                val a = entries.sumOf { it.attempts }
                val s = entries.sumOf { it.successes }
                SessionPoint(log.date, s.toFloat() / a, a)
            }
        val attempts = points.sumOf { it.attempts }
        val successes = logs.sumOf { l -> l.entries.filter { it.commandId == command.id }.sumOf { it.successes } }
        val recent = points.takeLast(5)
        val recentRate = if (recent.isEmpty()) 0f else {
            val a = recent.sumOf { it.attempts }
            recent.sumOf { (it.rate * it.attempts).toDouble() }.toFloat() / a
        }
        val level = when {
            points.isEmpty() -> Level.NEW
            recentRate < 0.5f -> Level.LEARNING
            recentRate < 0.8f -> Level.PARTIAL
            points.size < CommandProgress.SOLID_SESSIONS || recentRate < 0.9f -> Level.ALMOST
            else -> Level.SOLID
        }
        return CommandProgress(
            command = command,
            sessions = points.size,
            attempts = attempts,
            successes = successes,
            recentRate = recentRate,
            lastTrained = points.lastOrNull()?.date,
            history = points,
            level = level,
        )
    }

    fun all(data: AppData): List<CommandProgress> = data.commands.map { forCommand(it, data.logs) }

    /** Anzahl aufeinanderfolgender Tage mit mindestens einer Einheit (bis heute bzw. gestern). */
    fun streak(logs: List<SessionLog>, today: LocalDate = LocalDate.now()): Int {
        val days = logs.map { it.date }.toSet()
        var day = if (today in days) today else today.minusDays(1)
        var count = 0
        while (day in days) {
            count++
            day = day.minusDays(1)
        }
        return count
    }

    fun sessionsThisWeek(logs: List<SessionLog>, today: LocalDate = LocalDate.now()): Int {
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return logs.count { !it.date.isBefore(monday) && !it.date.isAfter(today) }
    }
}
