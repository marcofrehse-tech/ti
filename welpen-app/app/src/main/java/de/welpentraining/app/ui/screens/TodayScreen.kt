package de.welpentraining.app.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.welpentraining.app.data.AppData
import de.welpentraining.app.data.DogProfile
import de.welpentraining.app.data.Progress
import de.welpentraining.app.data.SessionLog
import de.welpentraining.app.data.Training
import de.welpentraining.app.ui.Routes
import de.welpentraining.app.ui.components.ScreenScaffold
import de.welpentraining.app.ui.components.SectionTitle
import de.welpentraining.app.ui.components.StatTile
import de.welpentraining.app.ui.components.formatted
import de.welpentraining.app.ui.components.percent
import de.welpentraining.app.ui.components.relative
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun TodayScreen(
    data: AppData,
    onStartSession: (String) -> Unit,
    onOpenProfile: () -> Unit,
    onDeleteLog: (String) -> Unit,
) {
    val today = LocalDate.now()
    val planned = data.trainings.filter { today.dayOfWeek in it.weekdays }
    val todayLogs = data.logs.filter { it.date == today }
    val doneIds = todayLogs.mapNotNull { it.trainingId }.toSet()
    val recent = data.logs.sortedByDescending { it.timestamp }.take(15)
    var toDelete by remember { mutableStateOf<SessionLog?>(null) }

    ScreenScaffold(
        title = if (data.profile.name.isBlank()) "Heute" else "Heute mit ${data.profile.name}",
        actions = {
            IconButton(onClick = onOpenProfile) { Icon(Icons.Filled.Pets, contentDescription = "Welpenprofil") }
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
                Column {
                    Text(today.formatted(), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    ageLine(data.profile, today)?.let {
                        Text(it, style = MaterialTheme.typography.bodyLarge)
                    }
                    if (data.profile.name.isBlank()) {
                        TextButton(onClick = onOpenProfile, contentPadding = PaddingValues(0.dp)) {
                            Text("Profil deines Welpen anlegen")
                        }
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatTile("Heute erledigt", "${doneIds.intersect(planned.map { it.id }.toSet()).size}/${planned.size}", Modifier.weight(1f))
                    StatTile("Tage in Folge", Progress.streak(data.logs, today).toString(), Modifier.weight(1f))
                    StatTile("Diese Woche", Progress.sessionsThisWeek(data.logs, today).toString(), Modifier.weight(1f))
                }
            }
            item { SectionTitle("Tägliche Einheiten") }
            if (planned.isEmpty()) {
                item {
                    Text(
                        "Für heute ist kein Training geplant. Lege Wochentage bei deinen Trainings fest oder starte eine freie Einheit.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            items(planned, key = { it.id }) { training ->
                PlannedCard(
                    training = training,
                    data = data,
                    timesDone = todayLogs.count { it.trainingId == training.id },
                    onStart = { onStartSession(training.id) },
                )
            }
            item {
                OutlinedButton(onClick = { onStartSession(Routes.NEW) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Freie Einheit starten")
                }
            }
            if (recent.isNotEmpty()) {
                item { SectionTitle("Letzte Einheiten") }
                items(recent, key = { it.id }) { log ->
                    LogRow(log, data, today, onDelete = { toDelete = log })
                }
            }
        }
    }

    toDelete?.let { log ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text("Einheit löschen?") },
            text = { Text("„${log.trainingName}“ vom ${log.date.relative(today)} wird mitsamt Ergebnissen entfernt.") },
            confirmButton = {
                TextButton(onClick = { onDeleteLog(log.id); toDelete = null }) { Text("Löschen") }
            },
            dismissButton = { TextButton(onClick = { toDelete = null }) { Text("Abbrechen") } },
        )
    }
}

private fun ageLine(profile: DogProfile, today: LocalDate): String? {
    val birth = profile.birthDate ?: return null
    val weeks = ChronoUnit.WEEKS.between(birth, today)
    if (weeks < 0) return null
    val name = profile.name.ifBlank { "Dein Welpe" }
    val age = when {
        weeks < 26 -> "$weeks Wochen"
        else -> {
            val months = ChronoUnit.MONTHS.between(birth, today)
            if (months < 24) "$months Monate" else "${months / 12} Jahre"
        }
    }
    return "$name ist $age alt."
}

@Composable
private fun PlannedCard(training: Training, data: AppData, timesDone: Int, onStart: () -> Unit) {
    val done = timesDone > 0
    Card(
        colors = if (done) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer) else CardDefaults.cardColors(),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (done) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                contentDescription = if (done) "Erledigt" else "Offen",
                tint = if (done) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline,
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(training.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${training.durationMinutes} Min · " + training.items.mapNotNull { data.command(it.commandId)?.name }.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (timesDone > 1) Text("${timesDone}× erledigt", style = MaterialTheme.typography.labelSmall)
            }
            FilledTonalButton(onClick = onStart) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text(if (done) "Nochmal" else "Start")
            }
        }
    }
}

@Composable
private fun LogRow(log: SessionLog, data: AppData, today: LocalDate, onDelete: () -> Unit) {
    val attempts = log.entries.sumOf { it.attempts }
    val successes = log.entries.sumOf { it.successes }
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(log.trainingName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    if (log.mood > 0) {
                        Spacer(Modifier.width(6.dp))
                        repeat(log.mood) {
                            Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                val rate = if (attempts > 0) " · Erfolg ${(successes.toFloat() / attempts).percent()}" else ""
                Text(
                    "${log.date.relative(today)}$rate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                val details = log.entries.filter { it.attempts > 0 }.joinToString(" · ") {
                    "${data.command(it.commandId)?.name ?: "?"} ${it.successes}/${it.attempts}"
                }
                if (details.isNotEmpty()) Text(details, style = MaterialTheme.typography.bodySmall)
                if (log.note.isNotBlank()) Text("„${log.note}“", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, contentDescription = "Löschen") }
        }
    }
}
