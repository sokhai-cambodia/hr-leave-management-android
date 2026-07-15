package com.mitclass.hrleave.core.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mitclass.hrleave.core.ui.OnResume

/** Generic paged/searchable list + create/edit/delete UI, unmodified across every resource (Task 10.1). */
@Composable
fun <T> GenericCrudListScreen(engine: CrudEngine<T>) {
    val listState by engine.listState.collectAsState()
    val searchQuery by engine.searchQuery.collectAsState()
    val deletingIds by engine.deletingIds.collectAsState()
    var pendingDelete by remember { mutableStateOf<T?>(null) }
    OnResume(onResume = engine::load)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = engine::startCreate) {
                Icon(Icons.Filled.Add, contentDescription = "Create")
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = engine::onSearchQueryChange,
                label = { Text("Search") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
            when (val current = listState) {
                is CrudListUiState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }

                is CrudListUiState.Error -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = current.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = engine::load, modifier = Modifier.padding(top = 12.dp)) { Text("Retry") }
                    }
                }

                is CrudListUiState.Loaded -> {
                    val visible = engine.visibleItems()
                    if (visible.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "Nothing here yet", style = MaterialTheme.typography.bodyLarge)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(visible, key = { engine.adapter.id(it) }) { item ->
                                CrudRow(
                                    title = engine.adapter.title(item),
                                    subtitle = engine.adapter.subtitle(item),
                                    isDeleting = engine.adapter.id(item) in deletingIds,
                                    onClick = { engine.startEdit(item) },
                                    onDeleteClick = { pendingDelete = item },
                                )
                            }
                            if (current.canLoadMore && searchQuery.isBlank()) {
                                item {
                                    LaunchedEffect(Unit) { engine.loadMore() }
                                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                        if (current.isLoadingMore) CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    GenericCrudFormDialog(engine = engine)

    pendingDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete this record?") },
            text = { Text("This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    engine.delete(item)
                    pendingDelete = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun CrudRow(
    title: String,
    subtitle: String,
    isDeleting: Boolean,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (isDeleting) {
                CircularProgressIndicator(modifier = Modifier.padding(8.dp))
            } else {
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}
