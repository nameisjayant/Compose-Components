package com.nameisjayant.composecomponents.datepicker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.components.datepicker.CalendarDate
import com.nameisjayant.components.datepicker.ComposeDatePicker
import com.nameisjayant.components.datepicker.ComposeDatePickerDefaults
import com.nameisjayant.components.datepicker.ComposeDatePickerState
import com.nameisjayant.components.datepicker.DateSelectionMode
import com.nameisjayant.components.datepicker.DayDecoration
import com.nameisjayant.components.datepicker.SelectableDates
import com.nameisjayant.components.datepicker.WeekDay
import com.nameisjayant.composecomponents.ui.components.CardShape
import com.nameisjayant.composecomponents.ui.theme.ComposeComponentsTheme

private val Oct22 = CalendarDate(2026, 10, 22)

@Composable
private fun RangeSample() {
    val state = remember {
        ComposeDatePickerState(
            selectionMode = DateSelectionMode.Range,
            initialSelectedRange = Oct22..Oct22.plusDays(6),
            selectableDates = SelectableDates.except(listOf(CalendarDate(2026, 10, 12))),
        )
    }
    Surface(color = MaterialTheme.colorScheme.background) {
        ComposeDatePicker(
            state = state,
            shape = CardShape,
            firstDayOfWeek = WeekDay.Monday,
            showAdjacentMonthDays = true,
            strikeThroughDisabledDays = true,
            decoration = { DayDecoration(label = "$" + (79 + (it.epochDay * 37).mod(60L))) },
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Composable
private fun MultipleSample() {
    val state = remember {
        ComposeDatePickerState(
            selectionMode = DateSelectionMode.Multiple,
            initialSelectedDates = setOf(Oct22, Oct22.plusDays(5), Oct22.minusDays(8)),
            selectableDates = SelectableDates.WeekdaysOnly,
        )
    }
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(Modifier.padding(16.dp)) {
            ComposeDatePicker(
                state = state,
                shape = CardShape,
                colors = ComposeDatePickerDefaults.accentColors(Color(0xFF0F8A66)),
                dayShape = ComposeDatePickerDefaults.RoundedDayShape,
                highlightWeekends = true,
                headline = null,
                decoration = { if (it.dayOfMonth % 6 == 0) DayDecoration.dot() else null },
            )
        }
    }
}

@Preview
@Composable
private fun RangeLightPreview() = ComposeComponentsTheme(darkTheme = false) { RangeSample() }

@Preview
@Composable
private fun RangeDarkPreview() = ComposeComponentsTheme(darkTheme = true) { RangeSample() }

@Preview
@Composable
private fun MultipleLightPreview() = ComposeComponentsTheme(darkTheme = false) { MultipleSample() }

@Preview
@Composable
private fun MultipleDarkPreview() = ComposeComponentsTheme(darkTheme = true) { MultipleSample() }
