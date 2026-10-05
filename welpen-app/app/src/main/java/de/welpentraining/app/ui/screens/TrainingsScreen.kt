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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.welpentraining.app.data.AppData
import de.welpentraining.app.data.Training
import de.welpentraining.app.ui.Routes
import de.welpentraining.app.ui.components.ScreenScaffold
import de.welpentraining.app.ui.components.describe

@Composable
fun TrainingsScreen(
    data: AppData,
    onStart: (String) -> Unit,
    onEdit: (String) -> Unit,
) {
    ScreenScaffold(
        title = "Trainings",
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onEdit(Routes.NEW) },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Neues Training") },
            )
        },
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 96.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Text(
                    "Vordefinierte Trainings kannst du anpassen oder löschen. Eigene Trainings stellst du frei aus Kommandos zusammen und legst fest, an welchen Tagen sie als Einheit erscheinen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            items(data.trainings, key = { it.id }) { training ->
                TrainingCard(training, data, onStart = { onStart(training.id) }, onEdit = { onEdit(training.id) })
            }
        }
    }
}

@Composable
private fun TrainingCard(training: Training, data: AppData, onStart: () -> Unit, onEdit: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(training.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (training.builtIn) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Text(
                        if (training.builtIn) "Vorlage" else "Eigenes",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                }
            }
            if (training.description.isNotBlank()) {
                Text(training.description, style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                "${training.weekdays.describe()} · ${training.durationMinutes} Min",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                training.items.mapNotNull { item -> data.command(item.commandId)?.let { "${it.name} (${item.targetReps}×)" } }
                    .joinToString(" · ")
                    .ifEmpty { "Noch keine Kommandos" },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                FilledTonalButton(onClick = onStart, enabled = training.items.isNotEmpty()) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Starten")
                }
                OutlinedButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Bearbeiten")
                }
            }
        }
    }
}
