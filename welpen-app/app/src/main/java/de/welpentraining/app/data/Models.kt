package de.welpentraining.app.data

import java.time.DayOfWeek
import java.time.LocalDate

/** Ein Kommando bzw. eine Übung, z. B. „Sitz“ oder „Rückruf“. */
data class Command(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    /** Schritt-für-Schritt-Anleitung, eine Zeile pro Schritt. */
    val steps: List<String>,
    val builtIn: Boolean = false,
)

/** Eine Übung innerhalb eines Trainings mit Ziel-Wiederholungen. */
data class TrainingItem(
    val commandId: String,
    val targetReps: Int,
)

/** Ein Trainingsplan (vordefiniert oder selbst gestaltet). */
data class Training(
    val id: String,
    val name: String,
    val description: String,
    val durationMinutes: Int,
    val items: List<TrainingItem>,
    /** Wochentage, an denen das Training als tägliche Einheit erscheint. Leer = nur manuell. */
    val weekdays: Set<DayOfWeek>,
    val builtIn: Boolean = false,
)

/** Ergebnis einer Übung in einer absolvierten Einheit. */
data class LogEntry(
    val commandId: String,
    val attempts: Int,
    val successes: Int,
)

/** Eine absolvierte Trainingseinheit. */
data class SessionLog(
    val id: String,
    /** null bei einer freien Einheit ohne Trainingsplan. */
    val trainingId: String?,
    val trainingName: String,
    val date: LocalDate,
    val timestamp: Long,
    val entries: List<LogEntry>,
    /** Wie lief es insgesamt? 1–5, 0 = keine Angabe. */
    val mood: Int,
    val note: String,
)

/** Steckbrief des Welpen. */
data class DogProfile(
    val name: String = "",
    val breed: String = "",
    val birthDate: LocalDate? = null,
)

data class AppData(
    val profile: DogProfile = DogProfile(),
    val commands: List<Command> = emptyList(),
    val trainings: List<Training> = emptyList(),
    val logs: List<SessionLog> = emptyList(),
) {
    fun command(id: String): Command? = commands.firstOrNull { it.id == id }
    fun training(id: String): Training? = trainings.firstOrNull { it.id == id }
}
