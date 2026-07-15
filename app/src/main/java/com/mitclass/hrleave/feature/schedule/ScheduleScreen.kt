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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import com.mitclass.hrleave.core.theme.SuccessColor
import com.mitclass.hrleave.core.theme.WarningColor
import com.mitclass.hrleave.data.remote.dto.PublicHolidayDto
import com.mitclass.hrleave.data.remote.dto.ScheduleTeamLeaveEntryDto
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val HolidayColor = WarningColor
private val TeamLeaveColor = SuccessColor

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

            is ScheduleUiState.Error -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = current.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = viewModel::load, modifier = Modifier.padding(top = 12.dp)) { Text("Retry") }
                }
            }

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
            Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous month")
        }
        Text(
            text = "${yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${yearMonth.year}",
            style = MaterialTheme.typography.titleLarge,
        )
        IconButton(onClick = onNext) {
            Icon(Icons.Filled.ChevronRight, contentDescription = "Next month")
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

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                CalendarGrid(yearMonth = yearMonth, holidaysByDay = holidaysByDay, teamLeaveByDay = teamLeaveByDay)
                Legend()
                SectionHeader("Holidays in ${monthLabel(yearMonth)}")
            }
        }
        if (holidaysThisMonth.isEmpty()) {
            item { EmptyRow("No holidays this month") }
        } else {
            items(holidaysThisMonth, key = { "h-${it.id}" }) { holiday ->
                Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = holiday.name, style = MaterialTheme.typography.titleSmall)
                        Text(text = holiday.date, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        item {
            SectionHeader("Team Leave in ${monthLabel(yearMonth)}", modifier = Modifier.padding(horizontal = 16.dp))
        }
        if (teamLeaveThisMonth.isEmpty()) {
            item { EmptyRow("No team leave this month") }
        } else {
            items(teamLeaveThisMonth, key = { "t-${it.id}-${it.startDate}" }) { entry ->
                Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = entry.owner.fullName ?: entry.owner.email,
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            text = "${entry.leaveType.name} · ${entry.startDate} → ${entry.endDate}",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
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
        modifier = Modifier.padding(horizontal = 16.dp),
    )
}

@Composable
private fun Legend() {
    Row(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        LegendItem(color = HolidayColor, label = "Holiday")
        LegendItem(color = TeamLeaveColor, label = "Team leave")
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
    val leadingBlanks = firstOfMonth.dayOfWeek.value - 1 // Monday-first week
    val totalCells = leadingBlanks + daysInMonth
    val weeks = (totalCells + 6) / 7

    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su").forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
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
private fun CalendarDayCell(dayNumber: Int, hasHoliday: Boolean, hasTeamLeave: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = dayNumber.toString(), style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            if (hasHoliday) Box(modifier = Modifier.size(6.dp).background(color = HolidayColor, shape = CircleShape))
            if (hasTeamLeave) Box(modifier = Modifier.size(6.dp).background(color = TeamLeaveColor, shape = CircleShape))
        }
    }
}
