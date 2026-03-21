package com.afgalindob.todoapp.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import com.afgalindob.todoapp.R
import java.time.Month
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.res.stringResource
import com.afgalindob.todoapp.utils.DateUtils
import java.time.format.TextStyle
import java.util.Locale

enum class CalendarViewMode {
    DAY,
    MONTH
}

@Composable
fun CalendarDialog(
    selectedDate: LocalDate?,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
    contentColor: Color = Color.White,
    backgroundColor: Color = Color.Black
) {
    Dialog(onDismissRequest = onDismiss) {

        Card(
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor,
                contentColor = contentColor
            )
        ) {

            var currentMonth by remember(selectedDate) {
                mutableStateOf(
                    selectedDate?.let { YearMonth.from(it) } ?: YearMonth.now()
                )
            }
            var viewMode by remember { mutableStateOf(CalendarViewMode.DAY) }

            var tempSelectedDate by remember(selectedDate) {
                mutableStateOf(selectedDate)
            }

            Column (
                modifier = Modifier
                    .padding(8.dp)
            ){

                Header(
                    currentMonth = currentMonth,
                    viewMode = viewMode,

                    onPrev = {
                        currentMonth =
                            if (viewMode == CalendarViewMode.DAY)
                                currentMonth.minusMonths(1)
                            else
                                currentMonth.minusYears(1)

                    },

                    onNext = {
                        currentMonth =
                            if (viewMode == CalendarViewMode.DAY)
                                 currentMonth.plusMonths(1)
                            else
                                 currentMonth.plusYears(1)

                    },

                    onLabelClick = {
                        viewMode =
                            if (viewMode == CalendarViewMode.DAY)
                                CalendarViewMode.MONTH
                            else
                                CalendarViewMode.DAY
                    },

                    contentColor = contentColor

                )

                when (viewMode) {

                    CalendarViewMode.DAY -> Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {

                            val days = listOf(
                                R.string.monday_day_picker,
                                R.string.tuesday_day_picker,
                                R.string.wednesday_day_picker,
                                R.string.thursday_day_picker,
                                R.string.friday_day_picker,
                                R.string.saturday_day_picker,
                                R.string.sunday_day_picker
                            )

                            days.forEachIndexed { index, day ->

                                val isLast = index == days.lastIndex

                                Text(
                                    text = stringResource(day),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isLast) Color.Magenta else contentColor
                                )
                            }
                        }

                        DayView(
                            month = currentMonth,
                            contentColor = contentColor,
                            selectedDate = tempSelectedDate,
                            onDateSelected = { date ->

                                if (tempSelectedDate == date) {
                                    onDateSelected(DateUtils.toTimestamp(date))
                                    onDismiss()
                                } else {
                                    tempSelectedDate = date
                                }
                            }
                        )
                    }

                    CalendarViewMode.MONTH -> MonthView(
                        onMonthSelected = {
                            currentMonth = YearMonth.of(currentMonth.year, it)
                            viewMode = CalendarViewMode.DAY
                        },
                        contentColor = contentColor
                    )
                }
            }
        }
    }
}

@Composable
fun Header(
    currentMonth: YearMonth,
    viewMode: CalendarViewMode,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onLabelClick: () -> Unit,
    contentColor: Color
) {

    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = onPrev) {
            Icon(
                painter = painterResource(R.drawable.previous),
                contentDescription = "Previous",
                tint = contentColor
            )
        }

        Text(
            text =
                if (viewMode == CalendarViewMode.DAY) {

                    val monthName = currentMonth.month.getDisplayName(
                        TextStyle.FULL,
                        Locale.getDefault()
                    )

                    monthName.replaceFirstChar { it.uppercase() } +
                            " " + currentMonth.year

                } else {
                    currentMonth.year.toString()
                },
            modifier = Modifier
                .clickable { onLabelClick() }
                .padding(horizontal = 6.dp),
            style = MaterialTheme.typography.titleMedium,
            color = contentColor
        )

        IconButton(onClick = onNext) {
            Icon(
                painter = painterResource(R.drawable.forward),
                contentDescription = "Next",
                tint = contentColor
            )
        }
    }
}

@Composable
fun DayView(
    month: YearMonth,
    contentColor: Color,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {

    val daysInMonth = month.lengthOfMonth()
    val firstDay = month.atDay(1).dayOfWeek.value

    LazyVerticalGrid(columns = GridCells.Fixed(7)) {

        items(firstDay - 1) {
            Box {}
        }

        items(daysInMonth) { index ->

            val day = index + 1
            val date = month.atDay(day)

            val selected = date == selectedDate

            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected)
                            MaterialTheme.colorScheme.primary
                        else Color.Transparent
                    )
                    .clickable {
                        onDateSelected(date)
                    }
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = day.toString(),
                    color = if (selected) MaterialTheme.colorScheme.onPrimary
                            else contentColor
                )
            }
        }
    }
}

@Composable
fun MonthView(
    onMonthSelected: (Int) -> Unit,
    contentColor: Color
) {

    val months = Month.entries.toTypedArray()

    LazyVerticalGrid(columns = GridCells.Fixed(3)) {

        items(months.size) { index ->

            val month = months[index]

            Box(
                modifier = Modifier
                    .padding(3.dp)
                    .clickable {
                        onMonthSelected(month.value)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = month.getDisplayName(
                        TextStyle.FULL,
                        Locale.getDefault()
                    ).replaceFirstChar { it.uppercase() },
                    color = contentColor
                )
            }
        }
    }
}