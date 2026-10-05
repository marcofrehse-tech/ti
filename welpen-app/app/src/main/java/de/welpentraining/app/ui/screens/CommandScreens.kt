package de.welpentraining.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.welpentraining.app.data.AppData
import de.welpentraining.app.data.Command
import de.welpentraining.app.data.Progress
import de.welpentraining.app.data.Repository
import de.welpentraining.app.ui.components.EmptyState
import de.welpentraining.app.ui.components.InfoCard
import de.welpentraining.app.ui.components.LevelBadge
import de.welpentraining.app.ui.components.ProgressLine
import de.welpentraining.app.ui.components.RateChart
import de.welpentraining.app.ui.components.ScreenScaffold
import de.welpentraining.app.ui.components.SectionTitle
import de.welpentraining.app.ui.components.StatTile
import de.welpentraining.app.ui.components.levelColor
import de.welpentraining.app.ui.components.percent
import de.welpentraining.app.ui.components.relative

@Composable
fun CommandDetailScreen(
    data: AppData,
    commandId: String,
    onEdit: () -> Unit,
    onBack: () -> Unit,
) {
    val command = data.command(commandId)
    if (command == null) {
        ScreenScaffold(title = "Kommando", onBack = onBack) {
            EmptyState(Icons.Filled.SearchOff, "Nicht gefunden", "Dieses Kommando wurde gelöscht.")
        }
        return
    }
    val p = Progress.forCommand(command, data.logs)
    val usedIn = data.trainings.filter { t -> t.items.any { it.commandId == command.id } }

    ScreenScaffold(
        title = command.name,
        onBack = onBack,
        actions = { IconButton(onClick = onEdit) { Icon(Icons.Filled.Edit, contentDescription = "Bearbeiten") } },
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
                InfoCard {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LevelBadge(p.level)
                        Text(command.category, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    ProgressLine(p.score, color = levelColor(p.level))
                    Text(p.level.hint, style = MaterialTheme.typography.bodyMedium)
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatTile("Einheiten", p.sessions.toString(), Modifier.weight(1f))
                    StatTile("Wieder­holungen", p.attempts.toString(), Modifier.weight(1f))
                    StatTile("Erfolg gesamt", if (p.attempts > 0) p.overallRate.percent() else "–", Modifier.weight(1f))
                }
            }
            if (p.history.isNotEmpty()) {
                item {
                    InfoCard {
                        Text("Erfolgsquote je Einheit", style = MaterialTheme.typography.titleSmall)
                        RateChart(p.history.takeLast(20).map { it.rate })
                        Text(
                            "Letzte ${p.history.takeLast(20).size} Einheiten · zuletzt ${p.recentRate.percent()} (Schnitt der letzten 5)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            if (command.description.isNotBlank() || command.steps.isNotEmpty()) {
                item {
                    InfoCard {
                        Text("So geht’s", style = MaterialTheme.typography.titleSmall)
                        if (command.description.isNotBlank()) Text(command.description, style = MaterialTheme.typography.bodyMedium)
                        command.steps.forEachIndexed { i, s ->
                            Text("${i + 1}. $s", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            if (usedIn.isNotEmpty()) {
                item {
                    Text(
                        "Enthalten in: " + usedIn.joinToString(", ") { it.name },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (p.history.isNotEmpty()) {
                item { SectionTitle("Verlauf") }
                items(p.history.reversed().take(30)) { point ->
                    Row(Modifier.fillMaxWidth()) {
                        Text(point.date.relative(), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "${(point.rate * point.attempts + 0.5f).toInt()}/${point.attempts} · ${point.rate.percent()}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CommandEditScreen(
    data: AppData,
    commandId: String?,
    onSave: (Command) -> Unit,
    onDelete: (String) -> Unit,
    onBack: () -> Unit,
) {
    val existing = commandId?.let { data.command(it) }
    var name by rememberSaveable { mutableStateOf(existing?.name.orEmpty()) }
    var category by rememberSaveable { mutableStateOf(existing?.category ?: "Eigene") }
    var description by rememberSaveable { mutableStateOf(existing?.description.orEmpty()) }
    var steps by rememberSaveable { mutableStateOf(existing?.steps?.joinToString("\n").orEmpty()) }
    var confirmDelete by rememberSaveable { mutableStateOf(false) }
    val categories = data.commands.map { it.category }.filter { it.isNotBlank() }.distinct().sorted()

    ScreenScaffold(
        title = if (existing == null) "Neues Kommando" else "Kommando bearbeiten",
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
                    value = name, onValueChange = { name = it }, label = { Text("Name, z. B. „High Five“") },
                    singleLine = true, modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                Column {
                    OutlinedTextField(
                        value = category, onValueChange = { category = it }, label = { Text("Kategorie") },
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        categories.forEach { c -> SuggestionChip(onClick = { category = c }, label = { Text(c) }) }
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = description, onValueChange = { description = it }, label = { Text("Beschreibung / Ziel") },
                    minLines = 2, modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                OutlinedTextField(
                    value = steps, onValueChange = { steps = it }, label = { Text("Anleitung – ein Schritt pro Zeile") },
                    minLines = 4, modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                Button(
                    onClick = {
                        onSave(
                            Command(
                                id = existing?.id ?: Repository.newId(),
                                name = name.trim(),
                                category = category.trim().ifEmpty { "Eigene" },
                                description = description.trim(),
                                steps = steps.lines().map { it.trim() }.filter { it.isNotEmpty() },
                                builtIn = existing?.builtIn ?: false,
                            )
                        )
                    },
                    enabled = name.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Speichern") }
            }
        }
    }

    if (confirmDelete && existing != null) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Kommando löschen?") },
            text = { Text("„${existing.name}“ wird aus allen Trainings entfernt und sein Fortschritt nicht mehr angezeigt.") },
            confirmButton = { TextButton(onClick = { confirmDelete = false; onDelete(existing.id) }) { Text("Löschen") } },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Abbrechen") } },
        )
    }
}
