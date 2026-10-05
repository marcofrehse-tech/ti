package de.welpentraining.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.welpentraining.app.data.Level
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

val German: Locale = Locale.GERMAN

fun DayOfWeek.shortName(): String = getDisplayName(TextStyle.SHORT, German).removeSuffix(".")

fun LocalDate.formatted(): String = format(DateTimeFormatter.ofPattern("EE, d. MMM yyyy", German))

fun LocalDate.relative(today: LocalDate = LocalDate.now()): String = when (this) {
    today -> "Heute"
    today.minusDays(1) -> "Gestern"
    else -> format(DateTimeFormatter.ofPattern("d. MMM", German))
}

fun Set<DayOfWeek>.describe(): String = when {
    isEmpty() -> "Nur manuell"
    size == 7 -> "Täglich"
    this == setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY) -> "Am Wochenende"
    else -> sorted().joinToString(", ") { it.shortName() }
}

fun Float.percent(): String = "${(this * 100).toInt()} %"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenScaffold(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                        }
                    }
                },
                actions = { actions() },
            )
        },
        floatingActionButton = floatingActionButton,
        content = content,
    )
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier.padding(top = 8.dp, bottom = 4.dp),
    )
}

@Composable
fun InfoCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp), content = content)
    }
}

@Composable
fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun levelColor(level: Level): Color = when (level) {
    Level.NEW -> MaterialTheme.colorScheme.outline
    Level.LEARNING -> Color(0xFFD9822B)
    Level.PARTIAL -> Color(0xFFC9A227)
    Level.ALMOST -> Color(0xFF7FA650)
    Level.SOLID -> Color(0xFF3E8E41)
}

@Composable
fun LevelBadge(level: Level) {
    val color = levelColor(level)
    Surface(shape = MaterialTheme.shapes.small, color = color.copy(alpha = 0.18f)) {
        Text(
            level.label,
            color = if (level == Level.NEW) MaterialTheme.colorScheme.onSurfaceVariant else color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

@Composable
fun ProgressLine(progress: Float, color: Color = MaterialTheme.colorScheme.primary) {
    LinearProgressIndicator(
        progress = { progress.coerceIn(0f, 1f) },
        modifier = Modifier.fillMaxWidth().height(8.dp),
        color = color,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        strokeCap = StrokeCap.Round,
        drawStopIndicator = {},
    )
}

@Composable
fun EmptyState(icon: ImageVector, title: String, text: String) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.outline)
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            Text(text, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** Einfaches Liniendiagramm für Werte zwischen 0 und 1. */
@Composable
fun RateChart(values: List<Float>, modifier: Modifier = Modifier) {
    val line = MaterialTheme.colorScheme.primary
    val grid = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    Column(modifier) {
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.height(140.dp).padding(end = 6.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Text("100 %", style = MaterialTheme.typography.labelSmall)
                Text("50 %", style = MaterialTheme.typography.labelSmall)
                Text("0 %", style = MaterialTheme.typography.labelSmall)
            }
            Canvas(Modifier.weight(1f).height(140.dp).padding(vertical = 6.dp)) {
                listOf(0f, 0.5f, 1f).forEach { y ->
                    val py = size.height * (1 - y)
                    drawLine(grid, Offset(0f, py), Offset(size.width, py), strokeWidth = 1.dp.toPx())
                }
                if (values.isEmpty()) return@Canvas
                val step = if (values.size > 1) size.width / (values.size - 1) else 0f
                val points = values.mapIndexed { i, v ->
                    Offset(if (values.size > 1) i * step else size.width / 2, size.height * (1 - v.coerceIn(0f, 1f)))
                }
                if (points.size > 1) {
                    val path = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        points.drop(1).forEach { lineTo(it.x, it.y) }
                    }
                    drawPath(path, line, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                }
                points.forEach { drawCircle(line, radius = 4.dp.toPx(), center = it) }
            }
        }
    }
}
