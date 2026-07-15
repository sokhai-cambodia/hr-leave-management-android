package com.mitclass.hrleave.core.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.mitclass.hrleave.core.theme.LightFieldFill
import com.mitclass.hrleave.core.theme.TextFieldCornerRadius
import com.mitclass.hrleave.core.ui.AppTextField

/**
 * Bold label + a filled InkWell-style trigger surface opening a search-filterable option
 * dialog, for relational fields (Task 10.3, restyled to the shared token set in Task 13.7).
 */
@Composable
fun SearchablePickerField(
    label: String,
    selectedLabel: String,
    options: List<PickerOption>,
    loading: Boolean,
    onSelected: (PickerOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(AppSpacing.sm))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true },
            shape = RoundedCornerShape(TextFieldCornerRadius),
            color = LightFieldFill,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .padding(horizontal = AppSpacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = selectedLabel.ifBlank { "Select $label" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedLabel.isNotBlank()) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.weight(1f),
                )
                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Choose $label")
            }
        }
    }

    if (showDialog) {
        var query by remember { mutableStateOf("") }
        val filtered = if (query.isBlank()) options else options.filter { it.label.contains(query, ignoreCase = true) }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Select $label") },
            text = {
                Column {
                    AppTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = "Search",
                    )
                    Spacer(Modifier.height(AppSpacing.sm))
                    if (loading) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            contentAlignment = Alignment.Center,
                        ) { CircularProgressIndicator() }
                    } else {
                        LazyColumn(modifier = Modifier.height(240.dp)) {
                            items(filtered, key = { it.id }) { option ->
                                Text(
                                    text = option.label,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onSelected(option)
                                            showDialog = false
                                        }
                                        .padding(vertical = 12.dp, horizontal = 4.dp),
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) { Text("Close") }
            },
        )
    }
}
