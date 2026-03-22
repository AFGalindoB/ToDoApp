package com.afgalindob.todoapp.utils

import com.afgalindob.todoapp.R
import com.afgalindob.todoapp.domain.TaskDomain
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

object TaskSections {
    val OVERDUE = R.string.section_overdue
    val TODAY = R.string.section_today
    val TOMORROW = R.string.section_tomorrow
    val THIS_WEEK = R.string.section_this_week
    val NEXT_WEEK = R.string.section_next_week
    val THIS_MONTH = R.string.section_this_month
    val NO_DATE = R.string.section_no_date
    val MORE = R.string.section_more
}

fun TaskDomain.getSection(): Int {
    val today = LocalDate.now()
    val taskDate = date?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    return when {
        taskDate == null -> TaskSections.NO_DATE
        taskDate.isBefore(today) -> TaskSections.OVERDUE
        taskDate.isEqual(today) -> TaskSections.TODAY
        taskDate.isEqual(today.plusDays(1)) -> TaskSections.TOMORROW
        taskDate.isAfter(today) && taskDate.isBefore(today.with(TemporalAdjusters.next(DayOfWeek.MONDAY))) -> TaskSections.THIS_WEEK
        taskDate.isAfter(today.with(TemporalAdjusters.next(DayOfWeek.SUNDAY))) &&
                taskDate.isBefore(today.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).plusWeeks(1)) -> TaskSections.NEXT_WEEK
        taskDate.isAfter(today) && taskDate.month == today.month -> TaskSections.THIS_MONTH
        else -> TaskSections.MORE
    }
}

fun sectionOrder(section: Int): Int {
    return when(section) {
        TaskSections.OVERDUE -> 0
        TaskSections.TODAY -> 1
        TaskSections.TOMORROW -> 2
        TaskSections.THIS_WEEK -> 3
        TaskSections.NEXT_WEEK -> 4
        TaskSections.THIS_MONTH -> 5
        TaskSections.NO_DATE -> 7
        else -> 6
    }
}