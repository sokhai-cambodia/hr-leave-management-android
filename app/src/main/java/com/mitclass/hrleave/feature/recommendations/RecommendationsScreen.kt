package com.mitclass.hrleave.feature.recommendations

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.CardCornerRadius
import com.mitclass.hrleave.core.theme.CardElevation
import com.mitclass.hrleave.core.ui.AppButton
import com.mitclass.hrleave.core.ui.EmptyStateView
import com.mitclass.hrleave.core.ui.ErrorStateView
import com.mitclass.hrleave.data.remote.dto.LeaveRecommendationDto
import java.time.LocalDate
import java.util.Locale

@Composable
fun RecommendationsScreen(
    onUseSelectedDates: (leaveTypeId: String, dates: List<LocalDate>) -> Unit,
    viewModel: RecommendationsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val year by viewModel.selectedYear.collectAsState()
    val selectedDates by viewModel.selectedDates.collectAsState()

    Scaffold(
        bottomBar = {
            val loaded = state as? RecommendationsUiState.Loaded
            if (loaded != null && selectedDates.isNotEmpty()) {
                AppButton(
                    text = "Use ${selectedDates.size} selected date(s)",
                    onClick = {
                        onUseSelectedDates(loaded.leaveTypeId, selectedDates.map(LocalDate::parse).sorted())
                    },
                    modifier = Modifier.padding(AppSpacing.lg),
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            YearSelector(year = year, onYearChange = viewModel::onYearSelected)
            when (val current = state) {
                is RecommendationsUiState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }

                is RecommendationsUiState.Error -> ErrorStateView(message = current.message, onRetry = viewModel::load)

                is RecommendationsUiState.Loaded -> {
                    if (current.items.isEmpty()) {
                        EmptyStateView(message = "No recommendations for $year")
                    } else {
                        val allDates = current.items.map { it.leaveDate }
                        SelectAllRow(
                            allSelected = selectedDates.size == allDates.size,
                            onToggleAll = { viewModel.toggleSelectAll(allDates) },
                        )
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(current.items, key = { it.leaveDate }) { item ->
                                RecommendationRow(
                                    item = item,
                                    selected = item.leaveDate in selectedDates,
                                    onToggle = { viewModel.toggleDateSelection(item.leaveDate) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun YearSelector(year: Int, onYearChange: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = { onYearChange(year - 1) }) {
            Icon(Icons.Outlined.ChevronLeft, contentDescription = "Previous year")
        }
        Text(text = year.toString(), style = MaterialTheme.typography.titleLarge)
        IconButton(onClick = { onYearChange(year + 1) }) {
            Icon(Icons.Outlined.ChevronRight, contentDescription = "Next year")
        }
    }
}

@Composable
private fun SelectAllRow(allSelected: Boolean, onToggleAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = allSelected, onCheckedChange = { onToggleAll() })
        Text(text = "Select all")
    }
}

@Composable
private fun RecommendationRow(item: LeaveRecommendationDto, selected: Boolean, onToggle: () -> Unit) {
    Card(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CardCornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = CardElevation),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(checked = selected, onCheckedChange = { onToggle() })
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.leaveDate, style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (item.bridgeHoliday) {
                        AssistChip(onClick = {}, label = { Text("Bridge holiday") })
                    }
                    AssistChip(onClick = {}, label = { Text("Team workload: ${item.teamWorkload}") })
                }
            }
            Text(
                text = String.format(Locale.US, "%.1f", item.predictedScore),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
