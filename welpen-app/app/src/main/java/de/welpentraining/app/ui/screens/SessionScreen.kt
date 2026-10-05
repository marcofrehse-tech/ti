package de.welpentraining.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.welpentraining.app.data.AppData
import de.welpentraining.app.data.Command
import de.welpentraining.app.data.LogEntry
import de.welpentraining.app.data.Repository
import de.welpentraining.app.data.SessionLog
import de.welpentraining.app.ui.components.ProgressLine
import de.welpentraining.app.ui.components.ScreenScaffold
import kotlinx.coroutines.delay
import java.time.LocalDate

/** Zustand einer Übung während der Einheit. results: true = geklappt, false = nicht geklappt. */
private data class ItemState(val commandId: String, val target: Int, val results: List<Boolean> = emptyList()) {
    val attempts get() = results.size
    val successes get() = results.count { it }
}

private val itemsSaver = listSaver<SnapshotStateList<ItemState>, String>(
    save = { list -> list.map { "${it.commandId}|${it.target}|" + it.results.joinToString("") { r -> if (r) "1" else "0" } } },
    restore = { saved ->
        saved.map { s ->
            val (id, target, res) = s.split("|", limit = 3)
            ItemState(id, target.toIntOrNull() ?: 5, res.map { it == '1' })
        }.toMutableStateList()
    },
)

@Composable
fun SessionScreen(
    data: AppData,
    trainingId: String?,
    onSave: (SessionLog) -> Unit,
    onBack: () -> Unit,
) {
    val training = trainingId?.let { data.training(it) }
    val exercises = rememberSaveable(saver = itemsSaver) {
        (training?.items ?: emptyList())
            .filter { data.command(it.commandId) != null }
            .distinctBy { it.commandId }
            .map { ItemState(it.commandId, it.targetReps) }
            .toMutableStateList()
    }
    var mood by rememberSaveable { mutableIntStateOf(0) }
    var note by rememberSaveable { mutableStateOf("") }
    var showAdd by rememberSaveable { mutableStateOf(false) }
    var confirmLeave by rememberSaveable { mutableStateOf(false) }
    val startedAt = rememberSaveable { System.currentTimeMillis() }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            now = System.currentTimeMillis()
        }
    }

    val hasResults = exercises.any { it.attempts > 0 }
    val leave: () -> Unit = { if (hasResults) confirmLeave = true else onBack() }
    BackHandler(onBack = leave)

    val title = training?.name ?: "Freie Einheit"
    ScreenScaffold(title = title, onBack = leave) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                val seconds = (now - startedAt) / 1000
                val target = training?.durationMinutes?.let { " von ca. $it Min" } ?: ""
                Text(
                    "Dauer: %d:%02d%s".format(seconds / 60, seconds % 60, target),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (exercises.isEmpty()) {
                item {
                    Text("Füge die Kommandos hinzu, die du jetzt üben möchtest.", style = MaterialTheme.typography.bodyMedium)
                }
            }
            itemsIndexed(exercises, key = { _, it -> it.commandId }) { index, item ->
                val command = data.command(item.commandId)
                if (command != null) {
                    ExerciseCard(
                        command = command,
                        state = item,
                        onResult = { ok -> exercises[index] = exercises[index].let { it.copy(results = it.results + ok) } },
                        onUndo = { exercises[index] = exercises[index].let { it.copy(results = it.results.dropLast(1)) } },
                        onRemove = { exercises.removeAt(index) },
                    )
                }
            }
            item {
                OutlinedButton(onClick = { showAdd = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Kommando hinzufügen")
                }
            }
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Wie lief es insgesamt?", style = MaterialTheme.typography.titleSmall)
                        Row {
                            (1..5).forEach { star ->
                                IconButton(onClick = { mood = if (mood == star) 0 else star }) {
                                    Icon(
                                        if (star <= mood) Icons.Filled.Star else Icons.Filled.StarBorder,
                                        contentDescription = "$star Sterne",
                                        tint = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                        }
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            label = { Text("Notiz (optional)") },
                            placeholder = { Text("z. B. viel Ablenkung im Park") },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        onSave(
                            SessionLog(
                                id = Repository.newId(),
                                trainingId = training?.id,
                                trainingName = title,
                                date = LocalDate.now(),
                                timestamp = System.currentTimeMillis(),
                                entries = exercises.map { LogEntry(it.commandId, it.attempts, it.successes) },
                                mood = mood,
                                note = note.trim(),
                            )
                        )
                    },
                    enabled = hasResults,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Einheit speichern")
                }
                if (!hasResults) {
                    Text(
                        "Trage mindestens einen Versuch ein, um die Einheit zu speichern.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
    }

    if (showAdd) {
        val available = data.commands.filter { c -> exercises.none { it.commandId == c.id } }.sortedBy { it.name }
        AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text("Kommando hinzufügen") },
            text = {
                if (available.isEmpty()) {
                    Text("Alle Kommandos sind bereits in dieser Einheit.")
                } else {
                    LazyColumn {
                        itemsIndexed(available, key = { _, c -> c.id }) { _, c ->
                            Text(
                                c.name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        exercises.add(ItemState(c.id, 5))
                                        showAdd = false
                                    }
                                    .padding(vertical = 12.dp),
                            )
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showAdd = false }) { Text("Schließen") } },
        )
    }

    if (confirmLeave) {
        AlertDialog(
            onDismissRequest = { confirmLeave = false },
            title = { Text("Einheit verwerfen?") },
            text = { Text("Die eingetragenen Ergebnisse gehen verloren.") },
            confirmButton = { TextButton(onClick = { confirmLeave = false; onBack() }) { Text("Verwerfen") } },
            dismissButton = { TextButton(onClick = { confirmLeave = false }) { Text("Weiter trainieren") } },
        )
    }
}

@Composable
private fun ExerciseCard(
    command: Command,
    state: ItemState,
    onResult: (Boolean) -> Unit,
    onUndo: () -> Unit,
    onRemove: () -> Unit,
) {
    var expanded by rememberSaveable(command.id) { mutableStateOf(false) }
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(command.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        "${state.attempts}/${state.target} Wiederholungen · ${state.successes} geklappt",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = "Anleitung")
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Filled.Close, contentDescription = "Entfernen")
                }
            }
            ProgressLine(state.attempts.toFloat() / state.target.coerceAtLeast(1))
            if (expanded) {
                if (command.description.isNotBlank()) Text(command.description, style = MaterialTheme.typography.bodyMedium)
                command.steps.forEachIndexed { i, step ->
                    Text("${i + 1}. $step", style = MaterialTheme.typography.bodySmall)
                }
            }
            if (state.results.isNotEmpty()) {
                Text(
                    state.results.joinToString(" ") { if (it) "✓" else "✗" },
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { onResult(true) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Geklappt")
                }
                OutlinedButton(onClick = { onResult(false) }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Nicht")
                }
                IconButton(onClick = onUndo, enabled = state.results.isNotEmpty()) {
                    Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Rückgängig")
                }
            }
        }
    }
}
