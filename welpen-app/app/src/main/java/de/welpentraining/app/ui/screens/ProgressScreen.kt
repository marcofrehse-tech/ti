package de.welpentraining.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.welpentraining.app.data.AppData
import de.welpentraining.app.data.CommandProgress
import de.welpentraining.app.data.Level
import de.welpentraining.app.data.Progress
import de.welpentraining.app.ui.components.LevelBadge
import de.welpentraining.app.ui.components.ProgressLine
import de.welpentraining.app.ui.components.ScreenScaffold
import de.welpentraining.app.ui.components.StatTile
import de.welpentraining.app.ui.components.levelColor
import de.welpentraining.app.ui.components.percent
import de.welpentraining.app.ui.components.relative

@Composable
fun ProgressScreen(
    data: AppData,
    onOpenCommand: (String) -> Unit,
    onNewCommand: () -> Unit,
) {
    val all = Progress.all(data)
    val categories = all.map { it.command.category }.filter { it.isNotBlank() }.distinct().sorted()
    var category by rememberSaveable { mutableStateOf<String?>(null) }
    val shown = all
        .filter { category == null || it.command.category == category }
        .sortedWith(compareByDescending<CommandProgress> { it.lastTrained != null }.thenByDescending { it.score }.thenBy { it.command.name })

    ScreenScaffold(
        title = "Fortschritt",
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewCommand,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Neues Kommando") },
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
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatTile("Sitzt!", all.count { it.level == Level.SOLID }.toString(), Modifier.weight(1f))
                    StatTile("In Arbeit", all.count { it.level != Level.SOLID && it.level != Level.NEW }.toString(), Modifier.weight(1f))
                    StatTile("Noch offen", all.count { it.level == Level.NEW }.toString(), Modifier.weight(1f))
                }
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item { FilterChip(selected = category == null, onClick = { category = null }, label = { Text("Alle") }) }
                    items(categories) { c ->
                        FilterChip(selected = category == c, onClick = { category = if (category == c) null else c }, label = { Text(c) })
                    }
                }
            }
            items(shown, key = { it.command.id }) { p ->
                ProgressCard(p, onClick = { onOpenCommand(p.command.id) })
            }
        }
    }
}

@Composable
private fun ProgressCard(p: CommandProgress, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(p.command.name, style = MaterialTheme.typography.titleMedium)
                    Text(p.command.category, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                LevelBadge(p.level)
            }
            ProgressLine(p.score, color = levelColor(p.level))
            Text(
                if (p.sessions == 0) "Noch keine Einheit"
                else "Zuletzt ${p.recentRate.percent()} Erfolg · ${p.sessions} Einheiten · ${p.lastTrained?.relative()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
