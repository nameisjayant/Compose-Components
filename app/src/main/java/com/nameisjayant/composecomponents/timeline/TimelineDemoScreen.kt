package com.nameisjayant.composecomponents.timeline

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.components.timeline.ComposeTimeline
import com.nameisjayant.components.timeline.ComposeTimelineColors
import com.nameisjayant.components.timeline.ComposeTimelineDefaults
import com.nameisjayant.components.timeline.IconIndicator
import com.nameisjayant.components.timeline.NumberIndicator
import com.nameisjayant.components.timeline.StatusIndicator
import com.nameisjayant.components.timeline.TimelineAnimation
import com.nameisjayant.components.timeline.TimelineContentSide
import com.nameisjayant.components.timeline.TimelineIndicatorAlignment
import com.nameisjayant.components.timeline.TimelineLineStyle
import com.nameisjayant.components.timeline.TimelineOrientation
import com.nameisjayant.components.timeline.TimelineStep
import com.nameisjayant.components.timeline.TimelineStepState
import com.nameisjayant.composecomponents.ui.components.AccentSwatch
import com.nameisjayant.composecomponents.ui.components.AppIcons
import com.nameisjayant.composecomponents.ui.components.DemoCard
import com.nameisjayant.composecomponents.ui.components.DemoTopBar
import com.nameisjayant.composecomponents.ui.components.SectionLabel
import com.nameisjayant.composecomponents.ui.components.SwitchRow
import com.nameisjayant.composecomponents.ui.theme.ComposeComponentsTheme

private enum class Side(val label: String, val side: TimelineContentSide) {
    End("End", TimelineContentSide.End),
    Start("Start", TimelineContentSide.Start),
    Alternate("Alternate", TimelineContentSide.Alternate),
}

private enum class IndicatorKind(val label: String) {
    Status("Status"),
    Number("Number"),
    Icon("Icon"),
}

private enum class PendingLine(val label: String, val style: TimelineLineStyle) {
    Solid("Solid", TimelineLineStyle.Solid),
    Dashed("Dashed", TimelineLineStyle.Dashed),
    Dotted("Dotted", TimelineLineStyle.Dotted),
}

/** Accent palettes for the "Theme" section; each has a light and a dark variant. */
private enum class TimelineAccent(val label: String, val light: Color?, val dark: Color?) {
    App("App theme", null, null),
    Emerald("Emerald", Color(0xFF0F8A66), Color(0xFF3DD6A0)),
    Coral("Coral", Color(0xFFD9482F), Color(0xFFFF8A73)),
    Amber("Amber", Color(0xFFB86E00), Color(0xFFFFB84D)),
    Rose("Rose", Color(0xFFC92E68), Color(0xFFFF7EAA)),
}

private class OrderStep(val title: String, val short: String, val detail: String, val time: String, val icon: ImageVector)

private val OrderSteps = listOf(
    OrderStep("Order placed", "Placed", "We've got your order.", "Mon 9:41", AppIcons.Cart),
    OrderStep("Packed", "Packed", "Your items are boxed up.", "Mon 14:20", AppIcons.Package),
    OrderStep("Shipped", "Shipped", "On its way to your city.", "Tue 8:05", AppIcons.Send),
    OrderStep("Out for delivery", "Out", "With the courier today.", "Wed 10:30", AppIcons.Place),
    OrderStep("Delivered", "Delivered", "Left at your front door.", "Wed 16:12", AppIcons.Home),
)

private class Activity(val id: Int, val title: String, val time: String, val icon: ImageVector)

private val ActivityPool = listOf(
    "Signed in on a new phone" to AppIcons.Lock,
    "Order #4821 delivered" to AppIcons.Home,
    "Added a delivery address" to AppIcons.Place,
    "Bought 2 items" to AppIcons.Cart,
    "Rated your last order" to AppIcons.Star,
    "Return label sent" to AppIcons.Send,
)

private fun activity(id: Int): Activity {
    val (title, icon) = ActivityPool[id % ActivityPool.size]
    return Activity(id, title, if (id < 3) "${(3 - id) * 2} days ago" else "Just now", icon)
}

@Composable
fun TimelineDemoScreen(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    onBack: () -> Unit,
) {
    // Index of the step in progress; OrderSteps.size = all done.
    var current by rememberSaveable { mutableIntStateOf(2) }
    var problem by rememberSaveable { mutableStateOf(false) }

    var horizontal by rememberSaveable { mutableStateOf(false) }
    var side by rememberSaveable { mutableStateOf(Side.End) }
    var indicatorKind by rememberSaveable { mutableStateOf(IndicatorKind.Status) }
    var pendingLine by rememberSaveable { mutableStateOf(PendingLine.Dashed) }
    var accent by rememberSaveable { mutableStateOf(TimelineAccent.App) }

    var showTimes by rememberSaveable { mutableStateOf(true) }
    var centered by rememberSaveable { mutableStateOf(false) }
    var lineGap by rememberSaveable { mutableStateOf(false) }
    var animate by rememberSaveable { mutableStateOf(true) }

    var activityCount by rememberSaveable { mutableIntStateOf(3) }
    // Newest first; ids keep each step's animations when a new one is added on top.
    val activities = (activityCount - 1 downTo 0).map(::activity)

    val accentColor = if (isDark) accent.dark else accent.light
    val colors: ComposeTimelineColors =
        if (accentColor == null) ComposeTimelineDefaults.colors() else ComposeTimelineDefaults.accentColors(accentColor)
    val animation = if (animate) TimelineAnimation.Default else TimelineAnimation.None
    val stepState = { index: Int, _: OrderStep ->
        if (problem && index == current) TimelineStepState.Error else TimelineStepState.forProgress(index, current)
    }

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize()) {
            DemoTopBar(title = "Timeline", isDark = isDark, onToggleTheme = onToggleTheme, onBack = onBack)

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                DemoCard(Modifier.padding(top = 4.dp)) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                    ) {
                        Text("Order #4821", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = orderSummary(current, problem),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (problem) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(24.dp))
                        ComposeTimeline(
                            items = OrderSteps,
                            stepState = stepState,
                            orientation = if (horizontal) TimelineOrientation.Horizontal else TimelineOrientation.Vertical,
                            contentSide = side.side,
                            indicatorAlignment = if (centered) TimelineIndicatorAlignment.Center else TimelineIndicatorAlignment.Start,
                            indicatorSize = if (indicatorKind == IndicatorKind.Status) 24.dp else 30.dp,
                            itemSpacing = if (horizontal) 4.dp else 28.dp,
                            contentSpacing = if (horizontal) 10.dp else 16.dp,
                            lineGap = if (lineGap) 4.dp else 0.dp,
                            pendingLineStyle = pendingLine.style,
                            colors = colors,
                            animation = animation,
                            indicator = { step ->
                                when (indicatorKind) {
                                    IndicatorKind.Status -> StatusIndicator(step.state)
                                    IndicatorKind.Number -> NumberIndicator("${step.index + 1}", step.state)
                                    IndicatorKind.Icon -> IconIndicator(step.item.icon, step.state)
                                }
                            },
                            oppositeContent = if (showTimes) {
                                { step -> StepTime(step, horizontal) }
                            } else {
                                null
                            },
                        ) { step ->
                            OrderStepContent(step, horizontal)
                        }
                        Spacer(Modifier.height(20.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TextButton(
                                onClick = { current--; problem = false },
                                enabled = current > 0,
                            ) { Text("Back") }
                            TextButton(
                                onClick = { current++; problem = false },
                                enabled = current < OrderSteps.size,
                            ) { Text("Next step") }
                            TextButton(
                                onClick = { problem = !problem },
                                enabled = current < OrderSteps.size,
                            ) { Text(if (problem) "Resolve" else "Report problem") }
                        }
                    }
                }

                SectionLabel("Orientation")
                Segments(listOf(false, true), horizontal, { horizontal = it; centered = it }) {
                    if (it) "Horizontal" else "Vertical"
                }

                SectionLabel("Content side")
                Segments(Side.entries, side, { side = it }) { it.label }

                SectionLabel("Indicator")
                Segments(IndicatorKind.entries, indicatorKind, { indicatorKind = it }) { it.label }

                SectionLabel("Line after the current step")
                Segments(PendingLine.entries, pendingLine, { pendingLine = it }) { it.label }

                SectionLabel("Options")
                DemoCard {
                    SwitchRow("Show times", showTimes, { showTimes = it }, hint = "oppositeContent on the other side of the line")
                    Divider()
                    SwitchRow("Center indicators", centered, { centered = it }, hint = "indicatorAlignment = Center")
                    Divider()
                    SwitchRow("Gap around indicators", lineGap, { lineGap = it }, hint = "lineGap = 4.dp")
                    Divider()
                    SwitchRow("Animations", animate, { animate = it }, hint = "Line fill, state blend, pulse and reveal")
                }

                SectionLabel("Theme")
                DemoCard {
                    Row(
                        Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        TimelineAccent.entries.forEach { option ->
                            AccentSwatch(
                                label = option.label,
                                color = (if (isDark) option.dark else option.light) ?: MaterialTheme.colorScheme.primary,
                                selected = accent == option,
                                onClick = { accent = option },
                            )
                        }
                    }
                }

                SectionLabel("Activity feed")
                DemoCard {
                    Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)) {
                        ComposeTimeline(
                            items = activities,
                            key = { it.id },
                            indicatorAlignment = TimelineIndicatorAlignment.Center,
                            indicatorSize = 32.dp,
                            itemSpacing = 16.dp,
                            lineGap = 4.dp,
                            lineStyle = TimelineLineStyle.Dotted,
                            colors = colors,
                            animation = animation,
                            indicator = { step -> IconIndicator(step.item.icon, step.state) },
                        ) { step ->
                            Column {
                                Text(step.item.title, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    text = step.item.time,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                    TextButton(
                        onClick = { activityCount++ },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                    ) { Text("Add event") }
                }

                Spacer(Modifier.height(32.dp))
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}

private fun orderSummary(current: Int, problem: Boolean): String = when {
    problem -> "Delivery hit a problem at \"${OrderSteps[current].title}\"."
    current >= OrderSteps.size -> "Delivered. Enjoy!"
    else -> "${OrderSteps[current].title} · step ${current + 1} of ${OrderSteps.size}"
}

@Composable
private fun OrderStepContent(step: TimelineStep<OrderStep>, horizontal: Boolean) {
    val titleColor = when (step.state) {
        TimelineStepState.Error -> MaterialTheme.colorScheme.error
        TimelineStepState.Upcoming -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurface
    }
    if (horizontal) {
        Text(
            text = step.item.short,
            style = MaterialTheme.typography.labelMedium,
            color = titleColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
        )
        return
    }
    val textAlign = if (step.contentSide == TimelineContentSide.Start) TextAlign.End else TextAlign.Start
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = step.item.title,
            style = MaterialTheme.typography.titleMedium,
            color = titleColor,
            textAlign = textAlign,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = if (step.state == TimelineStepState.Error) "Delivery attempt failed. We'll try again." else step.item.detail,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = textAlign,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** Opposite content: when the step happened, aligned towards the line. */
@Composable
private fun StepTime(step: TimelineStep<OrderStep>, horizontal: Boolean) {
    // Opposite content sits on the other side from the content.
    val textAlign = when {
        horizontal -> TextAlign.Center
        step.contentSide == TimelineContentSide.End -> TextAlign.End
        else -> TextAlign.Start
    }
    Text(
        text = if (step.state.isReached) step.item.time else "—",
        style = if (horizontal) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = textAlign,
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (horizontal) 0.dp else 2.dp),
    )
}

@Composable
private fun <T> Segments(options: List<T>, selected: T, onSelect: (T) -> Unit, label: (T) -> String) {
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = selected == option,
                onClick = { onSelect(option) },
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
                icon = {},
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    inactiveContainerColor = MaterialTheme.colorScheme.surface,
                    activeBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    inactiveBorderColor = MaterialTheme.colorScheme.outlineVariant,
                ),
            ) { Text(label(option), maxLines = 1) }
        }
    }
}

@Composable
private fun Divider() {
    HorizontalDivider(Modifier.padding(horizontal = 20.dp), color = MaterialTheme.colorScheme.outlineVariant)
}

@Preview
@Composable
private fun TimelineDemoPreview() {
    ComposeComponentsTheme(darkTheme = false) {
        TimelineDemoScreen(isDark = false, onToggleTheme = {}, onBack = {})
    }
}

@Preview
@Composable
private fun TimelineDemoDarkPreview() {
    ComposeComponentsTheme(darkTheme = true) {
        TimelineDemoScreen(isDark = true, onToggleTheme = {}, onBack = {})
    }
}
