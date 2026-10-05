package de.welpentraining.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.welpentraining.app.knowledge.Breed
import de.welpentraining.app.knowledge.KnowledgeContent
import de.welpentraining.app.ui.components.EmptyState
import de.welpentraining.app.ui.components.InfoCard
import de.welpentraining.app.ui.components.ScreenScaffold

@Composable
fun KnowledgeScreen(
    onOpenArticle: (String) -> Unit,
    onOpenBreed: (String) -> Unit,
) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var query by rememberSaveable { mutableStateOf("") }
    val q = query.trim().lowercase()

    val articles = KnowledgeContent.articles.filter { a ->
        q.isEmpty() || a.title.lowercase().contains(q) || a.summary.lowercase().contains(q) ||
            a.sections.any { it.heading.lowercase().contains(q) || it.text.lowercase().contains(q) }
    }
    val breeds = KnowledgeContent.breeds.filter { b ->
        q.isEmpty() || listOf(b.name, b.group, b.character, b.trainingTips).any { it.lowercase().contains(q) }
    }

    ScreenScaffold(title = "Wissen") { padding ->
        Column(Modifier.padding(top = padding.calculateTopPadding())) {
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Allgemeine Hinweise") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Rassen") })
            }
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                placeholder = { Text("Suchen …") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            )
            val empty = if (tab == 0) articles.isEmpty() else breeds.isEmpty()
            if (empty) {
                EmptyState(Icons.Filled.SearchOff, "Nichts gefunden", "Versuche einen anderen Suchbegriff.")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = padding.calculateBottomPadding() + 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (tab == 0) {
                        items(articles, key = { it.id }) { a ->
                            Card(Modifier.fillMaxWidth().clickable { onOpenArticle(a.id) }) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(a.title, style = MaterialTheme.typography.titleMedium)
                                    Text(a.summary, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    } else {
                        items(breeds, key = { it.id }) { b ->
                            Card(Modifier.fillMaxWidth().clickable { onOpenBreed(b.id) }) {
                                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(b.name, style = MaterialTheme.typography.titleMedium)
                                    Text("${b.group} · ${b.size}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Meter("Energie", b.energy)
                                    Meter("Trainierbarkeit", b.trainability)
                                }
                            }
                        }
                    }
                    item {
                        Text(
                            "Diese Hinweise ersetzen keine tierärztliche Beratung und keine Hundeschule. Jeder Hund ist ein Individuum.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Meter(label: String, value: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            (1..5).forEach { i ->
                Box(
                    Modifier
                        .size(10.dp)
                        .background(
                            if (i <= value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            CircleShape,
                        )
                )
            }
        }
    }
}

@Composable
fun ArticleScreen(articleId: String, onBack: () -> Unit) {
    val article = KnowledgeContent.articles.firstOrNull { it.id == articleId }
    ScreenScaffold(title = article?.title ?: "Hinweis", onBack = onBack) { padding ->
        if (article == null) {
            EmptyState(Icons.Filled.SearchOff, "Nicht gefunden", "")
            return@ScreenScaffold
        }
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item { Text(article.summary, style = MaterialTheme.typography.bodyLarge) }
            items(article.sections) { s ->
                InfoCard {
                    Text(s.heading, style = MaterialTheme.typography.titleSmall)
                    Text(s.text, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun BreedScreen(breedId: String, onBack: () -> Unit) {
    val breed = KnowledgeContent.breeds.firstOrNull { it.id == breedId }
    ScreenScaffold(title = breed?.name ?: "Rasse", onBack = onBack) { padding ->
        if (breed == null) {
            EmptyState(Icons.Filled.SearchOff, "Nicht gefunden", "")
            return@ScreenScaffold
        }
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item { BreedHeader(breed) }
            item { InfoCard { Text("Wesen", style = MaterialTheme.typography.titleSmall); Text(breed.character) } }
            item { InfoCard { Text("Trainingstipps", style = MaterialTheme.typography.titleSmall); Text(breed.trainingTips) } }
            item { InfoCard { Text("Pflege & Gesundheit", style = MaterialTheme.typography.titleSmall); Text(breed.care) } }
        }
    }
}

@Composable
private fun BreedHeader(breed: Breed) {
    InfoCard {
        Text("${breed.group} · ${breed.size}", style = MaterialTheme.typography.bodyMedium)
        Meter("Energie", breed.energy)
        Meter("Trainierbarkeit", breed.trainability)
    }
}
