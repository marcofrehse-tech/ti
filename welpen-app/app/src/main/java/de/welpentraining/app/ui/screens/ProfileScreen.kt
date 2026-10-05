package de.welpentraining.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.welpentraining.app.data.DogProfile
import de.welpentraining.app.knowledge.KnowledgeContent
import de.welpentraining.app.ui.components.ScreenScaffold
import de.welpentraining.app.ui.components.formatted
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    profile: DogProfile,
    onSave: (DogProfile) -> Unit,
    onBack: () -> Unit,
    onOpenBreed: (String) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf(profile.name) }
    var breed by rememberSaveable { mutableStateOf(profile.breed) }
    // Als Epoch-Day gespeichert, damit der Wert rememberSaveable-tauglich ist.
    var birthDay by rememberSaveable { mutableStateOf(profile.birthDate?.toEpochDay()) }
    var showPicker by rememberSaveable { mutableStateOf(false) }

    val matchingBreed = KnowledgeContent.breeds.firstOrNull { it.name.equals(breed.trim(), ignoreCase = true) }
    val suggestions = if (breed.isBlank()) emptyList() else KnowledgeContent.breeds.filter {
        it.name.contains(breed.trim(), ignoreCase = true) && !it.name.equals(breed.trim(), ignoreCase = true)
    }

    ScreenScaffold(title = "Mein Welpe", onBack = onBack) { padding ->
        Column(
            Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = name, onValueChange = { name = it }, label = { Text("Name") },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = breed, onValueChange = { breed = it }, label = { Text("Rasse") },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
            )
            if (suggestions.isNotEmpty()) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    suggestions.forEach { b -> SuggestionChip(onClick = { breed = b.name }, label = { Text(b.name) }) }
                }
            }
            if (matchingBreed != null) {
                TextButton(onClick = { onOpenBreed(matchingBreed.id) }) { Text("Hinweise zur Rasse ${matchingBreed.name} ansehen") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { showPicker = true }) {
                    Text(birthDay?.let { "Geboren: " + LocalDate.ofEpochDay(it).formatted() } ?: "Geburtsdatum wählen")
                }
                if (birthDay != null) TextButton(onClick = { birthDay = null }) { Text("Entfernen") }
            }
            Text(
                "Mit dem Geburtsdatum zeigt die App das Alter deines Welpen an.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = {
                    onSave(DogProfile(name.trim(), breed.trim(), birthDay?.let { LocalDate.ofEpochDay(it) }))
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Speichern") }
        }
    }

    if (showPicker) {
        val today = LocalDate.now()
        val state = rememberDatePickerState(
            initialSelectedDateMillis = (birthDay?.let { LocalDate.ofEpochDay(it) } ?: today.minusWeeks(10))
                .atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
            yearRange = (today.year - 20)..today.year,
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { ms ->
                        val picked = Instant.ofEpochMilli(ms).atZone(ZoneOffset.UTC).toLocalDate()
                        birthDay = (if (picked.isAfter(today)) today else picked).toEpochDay()
                    }
                    showPicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Abbrechen") } },
        ) {
            DatePicker(state = state)
        }
    }
}
