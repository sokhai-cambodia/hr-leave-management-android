package com.mitclass.hrleave.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.CardCornerRadius

/** A pastel-tinted action tile ("Request Leave"/"Plan Leave") — Task 13.3/13.4. */
@Composable
fun PastelActionTile(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(CardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = tint.copy(alpha = 0.12f)),
    ) {
        Column(modifier = Modifier.padding(AppSpacing.lg)) {
            Icon(imageVector = icon, contentDescription = null, tint = tint)
            Spacer(Modifier.height(AppSpacing.sm))
            Text(text = label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        }
    }
}
