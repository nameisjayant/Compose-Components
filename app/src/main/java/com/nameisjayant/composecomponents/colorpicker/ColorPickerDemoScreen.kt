package com.nameisjayant.composecomponents.colorpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.components.colorpicker.AlphaSlider
import com.nameisjayant.components.colorpicker.BrightnessSlider
import com.nameisjayant.components.colorpicker.ColorPickerStyle
import com.nameisjayant.components.colorpicker.ColorPreview
import com.nameisjayant.components.colorpicker.ColorSwatches
import com.nameisjayant.components.colorpicker.ComposeColorPicker
import com.nameisjayant.components.colorpicker.ComposeColorPickerColors
import com.nameisjayant.components.colorpicker.ComposeColorPickerDefaults
import com.nameisjayant.components.colorpicker.ComposeColorPickerDialog
import com.nameisjayant.components.colorpicker.HsvColor
import com.nameisjayant.components.colorpicker.HueSlider
import com.nameisjayant.components.colorpicker.SaturationSlider
import com.nameisjayant.components.colorpicker.rememberComposeColorPickerState
import com.nameisjayant.composecomponents.ui.components.AccentSwatch
import com.nameisjayant.composecomponents.ui.components.DemoCard
import com.nameisjayant.composecomponents.ui.components.DemoTopBar
import com.nameisjayant.composecomponents.ui.components.SectionLabel
import com.nameisjayant.composecomponents.ui.components.SwitchRow
import com.nameisjayant.composecomponents.ui.theme.ComposeComponentsTheme

private enum class PickerLook(val label: String, val style: ColorPickerStyle) {
    Wheel("Wheel", ColorPickerStyle.Wheel),
    Ring("Ring", ColorPickerStyle.Ring),
    Square("Square", ColorPickerStyle.Square),
}

private enum class SwatchSet(val label: String) {
    Default("Default"),
    Pastel("Pastel"),
    None("None"),
}

private val Pastels = listOf(
    Color(0xFFFFADAD), Color(0xFFFFD6A5), Color(0xFFFDFFB6), Color(0xFFCAFFBF),
    Color(0xFF9BF6FF), Color(0xFFA0C4FF), Color(0xFFBDB2FF), Color(0xFFFFC6FF),
)

/** Accent palettes for the "Theme" section; each has a light and a dark variant. */
private enum class PickerAccent(val label: String, val light: Color?, val dark: Color?) {
    App("App theme", null, null),
    Coral("Coral", Color(0xFFD9482F), Color(0xFFFF8A73)),
    Rose("Rose", Color(0xFFC92E68), Color(0xFFFF7EAA)),
    Emerald("Emerald", Color(0xFF0F8A66), Color(0xFF3DD6A0)),
    Amber("Amber", Color(0xFFB86E00), Color(0xFFFFC233)),
}

private val StartColor = Color(0xFF3D5AFE)

@Composable
fun ColorPickerDemoScreen(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    onBack: () -> Unit,
) {
    var look by rememberSaveable { mutableStateOf(PickerLook.Wheel) }
    var swatchSet by rememberSaveable { mutableStateOf(SwatchSet.Default) }
    var accent by rememberSaveable { mutableStateOf(PickerAccent.App) }

    var showAlpha by rememberSaveable { mutableStateOf(true) }
    var showPreview by rememberSaveable { mutableStateOf(true) }
    var showHex by rememberSaveable { mutableStateOf(true) }
    var compareOriginal by rememberSaveable { mutableStateOf(true) }
    var enabled by rememberSaveable { mutableStateOf(true) }

    val accentColor = (if (isDark) accent.dark else accent.light) ?: MaterialTheme.colorScheme.primary
    val colors = if (accent == PickerAccent.App) {
        ComposeColorPickerDefaults.colors()
    } else {
        ComposeColorPickerDefaults.accentColors(accentColor)
    }
    val swatches = when (swatchSet) {
        SwatchSet.Default -> ComposeColorPickerDefaults.Swatches
        SwatchSet.Pastel -> Pastels
        SwatchSet.None -> emptyList()
    }

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize()) {
            DemoTopBar(title = "Color Picker", isDark = isDark, onToggleTheme = onToggleTheme, onBack = onBack)

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                LabelCard(
                    style = look.style,
                    colors = colors,
                    swatches = swatches,
                    showAlpha = showAlpha,
                    showPreview = showPreview,
                    showHex = showHex,
                    compareOriginal = compareOriginal,
                    enabled = enabled,
                )

                SectionLabel("Style")
                Segments(PickerLook.entries, look, { look = it }) { it.label }

                SectionLabel("Swatches")
                Segments(SwatchSet.entries, swatchSet, { swatchSet = it }) { it.label }

                SectionLabel("Parts")
                DemoCard {
                    SwitchRow("Alpha slider", showAlpha, { showAlpha = it }, hint = "Hex field shows AARRGGBB too")
                    Divider()
                    SwitchRow("Preview", showPreview, { showPreview = it })
                    Divider()
                    SwitchRow(
                        "Compare with original",
                        compareOriginal,
                        { compareOriginal = it },
                        hint = "originalColor: tap its half to go back",
                        enabled = showPreview,
                    )
                    Divider()
                    SwitchRow("Hex field", showHex, { showHex = it })
                    Divider()
                    SwitchRow("Enabled", enabled, { enabled = it })
                }

                SectionLabel("Dialog")
                DialogCard(colors)

                SectionLabel("Build your own")
                SlidersCard(colors, enabled)

                SectionLabel("Theme")
                DemoCard {
                    Row(
                        Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        PickerAccent.entries.forEach { option ->
                            AccentSwatch(
                                label = option.label,
                                color = (if (isDark) option.dark else option.light) ?: MaterialTheme.colorScheme.primary,
                                selected = accent == option,
                                onClick = { accent = option },
                            )
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}

/**
 * The full picker on [rememberComposeColorPickerState], tinting a label chip live. Each finished
 * change goes to a "Recent" row through `onColorChangeFinished`.
 */
@Composable
private fun LabelCard(
    style: ColorPickerStyle,
    colors: ComposeColorPickerColors,
    swatches: List<Color>,
    showAlpha: Boolean,
    showPreview: Boolean,
    showHex: Boolean,
    compareOriginal: Boolean,
    enabled: Boolean,
) {
    val state = rememberComposeColorPickerState(initialColor = StartColor)
    val recents = remember { mutableStateListOf<Color>() }

    DemoCard(Modifier.padding(top = 4.dp)) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Label color", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                LabelChip("Design", state.color)
            }
            Spacer(Modifier.height(20.dp))
            ComposeColorPicker(
                state = state,
                onColorChangeFinished = { color ->
                    recents.remove(color)
                    recents.add(0, color)
                    if (recents.size > 8) recents.removeAt(recents.lastIndex)
                },
                style = style,
                enabled = enabled,
                showAlpha = showAlpha,
                showPreview = showPreview,
                showHexField = showHex,
                originalColor = if (compareOriginal) StartColor else null,
                swatches = swatches,
                colors = colors,
            )
            if (recents.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Text(
                    "Recent",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                ColorSwatches(
                    swatches = recents,
                    selectedColor = state.color,
                    onSwatchClick = { state.color = it },
                    enabled = enabled,
                    colors = colors,
                    swatchSize = 28.dp,
                    spacing = 8.dp,
                )
            }
        }
    }
}

/** A chip filled with [color], with text that stays readable on it. */
@Composable
private fun LabelChip(text: String, color: Color) {
    val onColor = if (color.alpha < 0.5f || color.luminance() > 0.5f) Color.Black else Color.White
    Box(
        Modifier
            .background(color, RoundedCornerShape(50))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge, color = onColor)
    }
}

/** [ComposeColorPickerDialog] around the value/callback overload; OK keeps the draft, Cancel drops it. */
@Composable
private fun DialogCard(colors: ComposeColorPickerColors) {
    var savedArgb by rememberSaveable { mutableIntStateOf(0xFFE91E63.toInt()) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val saved = Color(savedArgb)

    DemoCard {
        Row(
            Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                Modifier
                    .size(44.dp)
                    .background(saved, CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
            )
            Column(Modifier.weight(1f)) {
                Text("Accent", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "#" + Integer.toHexString(savedArgb).uppercase().takeLast(6),
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            FilledTonalButton(onClick = { showDialog = true }) { Text("Change") }
        }
    }

    if (showDialog) {
        var draft by remember { mutableStateOf(saved) }
        ComposeColorPickerDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Pick an accent") },
            confirmButton = {
                TextButton(onClick = {
                    savedArgb = draft.toArgb()
                    showDialog = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } },
        ) {
            ComposeColorPicker(
                color = draft,
                onColorChange = { draft = it },
                modifier = Modifier.padding(horizontal = 24.dp),
                style = ColorPickerStyle.Ring,
                showAlpha = false,
                originalColor = saved,
                swatches = emptyList(),
                colors = colors,
            )
        }
    }
}

/** A picker made only of sliders, built from the library's parts around one shared state. */
@Composable
private fun SlidersCard(colors: ComposeColorPickerColors, enabled: Boolean) {
    val state = rememberComposeColorPickerState(initialColor = Color(0xCC00BFA5))
    val onChange: (HsvColor) -> Unit = { state.hsvColor = it }

    DemoCard {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ColorPreview(color = state.color, colors = colors)
                Text(
                    "HueSlider, SaturationSlider, BrightnessSlider and AlphaSlider",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            HueSlider(state.hsvColor, onChange, enabled = enabled, colors = colors)
            SaturationSlider(state.hsvColor, onChange, enabled = enabled, colors = colors)
            BrightnessSlider(state.hsvColor, onChange, enabled = enabled, colors = colors)
            AlphaSlider(state.hsvColor, onChange, enabled = enabled, colors = colors)
        }
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

@Preview
@Composable
private fun ColorPickerDemoPreview() {
    ComposeComponentsTheme(darkTheme = false) {
        ColorPickerDemoScreen(isDark = false, onToggleTheme = {}, onBack = {})
    }
}

@Preview
@Composable
private fun ColorPickerDemoDarkPreview() {
    ComposeComponentsTheme(darkTheme = true) {
        ColorPickerDemoScreen(isDark = true, onToggleTheme = {}, onBack = {})
    }
}
