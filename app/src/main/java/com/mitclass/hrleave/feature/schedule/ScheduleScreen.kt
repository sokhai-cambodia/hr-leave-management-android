package com.mitclass.hrleave.feature.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mitclass.hrleave.core.theme.AppSpacing
import com.mitclass.hrleave.core.theme.BrandPrimary
import com.mitclass.hrleave.core.theme.WarningColor
import com.mitclass.hrleave.core.ui.ErrorStateView
import com.mitclass.hrleave.data.remote.dto.PublicHolidayDto
import com.mitclass.hrleave.data.remote.dto.ScheduleTeamLeaveEntryDto
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val HolidayColor = WarningColor
private val TeamLeaveColor = BrandPrimary

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val yearMonth by viewModel.yearMonth.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        MonthHeader(yearMonth = yearMonth, onPrevious = viewModel::previousMonth, onNext = viewModel::nextMonth)
        when (val current = state) {
            is ScheduleUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            is ScheduleUiState.Error -> ErrorStateView(message = current.message, onRetry = viewModel::load)

            is ScheduleUiState.Loaded -> ScheduleContent(yearMonth = yearMonth, holidays = current.holidays, teamLeave = current.teamLeave)
        }
    }
}

@Composable
private fun MonthHeader(yearMonth: YearMonth, onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Outlined.ChevronLeft, contentDescription = "Previous month")
        }
        Text(
            text = "${yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${yearMonth.year}",
            style = MaterialTheme.typography.titleLarge,
        )
        IconButton(onClick = onNext) {
            Icon(Icons.Outlined.ChevronRight, contentDescription = "Next month")
        }
    }
}

@Composable
private fun ScheduleContent(
    yearMonth: YearMonth,
    holidays: List<PublicHolidayDto>,
    teamLeave: List<ScheduleTeamLeaveEntryDto>,
) {
    val holidaysByDay = remember(holidays) {
        groupByDay(holidays, startDate = { it.holidayDate() }, endDate = { it.holidayDate() })
    }
    val teamLeaveByDay = remember(teamLeave) {
        groupByDay(teamLeave, startDate = { LocalDate.parse(it.startDate) }, endDate = { LocalDate.parse(it.endDate) })
    }
    val holidaysThisMonth = remember(holidays, yearMonth) {
        filterByMonth(holidays, yearMonth.year, yearMonth.monthValue, { it.holidayDate() }, { it.holidayDate() })
    }
    val teamLeaveThisMonth = remember(teamLeave, yearMonth) {
        filterByMonth(
            teamLeave,
            yearMonth.year,
            yearMonth.monthValue,
            { LocalDate.parse(it.startDate) },
            { LocalDate.parse(it.endDate) },
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = AppSpacing.lg)) {
        item {
            Column {
                CalendarGrid(yearMonth = yearMonth, holidaysByDay = holidaysByDay, teamLeaveByDay = teamLeaveByDay)
                Legend()
                SectionHeader("Holidays in ${monthLabel(yearMonth)}")
            }
        }
        if (holidaysThisMonth.isEmpty()) {
            item { EmptyRow("No public holidays this month.") }
        } else {
            itemsIndexed(holidaysThisMonth, key = { _, item -> "h-${item.id}" }) { index, holiday ->
                Column(modifier = Modifier.padding(vertical = AppSpacing.sm)) {
                    Text(text = holiday.name, style = MaterialTheme.typography.titleSmall)
                    Text(text = holiday.date, style = MaterialTheme.typography.bodySmall)
                }
                if (index != holidaysThisMonth.lastIndex) HorizontalDivider()
            }
        }
        item {
            SectionHeader("Team Leave in ${monthLabel(yearMonth)}")
        }
        if (teamLeaveThisMonth.isEmpty()) {
            item { EmptyRow("No team leave this month.") }
        } else {
            itemsIndexed(teamLeaveThisMonth, key = { _, item -> "t-${item.id}-${item.startDate}" }) { index, entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AppSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(color = TeamLeaveColor.copy(alpha = 0.15f), shape = CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = TeamLeaveColor,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Column(modifier = Modifier.padding(start = AppSpacing.md).weight(1f)) {
                        Text(
                            text = entry.owner.fullName ?: entry.owner.email,
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            text = entry.leaveType.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = "${entry.startDate} – ${entry.endDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (index != teamLeaveThisMonth.lastIndex) HorizontalDivider()
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

private fun monthLabel(yearMonth: YearMonth) = yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())

@Composable
private fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier.padding(top = 16.dp, bottom = 8.dp),
    )
}

@Composable
private fun EmptyRow(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun Legend() {
    Row(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        LegendItem(color = HolidayColor, label = "Public Holiday")
        LegendItem(color = TeamLeaveColor, label = "Team Leave")
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color = color, shape = CircleShape))
        Text(text = label, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
private fun CalendarGrid(
    yearMonth: YearMonth,
    holidaysByDay: Map<LocalDate, List<PublicHolidayDto>>,
    teamLeaveByDay: Map<LocalDate, List<ScheduleTeamLeaveEntryDto>>,
) {
    val firstOfMonth = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    // Sunday-first week, matching the Flutter client's actual calendar layout.
    val leadingBlanks = firstOfMonth.dayOfWeek.value % 7
    val totalCells = leadingBlanks + daysInMonth
    val weeks = (totalCells + 6) / 7

    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        val today = LocalDate.now()
        for (week in 0 until weeks) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (dayOfWeek in 0 until 7) {
                    val cellIndex = week * 7 + dayOfWeek
                    val dayNumber = cellIndex - leadingBlanks + 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (dayNumber in 1..daysInMonth) {
                            val date = yearMonth.atDay(dayNumber)
                            CalendarDayCell(
                                dayNumber = dayNumber,
                                isToday = date == today,
                                hasHoliday = holidaysByDay.containsKey(date),
                                hasTeamLeave = teamLeaveByDay.containsKey(date),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(dayNumber: Int, isToday: Boolean, hasHoliday: Boolean, hasTeamLeave: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    color = if (isToday) BrandPrimary.copy(alpha = 0.15f) else Color.Transparent,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = dayNumber.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (isToday) BrandPrimary else MaterialTheme.colorScheme.onSurface,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            if (hasHoliday) Box(modifier = Modifier.size(6.dp).background(color = HolidayColor, shape = CircleShape))
            if (hasTeamLeave) Box(modifier = Modifier.size(6.dp).background(color = TeamLeaveColor, shape = CircleShape))
        }
    }
}
