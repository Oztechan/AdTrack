/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.oztechan.adtrack.core.util.formatCurrency
import com.oztechan.adtrack.domain.model.RevenuePoint
import com.oztechan.adtrack.ui.theme.AdTrackTheme
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.ui.tooling.preview.Preview

private enum class ChartType { BAR, LINE }

/**
 * Revenue chart drawn with Compose Canvas (no chart dependency — identical on Android and iOS).
 * Each point is one bucket of the series (a day, week, or month depending on the period).
 * Supports a bar/line toggle, tap-to-inspect a single point, faint Y gridlines and right-side
 * value labels (so values are approximate without labeling every bar). Renders nothing for fewer
 * than two data points.
 */
@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
fun RevenueChart(
    points: List<RevenuePoint>,
    currencyCode: String,
    modifier: Modifier = Modifier,
    title: String = "Daily earnings"
) {
    if (points.size < 2) return

    var chartType by remember { mutableStateOf(ChartType.BAR) }
    var selectedIndex by remember(points) { mutableStateOf<Int?>(null) }

    val maxEarnings = points.maxOf { it.earnings }
    val barColor = MaterialTheme.colorScheme.primary
    val selectedColor = MaterialTheme.colorScheme.tertiary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val selected = selectedIndex?.let { points.getOrNull(it) }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = chartType == ChartType.BAR,
                        onClick = { chartType = ChartType.BAR },
                        label = { Text("Bar") }
                    )
                    FilterChip(
                        selected = chartType == ChartType.LINE,
                        onClick = { chartType = ChartType.LINE },
                        label = { Text("Line") }
                    )
                }
            }

            Text(
                text = selected
                    ?.let { "${it.date}  ·  ${formatCurrency(it.earnings, currencyCode)}" }
                    ?: "Max ${formatCurrency(maxEarnings, currencyCode)}  ·  tap to inspect a point",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 4.dp)
            )

            Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .height(160.dp)
                        .pointerInput(points, chartType) {
                            detectTapGestures { offset ->
                                val index = (offset.x / size.width * points.size)
                                    .toInt()
                                    .coerceIn(0, points.lastIndex)
                                selectedIndex = if (selectedIndex == index) null else index
                            }
                        }
                ) {
                    // Faint horizontal gridlines at 0 / 50% / 100%.
                    listOf(0f, 0.5f, 1f).forEach { fraction ->
                        val y = size.height * fraction
                        drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                    }
                    if (maxEarnings <= 0.0) return@Canvas

                    when (chartType) {
                        ChartType.BAR -> {
                            val count = points.size
                            val gap = 6f
                            val barWidth = ((size.width - gap * (count - 1)) / count).coerceAtLeast(1f)
                            val corner = CornerRadius(minOf(barWidth / 4f, 6f), minOf(barWidth / 4f, 6f))
                            points.forEachIndexed { index, point ->
                                val barHeight = (point.earnings / maxEarnings * size.height).toFloat()
                                val x = index * (barWidth + gap)
                                drawRoundRect(trackColor, Offset(x, 0f), Size(barWidth, size.height), corner)
                                drawRoundRect(
                                    color = if (index == selectedIndex) selectedColor else barColor,
                                    topLeft = Offset(x, size.height - barHeight),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = corner
                                )
                            }
                        }
                        ChartType.LINE -> {
                            val step = size.width / (points.size - 1)
                            fun pointY(value: Double) = size.height - (value / maxEarnings * size.height).toFloat()
                            val path = Path()
                            points.forEachIndexed { index, point ->
                                val x = index * step
                                val y = pointY(point.earnings)
                                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            }
                            drawPath(path, color = barColor, style = Stroke(width = 6f))
                            points.forEachIndexed { index, point ->
                                val isSelected = index == selectedIndex
                                drawCircle(
                                    color = if (isSelected) selectedColor else barColor,
                                    radius = if (isSelected) 9f else 5f,
                                    center = Offset(index * step, pointY(point.earnings))
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.height(160.dp).padding(start = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    AxisLabel(formatCurrency(maxEarnings, currencyCode))
                    AxisLabel(formatCurrency(maxEarnings / 2, currencyCode))
                    AxisLabel(formatCurrency(0.0, currencyCode))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AxisLabel(points.first().date.toString())
                AxisLabel(points.last().date.toString())
            }
        }
    }
}

@Composable
private fun AxisLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Preview
@Composable
private fun RevenueChartPreview() {
    AdTrackTheme {
        RevenueChart(
            points = (1..7).map { RevenuePoint(LocalDate(2026, 1, it), 8.0 + it * 2.5) },
            currencyCode = "USD"
        )
    }
}
