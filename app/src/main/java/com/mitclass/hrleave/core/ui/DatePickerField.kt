package com.mitclass.hrleave.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.LightBorder
import com.mitclass.hrleave.core.theme.TextFieldCornerRadius
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Bold label + an outlined (white bg + border) trigger surface — distinct from the filled
 * AppTextField convention, matching the Flutter client's actual date fields (`ui/leave-form.jpg`)
 * — opening the same Material3 date picker dialog. Shared by every start/end/single-date field.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    date: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(AppSpacing.sm))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled) { showDialog = true },
            shape = RoundedCornerShape(TextFieldCornerRadius),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, LightBorder),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .padding(horizontal = AppSpacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = date?.toString() ?: "Select Date",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (date != null) LocalContentColor.current else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                Icon(Icons.Outlined.CalendarMonth, contentDescription = "Pick date")
            }
        }
    }

    if (showDialog) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = date?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        onDateSelected(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())
                    }
                    showDialog = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = state)
        }
    }
}
