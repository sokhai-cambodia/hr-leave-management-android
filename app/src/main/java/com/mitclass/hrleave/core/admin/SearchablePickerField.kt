package com.mitclass.hrleave.core.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Read-only field + a search-filterable option dialog, for relational fields (Task 10.3). */
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
    OutlinedTextField(
        value = selectedLabel,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Choose $label")
            }
        },
        modifier = modifier.fillMaxWidth(),
    )

    if (showDialog) {
        var query by remember { mutableStateOf("") }
        val filtered = if (query.isBlank()) options else options.filter { it.label.contains(query, ignoreCase = true) }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Select $label") },
            text = {
                Column {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Search") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
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
