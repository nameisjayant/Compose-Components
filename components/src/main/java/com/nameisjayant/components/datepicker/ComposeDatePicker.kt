package com.nameisjayant.components.datepicker

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nameisjayant.components.R
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs

/**
 * A month calendar for picking one date, a date range, or several dates.
 *
 * - Swipe or use the arrows to change month; tap the month title to jump to a year.
 * - Disable dates with [ComposeDatePickerState.selectableDates] and the state's min/max dates.
 * - Mark days with [decoration] (dots, small labels, tinted backgrounds), or take over the inside of
 *   each day completely with [dayContent].
 * - Restyle it with [colors] (theme-based by default), [dayShape] and [shape], and by wrapping it in a
 *   `MaterialTheme` with your own typography.
 *
 * ```
 * val state = rememberComposeDatePickerState(selectionMode = DateSelectionMode.Range)
 * ComposeDatePicker(state)
 * // state.selectedRange → 2026-10-22..2026-10-28
 * ```
 *
 * @param state selection, displayed month and selection rules. See [rememberComposeDatePickerState].
 * @param colors colors of every part of the picker.
 * @param shape outline of the whole picker; also clips its background.
 * @param dayShape shape of the selection, today's outline and decoration backgrounds.
 * @param firstDayOfWeek the leftmost column; the locale's first day by default.
 * @param showAdjacentMonthDays show the neighbouring months' days in the leading/trailing cells (not tappable).
 * @param highlightToday outline today's date.
 * @param highlightWeekends color Saturday and Sunday with [ComposeDatePickerColors.weekendContentColor].
 * @param strikeThroughDisabledDays strike through disabled days, in addition to dimming them.
 * @param yearSelectionEnabled let the month title open a year grid.
 * @param decoration decoration per day, or `null` for none. Called for every visible day, so keep it cheap.
 * @param title small label at the top, or `null` to hide it.
 * @param headline large selection summary, or `null` to hide it. Hiding both also hides the divider.
 * @param dayContent replaces what is drawn inside each day's shape (selection background, today's
 *   outline and the range band are still drawn by the picker). [LocalContentColor] is set to the
 *   color the default day text would use.
 */
@Composable
public fun ComposeDatePicker(
    state: ComposeDatePickerState,
    modifier: Modifier = Modifier,
    colors: ComposeDatePickerColors = ComposeDatePickerDefaults.colors(),
    shape: Shape = RectangleShape,
    dayShape: Shape = ComposeDatePickerDefaults.DayShape,
    firstDayOfWeek: WeekDay = ComposeDatePickerDefaults.firstDayOfWeek(),
    showAdjacentMonthDays: Boolean = false,
    highlightToday: Boolean = true,
    highlightWeekends: Boolean = false,
    strikeThroughDisabledDays: Boolean = false,
    yearSelectionEnabled: Boolean = true,
    decoration: ((CalendarDate) -> DayDecoration?)? = null,
    title: (@Composable () -> Unit)? = { ComposeDatePickerDefaults.Title(state) },
    headline: (@Composable () -> Unit)? = { ComposeDatePickerDefaults.Headline(state) },
    dayContent: (@Composable (DayInfo) -> Unit)? = null,
) {
    val locale = currentLocale()
    val today = remember { CalendarDate.today() }
    val minMonth = state.minDate.calendarMonth
    val pageCount = minMonth.monthsUntil(state.maxDate.calendarMonth) + 1
    val pagerState = remember(state) {
        PagerState(currentPage = minMonth.monthsUntil(state.displayedMonth)) { pageCount }
    }
    var yearPickerVisible by rememberSaveable { mutableStateOf(false) }

    // Swipes and arrow taps settle the pager → state follows.
    LaunchedEffect(state, pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { state.displayedMonth = minMonth.plusMonths(it) }
    }
    // Code (or the year grid) sets state.displayedMonth → pager follows.
    LaunchedEffect(state, pagerState, state.displayedMonth) {
        val target = minMonth.monthsUntil(state.displayedMonth)
        if (target != pagerState.settledPage && target != pagerState.targetPage) {
            if (abs(target - pagerState.currentPage) == 1) pagerState.animateScrollToPage(target)
            else pagerState.scrollToPage(target)
        }
    }

    Column(
        modifier
            .clip(shape)
            .background(colors.containerColor)
            .padding(bottom = 8.dp),
    ) {
        if (title != null || headline != null) {
            Column(Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp, bottom = 12.dp)) {
                if (title != null) {
                    CompositionLocalProvider(LocalContentColor provides colors.titleContentColor, content = title)
                    if (headline != null) Spacer(Modifier.height(20.dp))
                }
                if (headline != null) {
                    CompositionLocalProvider(LocalContentColor provides colors.headlineContentColor, content = headline)
                }
            }
            HorizontalDivider(color = colors.dividerColor)
        }

        MonthHeader(
            month = minMonth.plusMonths(pagerState.targetPage),
            locale = locale,
            colors = colors,
            yearPickerVisible = yearPickerVisible,
            yearSelectionEnabled = yearSelectionEnabled,
            canGoBack = pagerState.targetPage > 0,
            canGoForward = pagerState.targetPage < pageCount - 1,
            onToggleYearPicker = { yearPickerVisible = !yearPickerVisible },
            pagerState = pagerState,
        )

        Box(
            Modifier
                .fillMaxWidth()
                .height(WeekdayRowHeight + DayRowHeight * 6)
                .padding(horizontal = 12.dp),
        ) {
            Crossfade(targetState = yearPickerVisible && yearSelectionEnabled, label = "DatePickerMode") { showYears ->
                if (showYears) {
                    YearGrid(
                        state = state,
                        currentYear = today.year,
                        colors = colors,
                        locale = locale,
                        onYearSelected = { year ->
                            state.displayedMonth = CalendarMonth(year, state.displayedMonth.month)
                            yearPickerVisible = false
                        },
                    )
                } else {
                    Column(Modifier.fillMaxSize()) {
                        WeekdayRow(firstDayOfWeek, locale, colors, highlightWeekends)
                        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { page ->
                            MonthGrid(
                                month = minMonth.plusMonths(page),
                                state = state,
                                today = today,
                                firstDayOfWeek = firstDayOfWeek,
                                colors = colors,
                                dayShape = dayShape,
                                showAdjacentMonthDays = showAdjacentMonthDays,
                                highlightToday = highlightToday,
                                highlightWeekends = highlightWeekends,
                                strikeThroughDisabledDays = strikeThroughDisabledDays,
                                decoration = decoration,
                                dayContent = dayContent,
                                locale = locale,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthHeader(
    month: CalendarMonth,
    locale: Locale,
    colors: ComposeDatePickerColors,
    yearPickerVisible: Boolean,
    yearSelectionEnabled: Boolean,
    canGoBack: Boolean,
    canGoForward: Boolean,
    onToggleYearPicker: () -> Unit,
    pagerState: PagerState,
) {
    val scope = rememberCoroutineScope()
    val arrowRotation by animateFloatAsState(if (yearPickerVisible) 180f else 0f, label = "YearArrow")
    val toggleLabel = stringResource(if (yearPickerVisible) R.string.ccdp_switch_to_day else R.string.ccdp_switch_to_year)

    CompositionLocalProvider(LocalContentColor provides colors.navigationContentColor) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(start = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable(enabled = yearSelectionEnabled, role = Role.Button, onClickLabel = toggleLabel) {
                        onToggleYearPicker()
                    }
                    .padding(start = 12.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AnimatedContent(
                    targetState = month,
                    transitionSpec = {
                        val forward = targetState > initialState
                        (slideInHorizontally { if (forward) it / 3 else -it / 3 } + fadeIn())
                            .togetherWith(slideOutHorizontally { if (forward) -it / 3 else it / 3 } + fadeOut())
                    },
                    label = "MonthTitle",
                ) { shown ->
                    Text(
                        text = shown.format("MMMMy", locale),
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
                if (yearSelectionEnabled) {
                    Icon(
                        imageVector = DatePickerIcons.DropDown,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(arrowRotation),
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            if (!yearPickerVisible) {
                IconButton(
                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.targetPage - 1) } },
                    enabled = canGoBack,
                ) {
                    Icon(DatePickerIcons.ChevronLeft, stringResource(R.string.ccdp_previous_month))
                }
                IconButton(
                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.targetPage + 1) } },
                    enabled = canGoForward,
                ) {
                    Icon(DatePickerIcons.ChevronRight, stringResource(R.string.ccdp_next_month))
                }
            }
        }
    }
}

@Composable
private fun WeekdayRow(
    firstDayOfWeek: WeekDay,
    locale: Locale,
    colors: ComposeDatePickerColors,
    highlightWeekends: Boolean,
) {
    val names = remember(locale) { weekdayNames(locale) }
    Row(Modifier.fillMaxWidth().height(WeekdayRowHeight), verticalAlignment = Alignment.CenterVertically) {
        repeat(7) { column ->
            val day = firstDayOfWeek + column
            val isWeekend = day == WeekDay.Saturday || day == WeekDay.Sunday
            Text(
                text = names.getValue(day),
                modifier = Modifier.weight(1f),
                color = if (highlightWeekends && isWeekend) colors.weekendContentColor else colors.weekdayContentColor,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun MonthGrid(
    month: CalendarMonth,
    state: ComposeDatePickerState,
    today: CalendarDate,
    firstDayOfWeek: WeekDay,
    colors: ComposeDatePickerColors,
    dayShape: Shape,
    showAdjacentMonthDays: Boolean,
    highlightToday: Boolean,
    highlightWeekends: Boolean,
    strikeThroughDisabledDays: Boolean,
    decoration: ((CalendarDate) -> DayDecoration?)?,
    dayContent: (@Composable (DayInfo) -> Unit)?,
    locale: Locale,
) {
    val gridStart = remember(month, firstDayOfWeek) {
        val first = month.firstDay
        first.minusDays((first.dayOfWeek.isoNumber - firstDayOfWeek.isoNumber).mod(7).toLong())
    }
    val isRange = state.selectionMode == DateSelectionMode.Range
    val rangeStart = state.rangeStart
    val rangeEnd = state.rangeEnd

    Column(Modifier.fillMaxWidth()) {
        repeat(6) { week ->
            Row(Modifier.fillMaxWidth().height(DayRowHeight)) {
                repeat(7) { column ->
                    val date = gridStart.plusDays(week * 7L + column)
                    val inMonth = date in month
                    if (!inMonth && !showAdjacentMonthDays) {
                        Spacer(Modifier.weight(1f))
                    } else {
                        val inRange = isRange && inMonth && rangeStart != null && rangeEnd != null &&
                            date >= rangeStart && date <= rangeEnd
                        val info = DayInfo(
                            date = date,
                            isCurrentMonth = inMonth,
                            isEnabled = inMonth && state.isSelectable(date),
                            isSelected = inMonth && state.isSelected(date),
                            isToday = date == today,
                            isRangeStart = inRange && date == rangeStart,
                            isRangeEnd = inRange && date == rangeEnd,
                            isInRange = inRange,
                            decoration = if (inMonth) decoration?.invoke(date) else null,
                        )
                        Day(
                            info = info,
                            colors = colors,
                            dayShape = dayShape,
                            drawRangeBand = inRange && rangeStart != rangeEnd,
                            highlightToday = highlightToday,
                            highlightWeekends = highlightWeekends,
                            strikeThroughDisabledDays = strikeThroughDisabledDays,
                            dayContent = dayContent,
                            locale = locale,
                            onClick = { state.select(date) },
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Day(
    info: DayInfo,
    colors: ComposeDatePickerColors,
    dayShape: Shape,
    drawRangeBand: Boolean,
    highlightToday: Boolean,
    highlightWeekends: Boolean,
    strikeThroughDisabledDays: Boolean,
    dayContent: (@Composable (DayInfo) -> Unit)?,
    locale: Locale,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val decoration = info.decoration
    val showToday = highlightToday && info.isToday && info.isCurrentMonth
    val containerColor by animateColorAsState(
        targetValue = when {
            info.isSelected -> colors.selectedDayContainerColor
            info.isEnabled && !info.isInRange && decoration != null -> decoration.containerColor.takeOrElse { Color.Transparent }
            else -> Color.Transparent
        },
        label = "DayContainer",
    )
    val contentColor by animateColorAsState(
        targetValue = when {
            info.isSelected -> colors.selectedDayContentColor
            !info.isCurrentMonth -> colors.adjacentMonthDayContentColor
            !info.isEnabled -> colors.disabledDayContentColor
            info.isInRange -> colors.rangeContentColor
            decoration != null && decoration.contentColor != Color.Unspecified -> decoration.contentColor
            showToday -> colors.todayContentColor
            highlightWeekends && info.date.isWeekend -> colors.weekendContentColor
            else -> colors.dayContentColor
        },
        label = "DayContent",
    )
    val bandColor = colors.rangeContainerColor
    val spoken = remember(info.date, locale) { info.date.spokenLabel(locale) }
    val stateLabel = when {
        info.isRangeStart -> stringResource(R.string.ccdp_range_start_label)
        info.isRangeEnd -> stringResource(R.string.ccdp_range_end_label)
        info.isInRange -> stringResource(R.string.ccdp_in_range_label)
        showToday -> stringResource(R.string.ccdp_today)
        else -> null
    }

    Box(
        modifier.drawBehind {
            if (!drawRangeBand) return@drawBehind
            val band = DayCellSize.toPx()
            val center = size.width / 2
            val ltr = layoutDirection == LayoutDirection.Ltr
            // The band runs edge to edge, except it starts/ends at the middle of the range's first/last day.
            var left = 0f
            var right = size.width
            if (info.isRangeStart) if (ltr) left = center else right = center
            if (info.isRangeEnd) if (ltr) right = center else left = center
            drawRect(bandColor, Offset(left, (size.height - band) / 2), Size(right - left, band))
        },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(DayCellSize)
                .clip(dayShape)
                .background(containerColor, dayShape)
                .then(
                    if (showToday && !info.isSelected) Modifier.border(1.dp, colors.todayBorderColor, dayShape)
                    else Modifier,
                )
                .semantics {
                    contentDescription = spoken
                    selected = info.isSelected
                    if (stateLabel != null) stateDescription = stateLabel
                }
                .clickable(enabled = info.isEnabled, role = Role.Button, onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                if (dayContent != null) {
                    dayContent(info)
                } else {
                    DefaultDayContent(info, colors, strikeThroughDisabledDays, locale)
                }
            }
        }
    }
}

@Composable
private fun DefaultDayContent(
    info: DayInfo,
    colors: ComposeDatePickerColors,
    strikeThroughDisabledDays: Boolean,
    locale: Locale,
) {
    val contentColor = LocalContentColor.current
    val decoration = info.decoration
    val label = decoration?.label
    val dots = decoration?.dots.orEmpty()
    val compact = label != null || dots.isNotEmpty()

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(
            text = String.format(locale, "%d", info.date.dayOfMonth),
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = if (compact) 15.sp else 20.sp),
            fontWeight = if (info.isSelected || (info.isToday && info.isCurrentMonth)) FontWeight.SemiBold else null,
            textDecoration = if (strikeThroughDisabledDays && !info.isEnabled && info.isCurrentMonth) {
                TextDecoration.LineThrough
            } else {
                null
            },
        )
        if (label != null) {
            Text(
                text = label,
                color = if (info.isSelected) contentColor else decoration.labelColor.takeOrElse { contentColor.copy(alpha = 0.72f) },
                fontSize = 9.sp,
                lineHeight = 10.sp,
                maxLines = 1,
            )
        }
        if (dots.isNotEmpty()) {
            Row(Modifier.padding(top = 2.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                dots.take(3).forEach { dot ->
                    Box(
                        Modifier
                            .size(4.dp)
                            .background(
                                if (info.isSelected) contentColor else dot.takeOrElse { colors.decorationColor },
                                CircleShape,
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun YearGrid(
    state: ComposeDatePickerState,
    currentYear: Int,
    colors: ComposeDatePickerColors,
    locale: Locale,
    onYearSelected: (Int) -> Unit,
) {
    val years = state.minDate.year..state.maxDate.year
    val selectedYear = state.displayedMonth.year
    // Start with the selected year on the third row so there is context above it.
    val gridState = rememberLazyGridState(
        initialFirstVisibleItemIndex = ((selectedYear - years.first) / 3 * 3 - 6).coerceAtLeast(0),
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = gridState,
        modifier = Modifier.fillMaxSize(),
    ) {
        items(years.toList(), key = { it }) { year ->
            val selected = year == selectedYear
            val container by animateColorAsState(
                if (selected) colors.selectedYearContainerColor else Color.Transparent,
                label = "YearContainer",
            )
            Box(Modifier.height(52.dp), contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .width(72.dp)
                        .height(36.dp)
                        .clip(RoundedCornerShape(50))
                        .background(container)
                        .semantics { this.selected = selected }
                        .clickable(role = Role.Button) { onYearSelected(year) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = String.format(locale, "%d", year),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (selected || year == currentYear) FontWeight.SemiBold else null,
                        color = when {
                            selected -> colors.selectedYearContentColor
                            year == currentYear -> colors.currentYearContentColor
                            else -> colors.yearContentColor
                        },
                    )
                }
            }
        }
    }
}

private val DayCellSize = 40.dp
private val DayRowHeight = 44.dp
private val WeekdayRowHeight = 32.dp
