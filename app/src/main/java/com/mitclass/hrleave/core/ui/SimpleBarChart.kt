package com.mitclass.hrleave.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.LightFieldFill

data class BarChartEntry(val label: String, val value: Float, val valueText: String)

/**
 * A hand-rolled horizontal bar chart — no charting library dependency this close to submission,
 * just proportionally-widthed colored boxes next to a label/value, which covers every report
 * chart in this app (leave-type usage, status breakdown, monthly trend).
 */
@Composable
fun SimpleBarChart(
    entries: List<BarChartEntry>,
    barColor: Color,
    modifier: Modifier = Modifier,
    labelWidth: Dp = 96.dp,
) {
    val maxValue = entries.maxOfOrNull { it.value }?.takeIf { it > 0f } ?: 1f
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)) {
        entries.forEach { entry ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = entry.label,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.width(labelWidth),
                    maxLines = 1,
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(LightFieldFill),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = (entry.value / maxValue).coerceIn(0f, 1f))
                            .height(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(barColor),
                    )
                }
                Text(
                    text = entry.valueText,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .width(48.dp)
                        .padding(start = AppSpacing.xs),
                )
            }
        }
    }
}
