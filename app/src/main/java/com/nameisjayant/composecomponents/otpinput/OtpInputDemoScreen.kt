package com.nameisjayant.composecomponents.otpinput

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.nameisjayant.components.otpinput.ComposeOtpInput
import com.nameisjayant.components.otpinput.ComposeOtpInputDefaults
import com.nameisjayant.components.otpinput.OtpCell
import com.nameisjayant.components.otpinput.OtpCellStyle
import com.nameisjayant.components.otpinput.OtpInputAnimation
import com.nameisjayant.components.otpinput.OtpInputType
import com.nameisjayant.composecomponents.ui.components.AccentSwatch
import com.nameisjayant.composecomponents.ui.components.DemoCard
import com.nameisjayant.composecomponents.ui.components.DemoTopBar
import com.nameisjayant.composecomponents.ui.components.SectionLabel
import com.nameisjayant.composecomponents.ui.components.SwitchRow
import com.nameisjayant.composecomponents.ui.theme.ComposeComponentsTheme
import kotlinx.coroutines.delay

private enum class CodeLength(val label: String, val cells: Int) {
    Four("4", 4),
    Five("5", 5),
    Six("6", 6),
    Eight("8", 8),
}

private enum class CellLook(val label: String) {
    Outlined("Outlined"),
    Filled("Filled"),
    Underlined("Line"),
    Circle("Circle"),
}

private enum class CodeType(val label: String, val type: OtpInputType, val sample: String) {
    Digits("Digits", OtpInputType.Digits, "12345678"),
    Letters("Letters", OtpInputType.Letters, "ABCDEFGH"),
    Mixed("Mixed", OtpInputType.Alphanumeric, "A1B2C3D4"),
}

/** Accent palettes for the "Theme" section; each has a light and a dark variant. */
private enum class OtpAccent(val label: String, val light: Color?, val dark: Color?) {
    App("App theme", null, null),
    Coral("Coral", Color(0xFFD9482F), Color(0xFFFF8A73)),
    Rose("Rose", Color(0xFFC92E68), Color(0xFFFF7EAA)),
    Emerald("Emerald", Color(0xFF0F8A66), Color(0xFF3DD6A0)),
    Amber("Amber", Color(0xFFB86E00), Color(0xFFFFC233)),
}

private enum class Verification { Waiting, Wrong, Verified }

private const val ResendSeconds = 30

@Composable
fun OtpInputDemoScreen(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    onBack: () -> Unit,
) {
    var code by rememberSaveable { mutableStateOf("") }
    var verification by rememberSaveable { mutableStateOf(Verification.Waiting) }

    var codeLength by rememberSaveable { mutableStateOf(CodeLength.Six) }
    var look by rememberSaveable { mutableStateOf(CellLook.Outlined) }
    var codeType by rememberSaveable { mutableStateOf(CodeType.Digits) }
    var accent by rememberSaveable { mutableStateOf(OtpAccent.App) }

    // Behaviour
    var mask by rememberSaveable { mutableStateOf(false) }
    var placeholder by rememberSaveable { mutableStateOf(false) }
    var grouped by rememberSaveable { mutableStateOf(false) }
    var stretch by rememberSaveable { mutableStateOf(false) }
    var cursor by rememberSaveable { mutableStateOf(true) }
    var uppercase by rememberSaveable { mutableStateOf(true) }
    var closeKeyboard by rememberSaveable { mutableStateOf(true) }
    var enabled by rememberSaveable { mutableStateOf(true) }
    var readOnly by rememberSaveable { mutableStateOf(false) }
    var animate by rememberSaveable { mutableStateOf(true) }
    var haptics by rememberSaveable { mutableStateOf(true) }

    // PIN sample
    var pin by rememberSaveable { mutableStateOf("") }

    var resendIn by rememberSaveable { mutableIntStateOf(ResendSeconds) }
    LaunchedEffect(resendIn) {
        if (resendIn > 0) {
            delay(1_000)
            resendIn--
        }
    }

    val length = codeLength.cells
    val expected = codeType.sample.take(length)
    // Even lengths split in two halves: 12–34, 123–456, 1234–5678.
    val groupSize = if (grouped && length % 2 == 0) length / 2 else 0
    val cellStyle = when (look) {
        CellLook.Filled -> OtpCellStyle.Filled
        CellLook.Underlined -> OtpCellStyle.Underlined
        else -> OtpCellStyle.Outlined
    }
    val accentColor = (if (isDark) accent.dark else accent.light) ?: MaterialTheme.colorScheme.primary
    val colors = ComposeOtpInputDefaults.accentColors(accentColor, cellStyle)

    fun reset() {
        code = ""
        verification = Verification.Waiting
    }

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize()) {
            DemoTopBar(title = "OTP Input", isDark = isDark, onToggleTheme = onToggleTheme, onBack = onBack)

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
                        Text("Verify it's you", style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Enter the ${length}-character code we sent to +1 •••• 4210",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(24.dp))

                        BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            val spacing = if (length > 6) 6.dp else 8.dp
                            val cellSize = when {
                                stretch -> DpSize(Dp.Unspecified, 56.dp)
                                else -> fittedCellSize(maxWidth, length, spacing, groupSize, round = look == CellLook.Circle)
                            }
                            ComposeOtpInput(
                                value = code,
                                onValueChange = {
                                    code = it
                                    verification = Verification.Waiting
                                },
                                onComplete = {
                                    verification = if (it.equals(expected, ignoreCase = true)) Verification.Verified else Verification.Wrong
                                },
                                modifier = if (stretch) Modifier.fillMaxWidth() else Modifier,
                                length = length,
                                enabled = enabled,
                                readOnly = readOnly,
                                isError = verification == Verification.Wrong,
                                inputType = codeType.type,
                                uppercase = uppercase,
                                mask = mask,
                                placeholder = if (placeholder) '0' else null,
                                cellStyle = cellStyle,
                                cellSize = cellSize,
                                cellSpacing = spacing,
                                cellShape = if (look == CellLook.Circle) ComposeOtpInputDefaults.CircleCellShape else ComposeOtpInputDefaults.CellShape,
                                colors = if (verification == Verification.Verified) {
                                    colors.copy(filledBorderColor = successColor(isDark), textColor = successColor(isDark))
                                } else {
                                    colors
                                },
                                groupSize = groupSize,
                                showCursor = cursor,
                                clearFocusOnComplete = closeKeyboard,
                                animation = if (animate) OtpInputAnimation.Default else OtpInputAnimation.None,
                                hapticFeedbackEnabled = haptics,
                            )
                        }

                        Spacer(Modifier.height(20.dp))
                        Status(verification, expected, isDark)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TextButton(
                                onClick = {
                                    code = expected
                                    verification = Verification.Verified
                                },
                                enabled = enabled && !readOnly,
                            ) { Text("Fill from SMS") }
                            TextButton(onClick = ::reset, enabled = enabled && !readOnly && code.isNotEmpty()) { Text("Clear") }
                            TextButton(
                                onClick = {
                                    reset()
                                    resendIn = ResendSeconds
                                },
                                enabled = resendIn == 0,
                            ) { Text(if (resendIn > 0) "Resend in 0:%02d".format(resendIn) else "Resend") }
                        }
                    }
                }

                SectionLabel("Length")
                Segments(CodeLength.entries, codeLength, {
                    codeLength = it
                    reset()
                }) { it.label }

                SectionLabel("Cell style")
                Segments(CellLook.entries, look, { look = it }) { it.label }

                SectionLabel("Characters")
                Segments(CodeType.entries, codeType, {
                    codeType = it
                    reset()
                }) { it.label }

                SectionLabel("Behaviour")
                DemoCard {
                    SwitchRow("Mask", mask, { mask = it }, hint = "Each character shows for a moment, then hides")
                    Divider()
                    SwitchRow("Placeholder", placeholder, { placeholder = it }, hint = "Empty cells show 0")
                    Divider()
                    SwitchRow(
                        "Split in two groups",
                        grouped && length % 2 == 0,
                        { grouped = it },
                        hint = "groupSize = ${if (length % 2 == 0) length / 2 else "–"}",
                        enabled = length % 2 == 0,
                    )
                    Divider()
                    SwitchRow("Stretch to width", stretch, { stretch = it }, hint = "Cells share the row's width")
                    Divider()
                    SwitchRow("Cursor", cursor, { cursor = it })
                    Divider()
                    SwitchRow("Uppercase letters", uppercase, { uppercase = it }, enabled = codeType != CodeType.Digits)
                    Divider()
                    SwitchRow("Close keyboard when complete", closeKeyboard, { closeKeyboard = it })
                    Divider()
                    SwitchRow("Enabled", enabled, { enabled = it })
                    Divider()
                    SwitchRow("Read-only", readOnly, { readOnly = it })
                    Divider()
                    SwitchRow("Animations", animate, { animate = it }, hint = "Slide-in, pop, blinking cursor, error shake")
                    Divider()
                    SwitchRow("Haptics", haptics, { haptics = it }, hint = "Tick on complete, buzz on a wrong code")
                }

                SectionLabel("Theme")
                DemoCard {
                    Row(
                        Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        OtpAccent.entries.forEach { option ->
                            AccentSwatch(
                                label = option.label,
                                color = (if (isDark) option.dark else option.light) ?: MaterialTheme.colorScheme.primary,
                                selected = accent == option,
                                onClick = { accent = option },
                            )
                        }
                    }
                }

                SectionLabel("Custom cells")
                DemoCard {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Enter your PIN", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(16.dp))
                        ComposeOtpInput(
                            value = pin,
                            onValueChange = { pin = it },
                            length = 4,
                            mask = true,
                            cellSize = DpSize(28.dp, 28.dp),
                            cellSpacing = 20.dp,
                            animation = if (animate) OtpInputAnimation.Default else OtpInputAnimation.None,
                            hapticFeedbackEnabled = haptics,
                        ) { cell -> PinDot(cell, accentColor) }
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = { pin = "" }, enabled = pin.isNotEmpty()) { Text("Clear") }
                    }
                }

                Spacer(Modifier.height(32.dp))
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}

@Composable
private fun Status(verification: Verification, expected: String, isDark: Boolean) {
    AnimatedContent(
        targetState = verification,
        transitionSpec = {
            (fadeIn() + slideInVertically { it / 3 }) togetherWith (fadeOut() + slideOutVertically { -it / 3 })
        },
        label = "Status",
    ) { state ->
        val (text, color) = when (state) {
            Verification.Waiting -> "Hint: the code is $expected" to MaterialTheme.colorScheme.onSurfaceVariant
            Verification.Wrong -> "That code didn't match. Try again." to MaterialTheme.colorScheme.error
            Verification.Verified -> "Verified" to successColor(isDark)
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** A PIN-pad dot drawn through the input's `cellContent` slot. */
@Composable
private fun PinDot(cell: OtpCell, accent: Color) {
    val fill by animateColorAsState(if (cell.isFilled) accent else Color.Transparent, label = "PinFill")
    val ring by animateColorAsState(
        when {
            cell.isFilled -> accent
            cell.isActive -> accent.copy(alpha = 0.7f)
            else -> MaterialTheme.colorScheme.outline
        },
        label = "PinRing",
    )
    val scale by animateFloatAsState(if (cell.isFilled) 1f else 0.78f, label = "PinScale")
    Box(
        Modifier
            .size(20.dp)
            .scale(scale)
            .border(2.dp, ring, CircleShape)
            .background(fill, CircleShape),
    )
}

/** Cells as wide as fit the card, up to 52dp. */
private fun fittedCellSize(maxWidth: Dp, length: Int, spacing: Dp, groupSize: Int, round: Boolean): DpSize {
    val separators = if (groupSize > 0) length / groupSize - 1 else 0
    // Each separator is 12dp wide plus a gap on its far side.
    val available = maxWidth - spacing * (length - 1) - (12.dp + spacing) * separators
    val width = minOf(52.dp, available / length)
    return if (round) DpSize(width, width) else DpSize(width, width * 1.18f)
}

private fun successColor(isDark: Boolean): Color = if (isDark) Color(0xFF3DD6A0) else Color(0xFF0F8A66)

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
private fun OtpInputDemoPreview() {
    ComposeComponentsTheme(darkTheme = false) {
        OtpInputDemoScreen(isDark = false, onToggleTheme = {}, onBack = {})
    }
}

@Preview
@Composable
private fun OtpInputDemoDarkPreview() {
    ComposeComponentsTheme(darkTheme = true) {
        OtpInputDemoScreen(isDark = true, onToggleTheme = {}, onBack = {})
    }
}
