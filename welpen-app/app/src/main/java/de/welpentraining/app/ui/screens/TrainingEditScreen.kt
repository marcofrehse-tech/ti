package de.welpentraining.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.welpentraining.app.data.AppData
import de.welpentraining.app.data.Repository
import de.welpentraining.app.data.Training
import de.welpentraining.app.data.TrainingItem
import de.welpentraining.app.ui.components.ScreenScaffold
import de.welpentraining.app.ui.components.SectionTitle
import de.welpentraining.app.ui.components.shortName
import java.time.DayOfWeek

private val trainingItemsSaver = listSaver<SnapshotStateList<TrainingItem>, String>(
    save = { list -> list.map { "${it.commandId}|${it.targetReps}" } },
    restore = { saved ->
        saved.map { s ->
            val (id, reps) = s.split("|", limit = 2)
            TrainingItem(id, reps.toIntOrNull() ?: 5)
        }.toMutableStateList()
    },
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TrainingEditScreen(
    data: AppData,
    trainingId: String?,
    onSave: (Training) -> Unit,
    onDelete: (String) -> Unit,
    onNewCommand: () -> Unit,
    onBack: () -> Unit,
) {
    val existing = trainingId?.let { data.training(it) }
    var name by rememberSaveable { mutableStateOf(existing?.name.orEmpty()) }
    var description by rememberSaveable { mutableStateOf(existing?.description.orEmpty()) }
    var duration by rememberSaveable { mutableStateOf((existing?.durationMinutes ?: 10).toString()) }
    var weekdays by rememberSaveable { mutableStateOf(existing?.weekdays?.map { it.value }?.toSet() ?: emptySet()) }
    val exercises = rememberSaveable(saver = trainingItemsSaver) {
        (existing?.items ?: emptyList()).toMutableStateList()
    }
    var showAdd by rememberSaveable { mutableStateOf(false) }
    var confirmDelete by rememberSaveable { mutableStateOf(false) }

    // Gelöschte Kommandos nicht mehr anzeigen.
    val visibleItems = exercises.filter { data.command(it.commandId) != null }

    ScreenScaffold(
        title = if (existing == null) "Neues Training" else "Training bearbeiten",
        onBack = onBack,
        actions = {
            if (existing != null) {
                IconButton(onClick = { confirmDelete = true }) { Icon(Icons.Filled.Delete, contentDescription = "Löschen") }
            }
        },
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                OutlinedTextField(
                    value = description, onValueChange = { description = it },
                    label = { Text("Beschreibung / Ziel") }, modifier = Modifier.fillMaxWidth(), minLines = 2,
                )
            }
            item {
                OutlinedTextField(
                    value = duration,
                    onValueChange = { v -> duration = v.filter { it.isDigit() }.take(3) },
                    label = { Text("Dauer in Minuten") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                Column {
                    SectionTitle("Tägliche Einheit an diesen Tagen")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        DayOfWeek.entries.forEach { day ->
                            FilterChip(
                                selected = day.value in weekdays,
                                onClick = { weekdays = if (day.value in weekdays) weekdays - day.value else weekdays + day.value },
                                label = { Text(day.shortName()) },
                            )
                        }
                    }
                    Row {
                        TextButton(onClick = { weekdays = DayOfWeek.entries.map { it.value }.toSet() }) { Text("Täglich") }
                        TextButton(onClick = { weekdays = emptySet() }) { Text("Nur manuell") }
                    }
                }
            }
            item { SectionTitle("Kommandos & Wiederholungen") }
            if (visibleItems.isEmpty()) {
                item { Text("Noch keine Kommandos. Füge mindestens eines hinzu.", style = MaterialTheme.typography.bodyMedium) }
            }
            items(visibleItems, key = { it.commandId }) { item ->
                val index = exercises.indexOf(item)
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(data.command(item.commandId)?.name.orEmpty(), style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                        IconButton(onClick = { if (item.targetReps > 1) exercises[index] = item.copy(targetReps = item.targetReps - 1) }) {
                            Icon(Icons.Filled.Remove, contentDescription = "Weniger")
                        }
                        Text("${item.targetReps}×", style = MaterialTheme.typography.titleMedium)
                        IconButton(onClick = { if (item.targetReps < 99) exercises[index] = item.copy(targetReps = item.targetReps + 1) }) {
                            Icon(Icons.Filled.Add, contentDescription = "Mehr")
                        }
                        Column {
                            IconButton(onClick = { if (index > 0) exercises.add(index - 1, exercises.removeAt(index)) }, enabled = index > 0) {
                                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Nach oben")
                            }
                            IconButton(onClick = { if (index < exercises.lastIndex) exercises.add(index + 1, exercises.removeAt(index)) }, enabled = index < exercises.lastIndex) {
                                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Nach unten")
                            }
                        }
                        IconButton(onClick = { exercises.removeAt(index) }) { Icon(Icons.Filled.Close, contentDescription = "Entfernen") }
                    }
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
                Button(
                    onClick = {
                        onSave(
                            Training(
                                id = existing?.id ?: Repository.newId(),
                                name = name.trim(),
                                description = description.trim(),
                                durationMinutes = duration.toIntOrNull()?.coerceAtLeast(1) ?: 10,
                                items = visibleItems,
                                weekdays = weekdays.map { DayOfWeek.of(it) }.toSet(),
                                builtIn = existing?.builtIn ?: false,
                            )
                        )
                    },
                    enabled = name.isNotBlank() && visibleItems.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Speichern") }
            }
        }
    }

    if (showAdd) {
        val available = data.commands.filter { c -> exercises.none { it.commandId == c.id } }.sortedBy { it.name }
        AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text("Kommando hinzufügen") },
            text = {
                LazyColumn {
                    items(available, key = { it.id }) { c ->
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    exercises.add(TrainingItem(c.id, 5))
                                    showAdd = false
                                }
                                .padding(vertical = 10.dp),
                        ) {
                            Text(c.name, style = MaterialTheme.typography.bodyLarge)
                            Text(c.category, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    item {
                        HorizontalDivider()
                        TextButton(onClick = { showAdd = false; onNewCommand() }) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Neues Kommando anlegen")
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showAdd = false }) { Text("Schließen") } },
        )
    }

    if (confirmDelete && existing != null) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Training löschen?") },
            text = { Text("„${existing.name}“ wird gelöscht. Bereits absolvierte Einheiten bleiben im Verlauf erhalten.") },
            confirmButton = { TextButton(onClick = { confirmDelete = false; onDelete(existing.id) }) { Text("Löschen") } },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Abbrechen") } },
        )
    }
}
