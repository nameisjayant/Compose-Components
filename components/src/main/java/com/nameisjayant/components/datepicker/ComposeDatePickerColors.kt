package com.nameisjayant.components.datepicker

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Colors used by [ComposeDatePicker]. Get the theme-based defaults from [ComposeDatePickerDefaults.colors],
 * derive a palette from one brand color with [ComposeDatePickerDefaults.accentColors], or tweak either with
 * [copy].
 */
@Immutable
public class ComposeDatePickerColors(
    public val containerColor: Color,
    public val titleContentColor: Color,
    public val headlineContentColor: Color,
    public val navigationContentColor: Color,
    public val weekdayContentColor: Color,
    public val weekendContentColor: Color,
    public val dayContentColor: Color,
    public val disabledDayContentColor: Color,
    public val adjacentMonthDayContentColor: Color,
    public val todayContentColor: Color,
    public val todayBorderColor: Color,
    public val selectedDayContainerColor: Color,
    public val selectedDayContentColor: Color,
    public val rangeContainerColor: Color,
    public val rangeContentColor: Color,
    public val decorationColor: Color,
    public val dividerColor: Color,
    public val yearContentColor: Color,
    public val currentYearContentColor: Color,
    public val selectedYearContainerColor: Color,
    public val selectedYearContentColor: Color,
) {

    /** Returns a copy with the given colors replaced. [Color.Unspecified] keeps the current color. */
    public fun copy(
        containerColor: Color = Color.Unspecified,
        titleContentColor: Color = Color.Unspecified,
        headlineContentColor: Color = Color.Unspecified,
        navigationContentColor: Color = Color.Unspecified,
        weekdayContentColor: Color = Color.Unspecified,
        weekendContentColor: Color = Color.Unspecified,
        dayContentColor: Color = Color.Unspecified,
        disabledDayContentColor: Color = Color.Unspecified,
        adjacentMonthDayContentColor: Color = Color.Unspecified,
        todayContentColor: Color = Color.Unspecified,
        todayBorderColor: Color = Color.Unspecified,
        selectedDayContainerColor: Color = Color.Unspecified,
        selectedDayContentColor: Color = Color.Unspecified,
        rangeContainerColor: Color = Color.Unspecified,
        rangeContentColor: Color = Color.Unspecified,
        decorationColor: Color = Color.Unspecified,
        dividerColor: Color = Color.Unspecified,
        yearContentColor: Color = Color.Unspecified,
        currentYearContentColor: Color = Color.Unspecified,
        selectedYearContainerColor: Color = Color.Unspecified,
        selectedYearContentColor: Color = Color.Unspecified,
    ): ComposeDatePickerColors = ComposeDatePickerColors(
        containerColor = containerColor.or(this.containerColor),
        titleContentColor = titleContentColor.or(this.titleContentColor),
        headlineContentColor = headlineContentColor.or(this.headlineContentColor),
        navigationContentColor = navigationContentColor.or(this.navigationContentColor),
        weekdayContentColor = weekdayContentColor.or(this.weekdayContentColor),
        weekendContentColor = weekendContentColor.or(this.weekendContentColor),
        dayContentColor = dayContentColor.or(this.dayContentColor),
        disabledDayContentColor = disabledDayContentColor.or(this.disabledDayContentColor),
        adjacentMonthDayContentColor = adjacentMonthDayContentColor.or(this.adjacentMonthDayContentColor),
        todayContentColor = todayContentColor.or(this.todayContentColor),
        todayBorderColor = todayBorderColor.or(this.todayBorderColor),
        selectedDayContainerColor = selectedDayContainerColor.or(this.selectedDayContainerColor),
        selectedDayContentColor = selectedDayContentColor.or(this.selectedDayContentColor),
        rangeContainerColor = rangeContainerColor.or(this.rangeContainerColor),
        rangeContentColor = rangeContentColor.or(this.rangeContentColor),
        decorationColor = decorationColor.or(this.decorationColor),
        dividerColor = dividerColor.or(this.dividerColor),
        yearContentColor = yearContentColor.or(this.yearContentColor),
        currentYearContentColor = currentYearContentColor.or(this.currentYearContentColor),
        selectedYearContainerColor = selectedYearContainerColor.or(this.selectedYearContainerColor),
        selectedYearContentColor = selectedYearContentColor.or(this.selectedYearContentColor),
    )

    private fun Color.or(fallback: Color): Color = if (this == Color.Unspecified) fallback else this

    private fun values() = listOf(
        containerColor, titleContentColor, headlineContentColor, navigationContentColor,
        weekdayContentColor, weekendContentColor, dayContentColor, disabledDayContentColor,
        adjacentMonthDayContentColor, todayContentColor, todayBorderColor, selectedDayContainerColor,
        selectedDayContentColor, rangeContainerColor, rangeContentColor, decorationColor, dividerColor,
        yearContentColor, currentYearContentColor, selectedYearContainerColor, selectedYearContentColor,
    )

    override fun equals(other: Any?): Boolean =
        this === other || (other is ComposeDatePickerColors && values() == other.values())

    override fun hashCode(): Int = values().hashCode()
}
