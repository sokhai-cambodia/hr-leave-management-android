package com.mitclass.hrleave.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.BrandPrimary
import com.mitclass.hrleave.core.theme.LightFieldFill
import com.mitclass.hrleave.core.theme.PillCornerRadius

/**
 * STYLE_GUIDE.md tab bar spec: "pill-shaped indicator in primary color, white selected label,
 * unselected label at 60% opacity" — a segmented pill control, not Material's default underline
 * [androidx.compose.material3.TabRow].
 */
@Composable
fun PillTabRow(
    titles: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = LightFieldFill, shape = RoundedCornerShape(PillCornerRadius))
            .padding(4.dp),
    ) {
        titles.forEachIndexed { index, title ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(PillCornerRadius))
                    .background(if (selected) BrandPrimary else Color.Transparent)
                    .clickable { onSelect(index) }
                    .padding(vertical = AppSpacing.sm),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }
        }
    }
}
