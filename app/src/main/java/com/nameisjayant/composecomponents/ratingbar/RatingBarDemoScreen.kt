package com.nameisjayant.composecomponents.ratingbar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.components.ratingbar.ComposeRatingBar
import com.nameisjayant.components.ratingbar.ComposeRatingBarColors
import com.nameisjayant.components.ratingbar.ComposeRatingBarDefaults
import com.nameisjayant.components.ratingbar.RatingBarAnimation
import com.nameisjayant.composecomponents.ui.components.AccentSwatch
import com.nameisjayant.composecomponents.ui.components.AppIcons
import com.nameisjayant.composecomponents.ui.components.DemoCard
import com.nameisjayant.composecomponents.ui.components.DemoTopBar
import com.nameisjayant.composecomponents.ui.components.SectionLabel
import com.nameisjayant.composecomponents.ui.components.SwitchRow
import com.nameisjayant.composecomponents.ui.theme.ComposeComponentsTheme
import java.util.Locale
import kotlin.math.ceil

private enum class RatingStep(val label: String, val size: Float) {
    Whole("Whole", 1f),
    Half("Half", 0.5f),
    Free("Free", 0f),
}

private enum class RatingItemShape(val label: String) {
    Star("Star"),
    Sharp("Sharp"),
    Heart("Heart"),
    Circle("Circle"),
    Icon("Icon"),
}

/** Accent palettes for the "Theme" section; each has a light and a dark variant. */
private enum class RatingAccent(val label: String, val light: Color?, val dark: Color?) {
    App("App theme", null, null),
    Gold("Gold", Color(0xFFF2A900), Color(0xFFFFC233)),
    Coral("Coral", Color(0xFFD9482F), Color(0xFFFF8A73)),
    Rose("Rose", Color(0xFFC92E68), Color(0xFFFF7EAA)),
    Emerald("Emerald", Color(0xFF0F8A66), Color(0xFF3DD6A0)),
}

private val Verdicts = listOf("Terrible", "Bad", "Okay", "Good", "Excellent")

private data class Review(val title: String, val rating: Float, val count: String)

private val SampleReviews = listOf(
    Review("Seaside Cabin", 4.8f, "2,148"),
    Review("City Loft", 4.3f, "873"),
    Review("Forest Hut", 3.6f, "412"),
)

@Composable
fun RatingBarDemoScreen(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    onBack: () -> Unit,
) {
    var rating by rememberSaveable { mutableFloatStateOf(3f) }

    var step by rememberSaveable { mutableStateOf(RatingStep.Whole) }
    var itemShape by rememberSaveable { mutableStateOf(RatingItemShape.Star) }
    var accent by rememberSaveable { mutableStateOf(RatingAccent.Gold) }

    // Behaviour
    var tenItems by rememberSaveable { mutableStateOf(false) }
    var requireOne by rememberSaveable { mutableStateOf(false) }
    var allowClear by rememberSaveable { mutableStateOf(true) }
    var enabled by rememberSaveable { mutableStateOf(true) }
    var animate by rememberSaveable { mutableStateOf(true) }
    var haptics by rememberSaveable { mutableStateOf(true) }

    // Appearance
    var outlined by rememberSaveable { mutableStateOf(false) }
    var largeItems by rememberSaveable { mutableStateOf(true) }

    // Read-only
    var shuffle by rememberSaveable { mutableIntStateOf(0) }

    val maxRating = if (tenItems) 10 else 5
    val minRating = if (requireOne) 1f else 0f
    val accentColor = (if (isDark) accent.dark else accent.light) ?: MaterialTheme.colorScheme.primary
    val colors: ComposeRatingBarColors = when {
        outlined -> ComposeRatingBarDefaults.outlinedColors(accent = accentColor)
        else -> ComposeRatingBarDefaults.accentColors(accent = accentColor)
    }
    val shape: Shape = when (itemShape) {
        RatingItemShape.Sharp -> ComposeRatingBarDefaults.SharpStarShape
        RatingItemShape.Heart -> ComposeRatingBarDefaults.HeartShape
        RatingItemShape.Circle -> CircleShape
        else -> ComposeRatingBarDefaults.StarShape
    }
    val iconPainter = rememberVectorPainter(AppIcons.Drop)
    val animation = if (animate) RatingBarAnimation.Default else RatingBarAnimation.None

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize()) {
            DemoTopBar(title = "Rating Bar", isDark = isDark, onToggleTheme = onToggleTheme, onBack = onBack)

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
                            .padding(horizontal = 16.dp, vertical = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Verdict(rating, maxRating)
                        Spacer(Modifier.height(20.dp))
                        ComposeRatingBar(
                            rating = rating,
                            onRatingChange = { rating = it },
                            maxRating = maxRating,
                            stepSize = step.size,
                            minRating = minRating,
                            enabled = enabled,
                            allowClear = allowClear,
                            itemSize = when {
                                tenItems -> 26.dp
                                largeItems -> 46.dp
                                else -> 34.dp
                            },
                            itemSpacing = if (tenItems) 4.dp else 8.dp,
                            shape = shape,
                            painter = if (itemShape == RatingItemShape.Icon) iconPainter else null,
                            borderWidth = if (outlined) ComposeRatingBarDefaults.OutlineWidth else 0.dp,
                            colors = colors,
                            animation = animation,
                            hapticFeedbackEnabled = haptics,
                        )
                        Spacer(Modifier.height(20.dp))
                        Text(
                            text = "${formatValue(rating)} / $maxRating",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        TextButton(
                            onClick = { rating = minRating },
                            enabled = enabled && rating > minRating,
                        ) { Text("Clear") }
                    }
                }

                SectionLabel("Step")
                Segments(RatingStep.entries, step, { step = it; rating = snapForDemo(rating, it.size) }) { it.label }

                SectionLabel("Shape")
                Segments(RatingItemShape.entries, itemShape, { itemShape = it }) { it.label }

                SectionLabel("Behaviour")
                DemoCard {
                    SwitchRow("Ten items", tenItems, {
                        tenItems = it
                        rating = rating.coerceAtMost(if (it) 10f else 5f)
                    })
                    Divider()
                    SwitchRow("Require at least one", requireOne, {
                        requireOne = it
                        if (it) rating = rating.coerceAtLeast(1f)
                    }, hint = "minRating = 1")
                    Divider()
                    SwitchRow("Tap again to clear", allowClear, { allowClear = it }, hint = "Tapping the current rating resets it")
                    Divider()
                    SwitchRow("Enabled", enabled, { enabled = it })
                    Divider()
                    SwitchRow("Animations", animate, { animate = it }, hint = "Cascade, pop, halo and drag lift")
                    Divider()
                    SwitchRow("Haptic ticks", haptics, { haptics = it })
                }

                SectionLabel("Theme")
                DemoCard {
                    Row(
                        Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        RatingAccent.entries.forEach { option ->
                            AccentSwatch(
                                label = option.label,
                                color = (if (isDark) option.dark else option.light) ?: MaterialTheme.colorScheme.primary,
                                selected = accent == option,
                                onClick = { accent = option },
                            )
                        }
                    }
                    Divider()
                    SwitchRow("Outlined", outlined, { outlined = it }, hint = "Empty items show only a border")
                    Divider()
                    SwitchRow("Large items", largeItems, { largeItems = it }, enabled = !tenItems)
                }

                SectionLabel("Read-only")
                DemoCard {
                    SampleReviews.forEachIndexed { index, review ->
                        if (index > 0) Divider()
                        ReviewRow(review, shuffle, accentColor)
                    }
                    Divider()
                    TextButton(
                        onClick = { shuffle++ },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    ) { Text("Shuffle scores") }
                }

                Spacer(Modifier.height(32.dp))
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}

@Composable
private fun Verdict(rating: Float, maxRating: Int) {
    val verdict = if (rating <= 0f) {
        "Tap or drag to rate"
    } else {
        Verdicts[(ceil(rating / maxRating * Verdicts.size).toInt() - 1).coerceIn(0, Verdicts.lastIndex)]
    }
    AnimatedContent(
        targetState = verdict,
        transitionSpec = {
            (fadeIn() + slideInVertically { it / 3 }) togetherWith (fadeOut() + slideOutVertically { -it / 3 })
        },
        label = "Verdict",
    ) { text ->
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** A listing with a small read-only bar; "Shuffle scores" changes it to show the animation. */
@Composable
private fun ReviewRow(review: Review, shuffle: Int, accent: Color) {
    val score = if (shuffle == 0) review.rating else (10 + (review.title.hashCode() + shuffle * 7).mod(41)) / 10f
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(review.title, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                ComposeRatingBar(
                    rating = score,
                    onRatingChange = null,
                    itemSize = 16.dp,
                    itemSpacing = 2.dp,
                    colors = ComposeRatingBarDefaults.accentColors(accent),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "(${review.count})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Text(String.format(Locale.US, "%.1f", score), style = MaterialTheme.typography.titleLarge)
    }
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

private fun formatValue(rating: Float): String =
    if (rating % 1f == 0f) rating.toInt().toString() else String.format(Locale.US, "%.2f", rating).trimEnd('0')

/** Keeps the current rating on the new step grid when the step changes. */
private fun snapForDemo(rating: Float, step: Float): Float =
    if (step <= 0f) rating else ceil(rating / step - 1e-3f) * step

@Preview
@Composable
private fun RatingBarDemoPreview() {
    ComposeComponentsTheme(darkTheme = false) {
        RatingBarDemoScreen(isDark = false, onToggleTheme = {}, onBack = {})
    }
}

@Preview
@Composable
private fun RatingBarDemoDarkPreview() {
    ComposeComponentsTheme(darkTheme = true) {
        RatingBarDemoScreen(isDark = true, onToggleTheme = {}, onBack = {})
    }
}
