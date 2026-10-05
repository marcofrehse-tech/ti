package de.welpentraining.app.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.util.UUID

/**
 * Hält alle Daten im Speicher und schreibt sie bei jeder Änderung als JSON-Datei
 * in den privaten App-Speicher.
 */
class Repository private constructor(context: Context) {

    private val file = File(context.filesDir, "welpentraining.json")
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val writeLock = Mutex()

    private val _data = MutableStateFlow(load())
    val data: StateFlow<AppData> = _data.asStateFlow()

    private fun load(): AppData {
        if (file.exists()) {
            try {
                return JsonCodec.decode(file.readText())
            } catch (e: Exception) {
                Log.e(TAG, "Daten konnten nicht gelesen werden, Sicherung wird angelegt", e)
                file.copyTo(File(file.parentFile, "welpentraining.corrupt.json"), overwrite = true)
            }
        }
        return SeedData.initial().also { persist(it) }
    }

    private fun persist(data: AppData) {
        scope.launch {
            writeLock.withLock {
                val tmp = File(file.parentFile, file.name + ".tmp")
                tmp.writeText(JsonCodec.encode(data))
                if (!tmp.renameTo(file)) {
                    file.writeText(tmp.readText())
                    tmp.delete()
                }
            }
        }
    }

    private fun change(transform: (AppData) -> AppData) {
        _data.update(transform)
        persist(_data.value)
    }

    // --- Profil ---

    fun saveProfile(profile: DogProfile) = change { it.copy(profile = profile) }

    // --- Kommandos ---

    fun saveCommand(command: Command) = change { d ->
        val exists = d.commands.any { it.id == command.id }
        d.copy(commands = if (exists) d.commands.map { if (it.id == command.id) command else it } else d.commands + command)
    }

    fun deleteCommand(id: String) = change { d ->
        d.copy(
            commands = d.commands.filterNot { it.id == id },
            trainings = d.trainings.map { t -> t.copy(items = t.items.filterNot { it.commandId == id }) },
        )
    }

    // --- Trainings ---

    fun saveTraining(training: Training) = change { d ->
        val exists = d.trainings.any { it.id == training.id }
        d.copy(trainings = if (exists) d.trainings.map { if (it.id == training.id) training else it } else d.trainings + training)
    }

    fun deleteTraining(id: String) = change { d -> d.copy(trainings = d.trainings.filterNot { it.id == id }) }

    // --- Einheiten ---

    fun saveLog(log: SessionLog) = change { d -> d.copy(logs = d.logs.filterNot { it.id == log.id } + log) }

    fun deleteLog(id: String) = change { d -> d.copy(logs = d.logs.filterNot { it.id == id }) }

    companion object {
        private const val TAG = "Repository"

        @Volatile
        private var instance: Repository? = null

        fun get(context: Context): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(context.applicationContext).also { instance = it }
            }

        fun newId(): String = UUID.randomUUID().toString()
    }
}
