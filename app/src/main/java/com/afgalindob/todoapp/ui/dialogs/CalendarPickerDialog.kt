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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import com.afgalindob.todoapp.R
import java.time.Month
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

enum class CalendarViewMode {
    DAY,
    MONTH
}

@Composable
fun CalendarDialog(
    selectedDate: LocalDate?,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    color: Color = Color.White
) {
    Dialog(onDismissRequest = onDismiss) {

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Black
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
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            Column (
                modifier = Modifier
                    .background(Color.Black)
                    .padding(8.dp)
            ){

                Header(
                    currentMonth = currentMonth,
                    viewMode = viewMode,

                    onPrev = {
                        if (viewMode == CalendarViewMode.DAY) {
                            currentMonth = currentMonth.minusMonths(1)
                        } else {
                            currentMonth = currentMonth.minusYears(1)
                        }
                    },

                    onNext = {
                        if (viewMode == CalendarViewMode.DAY) {
                            currentMonth = currentMonth.plusMonths(1)
                        } else {
                            currentMonth = currentMonth.plusYears(1)
                        }
                    },

                    onMonthClick = {
                        viewMode =
                            if (viewMode == CalendarViewMode.DAY)
                                CalendarViewMode.MONTH
                            else
                                CalendarViewMode.DAY
                    },
                    color = color

                )

                when (viewMode) {

                    CalendarViewMode.DAY -> Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {

                            val days = listOf("Mo","Tu","We","Th","Fr","Sa","Su")

                            days.forEach {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )
                            }
                        }

                        DayView(
                            month = currentMonth,
                            color = color,
                            selectedDate = tempSelectedDate,
                            onDateSelected = { date ->

                                if (tempSelectedDate == date) {
                                    onDateSelected(date.format(formatter))
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
                        color = color
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
    onMonthClick: () -> Unit,
    color: Color
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
                tint = color
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
                .clickable { onMonthClick() }
                .padding(horizontal = 6.dp),
            style = MaterialTheme.typography.titleMedium,
            color = color
        )

        IconButton(onClick = onNext) {
            Icon(
                painter = painterResource(R.drawable.forward),
                contentDescription = "Next",
                tint = color
            )
        }
    }
}

@Composable
fun DayView(
    month: YearMonth,
    color: Color,
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
                    color =
                        if (selected)
                            MaterialTheme.colorScheme.onPrimary
                        else color
                )
            }
        }
    }
}

@Composable
fun MonthView(
    onMonthSelected: (Int) -> Unit,
    color: Color
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
                    color = color
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarPickerPreview() {

    var selectedDate by remember {
        mutableStateOf(LocalDate.now())
    }

    CalendarDialog(
        selectedDate = selectedDate,
        onDateSelected = {
            selectedDate = LocalDate.parse(it)
        },
        onDismiss = {}
    )
}