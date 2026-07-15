package com.mitclass.hrleave.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.BrandPrimary
import com.mitclass.hrleave.core.theme.ButtonCornerRadius
import com.mitclass.hrleave.core.theme.ButtonMinHeight

/**
 * Full-width primary button at the shared 54dp min height / 14dp corner radius, so those two
 * values live in one place instead of being hand-applied per call site.
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    colors: ButtonColors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = Color.White),
    icon: (@Composable () -> Unit)? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = ButtonMinHeight),
        enabled = enabled && !loading,
        shape = RoundedCornerShape(ButtonCornerRadius),
        colors = colors,
        // STYLE_GUIDE.md: "Buttons: ... no elevation."
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
            disabledElevation = 0.dp,
        ),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = colors.contentColor,
            )
        } else if (icon != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon()
                Spacer(Modifier.width(AppSpacing.sm))
                Text(text, style = MaterialTheme.typography.labelLarge)
            }
        } else {
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/** Full-width outlined counterpart to [AppButton], same height/radius, for secondary actions. */
@Composable
fun AppOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentColor: Color = BrandPrimary,
    icon: (@Composable () -> Unit)? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = ButtonMinHeight),
        enabled = enabled,
        shape = RoundedCornerShape(ButtonCornerRadius),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor),
        border = BorderStroke(1.dp, contentColor),
    ) {
        if (icon != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon()
                Spacer(Modifier.width(AppSpacing.sm))
                Text(text, style = MaterialTheme.typography.labelLarge)
            }
        } else {
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/**
 * The plan's create-mode submit hierarchy: full-width primary [primaryText] over an outlined
 * [secondaryText], [AppSpacing.sm] apart.
 */
@Composable
fun SplitActionButtons(
    primaryText: String,
    onPrimaryClick: () -> Unit,
    secondaryText: String,
    onSecondaryClick: () -> Unit,
    modifier: Modifier = Modifier,
    primaryEnabled: Boolean = true,
    secondaryEnabled: Boolean = true,
    primaryLoading: Boolean = false,
) {
    Column(modifier = modifier) {
        AppButton(
            text = primaryText,
            onClick = onPrimaryClick,
            enabled = primaryEnabled,
            loading = primaryLoading,
        )
        Spacer(Modifier.height(AppSpacing.sm))
        AppOutlinedButton(
            text = secondaryText,
            onClick = onSecondaryClick,
            enabled = secondaryEnabled,
        )
    }
}
