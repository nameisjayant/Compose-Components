package com.nameisjayant.composecomponents.docs

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.nameisjayant.components.colorpicker.ColorPickerStyle
import com.nameisjayant.components.colorpicker.ComposeColorPicker
import com.nameisjayant.components.colorpicker.rememberComposeColorPickerState
import com.nameisjayant.components.datepicker.CalendarMonth
import com.nameisjayant.components.datepicker.ComposeDatePicker
import com.nameisjayant.components.datepicker.ComposeDatePickerDefaults
import com.nameisjayant.components.datepicker.DateSelectionMode
import com.nameisjayant.components.datepicker.DayDecoration
import com.nameisjayant.components.datepicker.SelectableDates
import com.nameisjayant.components.datepicker.rememberComposeDatePickerState
import com.nameisjayant.components.otpinput.ComposeOtpInput
import com.nameisjayant.components.otpinput.ComposeOtpInputDefaults
import com.nameisjayant.components.otpinput.OtpCellStyle
import com.nameisjayant.components.otpinput.OtpInputType
import com.nameisjayant.components.ratingbar.ComposeRatingBar
import com.nameisjayant.components.ratingbar.ComposeRatingBarDefaults
import com.nameisjayant.components.timeline.ComposeTimeline
import com.nameisjayant.components.timeline.IconIndicator
import com.nameisjayant.components.timeline.NumberIndicator
import com.nameisjayant.components.timeline.StatusIndicator
import com.nameisjayant.components.timeline.TimelineContentSide
import com.nameisjayant.components.timeline.TimelineLineStyle
import com.nameisjayant.components.timeline.TimelineOrientation
import com.nameisjayant.components.timeline.TimelineStepState
import com.nameisjayant.components.wheelpicker.ComposeWheelPicker
import com.nameisjayant.components.wheelpicker.ComposeWheelPickerDefaults
import com.nameisjayant.components.wheelpicker.WheelPickerStyle
import com.nameisjayant.composecomponents.ui.components.AppIcons
import com.nameisjayant.composecomponents.ui.theme.ComposeComponentsTheme
import java.io.File
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Records the frames behind the README GIFs. Each test renders one scene off-device and drives it with
 * real taps, drags and typing on a paused clock, capturing the scene after every frame.
 *
 * Skipped unless `-PgifDir=<dir>` is passed; run `scripts/record-gifs.sh` instead.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36], qualifiers = "w411dp-h891dp-xxhdpi")
class RecordComponentGifs {

    @get:Rule
    val rule = createComposeRule()

    private val outputDir: File? = System.getProperty("gifDir")?.let(::File)

    // region Date picker

    @Test
    fun datePickerRange() = record("date-picker-range", {
        Scene("Range", "Tap a start and an end, even across months", framed = false) {
            val state = rememberComposeDatePickerState(
                selectionMode = DateSelectionMode.Range,
                initialDisplayedMonth = CalendarMonth(2026, 10),
            )
            ComposeDatePicker(
                state = state,
                modifier = Modifier.testTag(PICKER),
                shape = RoundedCornerShape(28.dp),
                highlightWeekends = true,
            )
        }
    }) {
        hold(700)
        tap(day("6")); hold(500)
        tap(day("14")); hold(1100)
        tap(day("27")); hold(500)
        drag(rule.onNodeWithTag(PICKER), Offset(0.85f, 0.7f), Offset(0.15f, 0.7f), 360); hold(800)
        tap(day("5")); hold(1800)
    }

    @Test
    fun datePickerMultiple() = record("date-picker-multiple", {
        Scene("Multiple", "Weekdays only, event dots and a holiday", framed = false) {
            val state = rememberComposeDatePickerState(
                selectionMode = DateSelectionMode.Multiple,
                initialDisplayedMonth = CalendarMonth(2026, 10),
                selectableDates = SelectableDates.WeekdaysOnly,
            )
            val accent = Color(0xFF0F8A66)
            ComposeDatePicker(
                state = state,
                modifier = Modifier.testTag(PICKER),
                shape = RoundedCornerShape(28.dp),
                colors = ComposeDatePickerDefaults.accentColors(accent),
                dayShape = ComposeDatePickerDefaults.RoundedDayShape,
                strikeThroughDisabledDays = true,
                decoration = { date ->
                    when (date.dayOfMonth) {
                        2 -> DayDecoration(containerColor = Color(0xFFFBE4DC), contentColor = Color(0xFFB4472B))
                        7, 21 -> DayDecoration.dot()
                        15 -> DayDecoration(dots = listOf(accent, Color(0xFFE5484D)))
                        28 -> DayDecoration(dots = listOf(accent, Color(0xFFE5484D), Color(0xFF4338CA)))
                        else -> null
                    }
                },
            )
        }
    }) {
        hold(700)
        for (d in listOf("5", "9", "13", "20", "23")) {
            tap(day(d)); hold(380)
        }
        hold(600)
        tap(day("9")); hold(1700)
    }

    @Test
    fun datePickerYear() = record("date-picker-single", {
        Scene("Single + labels", "Price labels per day, tap the month to jump years", framed = false) {
            val state = rememberComposeDatePickerState(initialDisplayedMonth = CalendarMonth(2026, 10))
            ComposeDatePicker(
                state = state,
                modifier = Modifier.testTag(PICKER),
                shape = RoundedCornerShape(28.dp),
                dayShape = ComposeDatePickerDefaults.RoundedDayShape,
                decoration = { date ->
                    val price = 60 + (date.dayOfMonth * 37 + date.month * 11) % 90
                    DayDecoration(
                        label = "$$price",
                        labelColor = if (price < 80) Color(0xFF0F8A66) else Color.Unspecified,
                    )
                },
            )
        }
    }) {
        hold(700)
        tap(day("18")); hold(900)
        tap(rule.onNodeWithText("October 2026")); hold(900)
        tap(rule.onNodeWithText("2027")); hold(900)
        tap(day("12")); hold(1700)
    }

    // endregion

    // region Rating bar

    @Test
    fun ratingBar() {
        val ratings = listOf(mutableFloatStateOf(2f), mutableFloatStateOf(1.5f), mutableFloatStateOf(1.2f), mutableFloatStateOf(3f))
        record("rating-bar", {
            Scene("Rating bar", "Tap or drag; whole, half or free steps") {
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    RatingRow("Stars", ratings[0].floatValue) {
                        ComposeRatingBar(
                            rating = ratings[0].floatValue,
                            onRatingChange = { ratings[0].floatValue = it },
                            modifier = Modifier.testTag("bar0"),
                        )
                    }
                    RatingRow("Halves", ratings[1].floatValue) {
                        ComposeRatingBar(
                            rating = ratings[1].floatValue,
                            onRatingChange = { ratings[1].floatValue = it },
                            modifier = Modifier.testTag("bar1"),
                            stepSize = 0.5f,
                            colors = ComposeRatingBarDefaults.accentColors(ComposeRatingBarDefaults.Gold),
                        )
                    }
                    RatingRow("Hearts", ratings[2].floatValue) {
                        ComposeRatingBar(
                            rating = ratings[2].floatValue,
                            onRatingChange = { ratings[2].floatValue = it },
                            modifier = Modifier.testTag("bar2"),
                            stepSize = 0f,
                            shape = ComposeRatingBarDefaults.HeartShape,
                            colors = ComposeRatingBarDefaults.accentColors(Color(0xFFE5484D)),
                        )
                    }
                    RatingRow("Outlined", ratings[3].floatValue) {
                        ComposeRatingBar(
                            rating = ratings[3].floatValue,
                            onRatingChange = { ratings[3].floatValue = it },
                            modifier = Modifier.testTag("bar3"),
                            shape = ComposeRatingBarDefaults.SharpStarShape,
                            borderWidth = ComposeRatingBarDefaults.OutlineWidth,
                            colors = ComposeRatingBarDefaults.outlinedColors(),
                        )
                    }
                    RatingRow("Read-only", 3.7f) {
                        ComposeRatingBar(
                            rating = 3.7f,
                            onRatingChange = null,
                            colors = ComposeRatingBarDefaults.accentColors(ComposeRatingBarDefaults.Gold),
                        )
                    }
                }
            }
        }) {
            hold(600)
            tap(rule.onNodeWithTag("bar0"), starX(5)); hold(1000)
            tap(rule.onNodeWithTag("bar0"), starX(4)); hold(800)
            drag(rule.onNodeWithTag("bar1"), Offset(0.02f, 0.5f), Offset(starX(4, half = true), 0.5f), 900); hold(900)
            drag(rule.onNodeWithTag("bar2"), Offset(0.05f, 0.5f), Offset(0.95f, 0.5f), 800)
            drag(rule.onNodeWithTag("bar2"), Offset(0.95f, 0.5f), Offset(0.62f, 0.5f), 500); hold(900)
            tap(rule.onNodeWithTag("bar3"), starX(5)); hold(900)
            tap(rule.onNodeWithTag("bar3"), starX(2)); hold(1500)
        }
    }

    @Composable
    private fun RatingRow(label: String, value: Float, bar: @Composable () -> Unit) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, Modifier.width(76.dp), style = MaterialTheme.typography.labelLarge)
            bar()
            Spacer(Modifier.weight(1f))
            Text(
                String.format(Locale.US, "%.1f", value),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    /** Horizontal fraction of the middle (or left half) of star [n] in a default 5-item bar. */
    private fun starX(n: Int, half: Boolean = false): Float {
        val item = 32f
        val step = 38f
        val total = 5 * item + 4 * 6f
        return ((n - 1) * step + if (half) item / 4 else item / 2) / total
    }

    // endregion

    // region OTP input

    @Test
    fun otpInput() {
        val codes = List(4) { mutableStateOf("") }
        var pinError by mutableStateOf(false)
        record("otp-input", {
            Scene("OTP input", "Type, paste, mask, and shake on a wrong code") {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    SectionLabel("Outlined")
                    ComposeOtpInput(
                        value = codes[0].value,
                        onValueChange = { codes[0].value = it },
                        cellSize = DpSize(Dp.Unspecified, 52.dp),
                    )
                    SectionLabel("Filled · grouped · pasted")
                    ComposeOtpInput(
                        value = codes[1].value,
                        onValueChange = { codes[1].value = it },
                        cellStyle = OtpCellStyle.Filled,
                        cellSize = DpSize(Dp.Unspecified, 52.dp),
                        groupSize = 3,
                        colors = ComposeOtpInputDefaults.accentColors(Color(0xFF0F8A66), OtpCellStyle.Filled),
                    )
                    SectionLabel(if (pinError) "Underlined · masked · wrong PIN" else "Underlined · masked")
                    ComposeOtpInput(
                        value = codes[2].value,
                        onValueChange = { codes[2].value = it; pinError = false },
                        onComplete = { pinError = true },
                        length = 4,
                        cellStyle = OtpCellStyle.Underlined,
                        cellSize = DpSize(52.dp, 52.dp),
                        cellSpacing = 14.dp,
                        mask = true,
                        isError = pinError,
                    )
                    SectionLabel("Round · letters")
                    ComposeOtpInput(
                        value = codes[3].value,
                        onValueChange = { codes[3].value = it },
                        length = 5,
                        inputType = OtpInputType.Letters,
                        uppercase = true,
                        cellShape = ComposeOtpInputDefaults.CircleCellShape,
                        cellSize = DpSize(46.dp, 46.dp),
                        colors = ComposeOtpInputDefaults.accentColors(Color(0xFFB4472B)),
                    )
                }
            }
        }) {
            val fields = { i: Int -> rule.onAllNodes(hasSetTextAction())[i] }
            hold(500)
            type(fields(0), "482913", 260); hold(700)
            fields(1).performTextInput("529-174"); hold(1100)
            type(fields(2), "1234", 320); hold(1400)
            type(fields(3), "hello", 260); hold(1500)
        }
    }

    // endregion

    // region Wheel picker

    @Test
    fun wheelPicker() = record("wheel-picker", {
        Scene("Wheel picker", "Curved and looping, or flat with disabled items") {
            SectionLabel("Time · three wheels, one band")
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                ComposeWheelPickerDefaults.SelectionBand(Modifier.fillMaxWidth().height(48.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ComposeWheelPicker(
                        items = (1..12).toList(),
                        modifier = Modifier.testTag("hours").width(72.dp),
                        state = remember { com.nameisjayant.components.wheelpicker.ComposeWheelPickerState(9) },
                        cyclic = true,
                        selectionIndicator = null,
                    )
                    Text(":", style = MaterialTheme.typography.titleLarge)
                    ComposeWheelPicker(
                        items = (0..59).map { "%02d".format(it) },
                        modifier = Modifier.testTag("minutes").width(72.dp),
                        state = remember { com.nameisjayant.components.wheelpicker.ComposeWheelPickerState(4) },
                        cyclic = true,
                        selectionIndicator = null,
                    )
                    ComposeWheelPicker(
                        items = listOf("AM", "PM"),
                        modifier = Modifier.testTag("ampm").width(72.dp),
                        selectionIndicator = null,
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            SectionLabel("Date · flat, years before 2024 disabled")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ComposeWheelPicker(
                    items = (1..31).toList(),
                    modifier = Modifier.width(64.dp),
                    state = remember { com.nameisjayant.components.wheelpicker.ComposeWheelPickerState(21) },
                    style = WheelPickerStyle.Flat,
                    selectionIndicator = { ComposeWheelPickerDefaults.SelectionLines() },
                )
                ComposeWheelPicker(
                    items = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"),
                    modifier = Modifier.testTag("month").width(88.dp),
                    state = remember { com.nameisjayant.components.wheelpicker.ComposeWheelPickerState(9) },
                    style = WheelPickerStyle.Flat,
                    selectionIndicator = { ComposeWheelPickerDefaults.SelectionLines() },
                )
                ComposeWheelPicker(
                    items = (2020..2032).toList(),
                    modifier = Modifier.testTag("year").width(88.dp),
                    state = remember { com.nameisjayant.components.wheelpicker.ComposeWheelPickerState(6) },
                    style = WheelPickerStyle.Flat,
                    selectionIndicator = { ComposeWheelPickerDefaults.SelectionLines() },
                    isItemEnabled = { it >= 4 },
                )
            }
        }
    }) {
        hold(600)
        drag(rule.onNodeWithTag("hours"), Offset(0.5f, 0.7f), Offset(0.5f, 0.3f), 200); hold(1300)
        drag(rule.onNodeWithTag("minutes"), Offset(0.5f, 0.05f), Offset(0.5f, 0.95f), 160); hold(160)
        drag(rule.onNodeWithTag("minutes"), Offset(0.5f, 0.05f), Offset(0.5f, 0.95f), 160); hold(1500)
        tap(rule.onNodeWithTag("ampm"), 0.5f, 0.7f); hold(1000)
        drag(rule.onNodeWithTag("month"), Offset(0.5f, 0.75f), Offset(0.5f, 0.35f), 500); hold(1000)
        drag(rule.onNodeWithTag("year"), Offset(0.5f, 0.2f), Offset(0.5f, 0.9f), 600); hold(1800)
    }

    // endregion

    // region Timeline

    @Test
    fun timelineVertical() {
        var current by mutableIntStateOf(1)
        record("timeline-vertical", {
            Scene("Vertical", "Order tracking with times opposite the line") {
                ComposeTimeline(
                    items = OrderSteps,
                    stepState = { index, _ -> TimelineStepState.forProgress(index, current) },
                    pendingLineStyle = TimelineLineStyle.Dashed,
                    itemSpacing = 18.dp,
                    oppositeContentFraction = 0.3f,
                    oppositeContent = { step ->
                        Text(
                            step.item.third,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                ) { step ->
                    Column {
                        Text(step.item.first, style = MaterialTheme.typography.titleSmall)
                        Text(
                            step.item.second,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }) {
            hold(1600)
            for (step in 2..5) {
                current = step
                hold(1100)
            }
            hold(800)
        }
    }

    @Test
    fun timelineHorizontal() {
        var current by mutableIntStateOf(0)
        var failed by mutableStateOf(false)
        record("timeline-horizontal", {
            Scene("Horizontal", "Numbered stepper, error state, icon tracker") {
                val steps = listOf("Cart", "Address", "Payment", "Done")
                ComposeTimeline(
                    items = steps,
                    orientation = TimelineOrientation.Horizontal,
                    stepState = { index, _ ->
                        if (failed && index == current) TimelineStepState.Error
                        else TimelineStepState.forProgress(index, current)
                    },
                    indicatorSize = 28.dp,
                    itemSpacing = 6.dp,
                    contentSpacing = 8.dp,
                    indicator = { step ->
                        if (step.state == TimelineStepState.Upcoming || step.state == TimelineStepState.Active) {
                            NumberIndicator("${step.index + 1}", step.state)
                        } else {
                            StatusIndicator(step.state)
                        }
                    },
                ) { step ->
                    Text(step.item, style = MaterialTheme.typography.labelMedium)
                }
                Spacer(Modifier.height(28.dp))
                ComposeTimeline(
                    items = TrackerSteps,
                    orientation = TimelineOrientation.Horizontal,
                    contentSide = TimelineContentSide.Alternate,
                    stepState = { index, _ -> TimelineStepState.forProgress(index, current + 1) },
                    indicatorSize = 32.dp,
                    itemSpacing = 4.dp,
                    contentSpacing = 8.dp,
                    pendingLineStyle = TimelineLineStyle.Dotted,
                    indicator = { step -> IconIndicator(step.item.second, step.state) },
                ) { step ->
                    Text(step.item.first, style = MaterialTheme.typography.labelSmall)
                }
            }
        }) {
            hold(1400)
            current = 1; hold(1100)
            current = 2; hold(900)
            failed = true; hold(1300)
            failed = false; hold(700)
            current = 3; hold(1100)
            current = 4; hold(1600)
        }
    }

    // endregion

    // region Color picker

    @Test
    fun colorPickerWheel() = recordColorPicker("color-picker-wheel", ColorPickerStyle.Wheel) {
        hold(600)
        val panel = rule.onNodeWithContentDescription("Color")
        drag(panel, Offset(0.5f, 0.5f), Offset(0.5f, 0.5f), 1700) { t ->
            val angle = -PI / 2 + t * 2 * PI
            val radius = 0.12f + 0.3f * t
            Offset(0.5f + radius * cos(angle).toFloat(), 0.5f + radius * sin(angle).toFloat())
        }
        hold(500)
        drag(rule.onNodeWithContentDescription("Brightness"), Offset(0.98f, 0.5f), Offset(0.5f, 0.5f), 600); hold(300)
        drag(rule.onNodeWithContentDescription("Opacity"), Offset(0.98f, 0.5f), Offset(0.55f, 0.5f), 600); hold(700)
        tap(rule.onNodeWithContentDescription("#E91E63")); hold(900)
        tap(rule.onNodeWithContentDescription("Original color")); hold(1500)
    }

    @Test
    fun colorPickerRing() = recordColorPicker("color-picker-ring", ColorPickerStyle.Ring) {
        hold(600)
        val panel = rule.onNodeWithContentDescription("Color")
        drag(panel, Offset(0.95f, 0.5f), Offset(0.95f, 0.5f), 1300) { t ->
            val angle = t * 1.6 * PI
            Offset(0.5f + 0.45f * cos(angle).toFloat(), 0.5f + 0.45f * sin(angle).toFloat())
        }
        hold(400)
        drag(panel, Offset(0.68f, 0.34f), Offset(0.42f, 0.6f), 900); hold(500)
        drag(rule.onNodeWithContentDescription("Opacity"), Offset(0.98f, 0.5f), Offset(0.6f, 0.5f), 500); hold(600)
        tap(rule.onNodeWithContentDescription("#009688")); hold(1500)
    }

    @Test
    fun colorPickerSquare() = recordColorPicker("color-picker-square", ColorPickerStyle.Square) {
        hold(600)
        val panel = rule.onNodeWithContentDescription("Color")
        drag(panel, Offset(0.9f, 0.15f), Offset(0.9f, 0.15f), 1300) { t ->
            Offset(0.9f - 0.6f * t, 0.15f + 0.5f * sin(t * PI).toFloat() * 0.8f + 0.1f * t)
        }
        hold(400)
        drag(rule.onNodeWithContentDescription("Hue"), Offset(0.64f, 0.5f), Offset(0.08f, 0.5f), 1000); hold(600)
        tap(rule.onNodeWithContentDescription("#FF9800")); hold(1500)
    }

    private fun recordColorPicker(name: String, style: ColorPickerStyle, script: Recorder.() -> Unit) =
        record(name, {
            Scene(style.name, "Alpha, preview, hex field and swatches") {
                val initial = Color(0xFF3D5AFE)
                val state = rememberComposeColorPickerState(initial)
                ComposeColorPicker(state = state, style = style, originalColor = initial)
            }
        }, script)

    // endregion

    // region Recording

    @Composable
    private fun Scene(
        title: String,
        caption: String,
        framed: Boolean = true,
        content: @Composable ColumnScope.() -> Unit,
    ) {
        val colors = MaterialTheme.colorScheme
        Column(
            Modifier
                .testTag(SCENE)
                .width(SceneWidth)
                .background(colors.background)
                .padding(16.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(caption, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            if (framed) {
                val shape = RoundedCornerShape(24.dp)
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(colors.surface, shape)
                        .border(1.dp, colors.outlineVariant, shape)
                        .padding(20.dp),
                    content = content,
                )
            } else {
                Column(content = content)
            }
        }
    }

    @Composable
    private fun SectionLabel(text: String) {
        Text(
            text.uppercase(),
            Modifier.padding(bottom = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    private fun day(text: String): SemanticsNodeInteraction {
        val box = rule.onNodeWithTag(PICKER).fetchSemanticsNode().boundsInRoot
        val all = rule.onAllNodesWithText(text, useUnmergedTree = true)
        val index = all.fetchSemanticsNodes().indexOfFirst {
            val b = it.boundsInRoot
            b.width > 0f && b.left >= box.left - 1f && b.right <= box.right + 1f
        }
        check(index >= 0) { "No visible day $text" }
        return all[index]
    }

    private fun record(name: String, content: @Composable () -> Unit, script: Recorder.() -> Unit) {
        assumeTrue("Pass -PgifDir to record GIF frames", outputDir != null)
        val dir = File(outputDir, name).apply {
            deleteRecursively()
            mkdirs()
        }
        rule.mainClock.autoAdvance = false
        rule.setContent {
            ComposeComponentsTheme(darkTheme = false) {
                Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) { content() }
            }
        }
        Recorder(dir).script()
    }

    private inner class Recorder(private val dir: File) {
        private var frame = 0

        fun hold(millis: Long) = repeat((millis / FRAME_MILLIS).toInt()) { snap() }

        fun snap() {
            val bitmap = rule.onNodeWithTag(SCENE).captureToImage().asAndroidBitmap()
            File(dir, "frame_%04d.png".format(frame++)).outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            rule.mainClock.advanceTimeBy(FRAME_MILLIS)
        }

        fun tap(node: SemanticsNodeInteraction, x: Float = 0.5f, y: Float = 0.5f) {
            node.performTouchInput { down(percentOffset(x, y)) }
            snap()
            snap()
            node.performTouchInput { advanceEventTime(2 * FRAME_MILLIS); up() }
        }

        /** Drags across [node] in fractions of its size, along a straight line or [path] (t in 0..1). */
        fun drag(
            node: SemanticsNodeInteraction,
            from: Offset,
            to: Offset,
            millis: Long,
            path: ((Float) -> Offset)? = null,
        ) {
            val start = path?.invoke(0f) ?: from
            node.performTouchInput { down(percentOffset(start.x, start.y)) }
            snap()
            val steps = (millis / FRAME_MILLIS).toInt().coerceAtLeast(1)
            for (i in 1..steps) {
                val t = i / steps.toFloat()
                val p = path?.invoke(t) ?: Offset(from.x + (to.x - from.x) * t, from.y + (to.y - from.y) * t)
                node.performTouchInput {
                    advanceEventTime(FRAME_MILLIS)
                    moveTo(percentOffset(p.x, p.y))
                }
                snap()
            }
            node.performTouchInput { up() }
        }

        fun type(node: SemanticsNodeInteraction, text: String, perCharMillis: Long) {
            text.forEach {
                node.performTextInput(it.toString())
                hold(perCharMillis)
            }
        }
    }

    private companion object {
        const val SCENE = "gif-scene"
        const val PICKER = "gif-picker"
        const val FRAME_MILLIS = 40L
        val SceneWidth = 360.dp

        val OrderSteps = listOf(
            Triple("Order placed", "We've got your order.", "Mon 9:41"),
            Triple("Packed", "Your items are boxed up.", "Mon 14:20"),
            Triple("Shipped", "On its way to your city.", "Tue 8:05"),
            Triple("Out for delivery", "With the courier today.", "Wed 10:30"),
            Triple("Delivered", "Left at your front door.", "Wed 16:12"),
        )
        val TrackerSteps = listOf(
            "Placed" to AppIcons.Cart,
            "Packed" to AppIcons.Package,
            "Shipped" to AppIcons.Send,
            "Nearby" to AppIcons.Place,
            "Home" to AppIcons.Home,
        )
    }
}
