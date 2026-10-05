package de.welpentraining.app.data

import org.json.JSONArray
import org.json.JSONObject
import java.time.DayOfWeek
import java.time.LocalDate

/** Speichert die App-Daten als JSON (org.json ist in Android enthalten, keine Zusatzbibliothek nötig). */
object JsonCodec {

    private const val VERSION = 1

    fun encode(data: AppData): String = JSONObject().apply {
        put("version", VERSION)
        put("profile", JSONObject().apply {
            put("name", data.profile.name)
            put("breed", data.profile.breed)
            put("birthDate", data.profile.birthDate?.toString() ?: JSONObject.NULL)
        })
        put("commands", JSONArray().apply {
            data.commands.forEach { c ->
                put(JSONObject().apply {
                    put("id", c.id)
                    put("name", c.name)
                    put("category", c.category)
                    put("description", c.description)
                    put("steps", JSONArray(c.steps))
                    put("builtIn", c.builtIn)
                })
            }
        })
        put("trainings", JSONArray().apply {
            data.trainings.forEach { t ->
                put(JSONObject().apply {
                    put("id", t.id)
                    put("name", t.name)
                    put("description", t.description)
                    put("durationMinutes", t.durationMinutes)
                    put("items", JSONArray().apply {
                        t.items.forEach { i ->
                            put(JSONObject().put("commandId", i.commandId).put("targetReps", i.targetReps))
                        }
                    })
                    put("weekdays", JSONArray(t.weekdays.map { it.value }.sorted()))
                    put("builtIn", t.builtIn)
                })
            }
        })
        put("logs", JSONArray().apply {
            data.logs.forEach { l ->
                put(JSONObject().apply {
                    put("id", l.id)
                    put("trainingId", l.trainingId ?: JSONObject.NULL)
                    put("trainingName", l.trainingName)
                    put("date", l.date.toString())
                    put("timestamp", l.timestamp)
                    put("entries", JSONArray().apply {
                        l.entries.forEach { e ->
                            put(
                                JSONObject()
                                    .put("commandId", e.commandId)
                                    .put("attempts", e.attempts)
                                    .put("successes", e.successes)
                            )
                        }
                    })
                    put("mood", l.mood)
                    put("note", l.note)
                })
            }
        })
    }.toString()

    fun decode(json: String): AppData {
        val root = JSONObject(json)
        val p = root.optJSONObject("profile")
        val profile = if (p == null) DogProfile() else DogProfile(
            name = p.optString("name"),
            breed = p.optString("breed"),
            birthDate = p.optStringOrNull("birthDate")?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
        )
        val commands = root.optJSONArray("commands").objects().map { c ->
            Command(
                id = c.getString("id"),
                name = c.optString("name"),
                category = c.optString("category"),
                description = c.optString("description"),
                steps = c.optJSONArray("steps").strings(),
                builtIn = c.optBoolean("builtIn"),
            )
        }
        val trainings = root.optJSONArray("trainings").objects().map { t ->
            Training(
                id = t.getString("id"),
                name = t.optString("name"),
                description = t.optString("description"),
                durationMinutes = t.optInt("durationMinutes", 10),
                items = t.optJSONArray("items").objects().map {
                    TrainingItem(it.getString("commandId"), it.optInt("targetReps", 5))
                },
                weekdays = t.optJSONArray("weekdays").ints().mapNotNull { v ->
                    if (v in 1..7) DayOfWeek.of(v) else null
                }.toSet(),
                builtIn = t.optBoolean("builtIn"),
            )
        }
        val logs = root.optJSONArray("logs").objects().mapNotNull { l ->
            val date = runCatching { LocalDate.parse(l.getString("date")) }.getOrNull() ?: return@mapNotNull null
            SessionLog(
                id = l.getString("id"),
                trainingId = l.optStringOrNull("trainingId"),
                trainingName = l.optString("trainingName"),
                date = date,
                timestamp = l.optLong("timestamp"),
                entries = l.optJSONArray("entries").objects().map {
                    LogEntry(it.getString("commandId"), it.optInt("attempts"), it.optInt("successes"))
                },
                mood = l.optInt("mood"),
                note = l.optString("note"),
            )
        }
        return AppData(profile, commands, trainings, logs)
    }

    private fun JSONObject.optStringOrNull(key: String): String? =
        if (isNull(key)) null else optString(key).takeIf { it.isNotEmpty() }

    private fun JSONArray?.objects(): List<JSONObject> =
        if (this == null) emptyList() else (0 until length()).mapNotNull { optJSONObject(it) }

    private fun JSONArray?.strings(): List<String> =
        if (this == null) emptyList() else (0 until length()).map { optString(it) }

    private fun JSONArray?.ints(): List<Int> =
        if (this == null) emptyList() else (0 until length()).map { optInt(it) }
}
