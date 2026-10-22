package com.nameisjayant.components.colorpicker

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nameisjayant.components.R

/**
 * A wrapping row of preset colors to pick from with one tap. The swatch matching [selectedColor] gets
 * a ring; see-through swatches show a checkerboard.
 *
 * @param swatches the colors to offer.
 * @param selectedColor the current color, or null to ring none.
 * @param onSwatchClick called with the tapped swatch.
 */
@Composable
public fun ColorSwatches(
    swatches: List<Color>,
    selectedColor: Color?,
    onSwatchClick: (Color) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ComposeColorPickerColors = ComposeColorPickerDefaults.colors(),
    swatchSize: Dp = ComposeColorPickerDefaults.SwatchSize,
    spacing: Dp = ComposeColorPickerDefaults.SwatchSpacing,
    shape: Shape = ComposeColorPickerDefaults.SwatchShape,
) {
    val selectedArgb = selectedColor?.toArgb()
    FlowRow(
        modifier = modifier.alpha(if (enabled) 1f else ComposeColorPickerDefaults.DisabledAlpha),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalArrangement = Arrangement.spacedBy(spacing),
    ) {
        swatches.forEach { swatch ->
            val argb = swatch.toArgb()
            val selected = argb == selectedArgb
            val hex = "#" + formatHex(argb, includeAlpha = swatch.alpha < 1f)
            Box(
                Modifier
                    .size(swatchSize)
                    .clip(shape)
                    .selectable(selected = selected, enabled = enabled, role = Role.RadioButton) { onSwatchClick(swatch) }
                    .semantics { contentDescription = hex }
                    .border(2.dp, if (selected) colors.selectedSwatchBorderColor else Color.Transparent, shape)
                    .padding(if (selected) 4.dp else 0.dp)
                    .clip(shape)
                    .drawBehind {
                        if (swatch.alpha < 1f) drawCheckerboard(colors.checkerboardLightColor, colors.checkerboardDarkColor)
                        drawRect(swatch)
                    }
                    .border(1.dp, colors.borderColor, shape),
            )
        }
    }
}

/**
 * A swatch of [color] over a checkerboard. With [originalColor] it splits in two, the original on the
 * start side, so a change can be compared with where it began; tapping that half calls
 * [onOriginalClick] (to go back to it, say).
 *
 * It is [ComposeColorPickerDefaults.PreviewWidth] by [ComposeColorPickerDefaults.FieldHeight] unless
 * [modifier] sizes it.
 */
@Composable
public fun ColorPreview(
    color: Color,
    modifier: Modifier = Modifier,
    originalColor: Color? = null,
    onOriginalClick: (() -> Unit)? = null,
    colors: ComposeColorPickerColors = ComposeColorPickerDefaults.colors(),
    shape: Shape = ComposeColorPickerDefaults.PreviewShape,
) {
    Row(
        modifier
            .size(ComposeColorPickerDefaults.PreviewWidth, ComposeColorPickerDefaults.FieldHeight)
            .clip(shape)
            .drawBehind { drawCheckerboard(colors.checkerboardLightColor, colors.checkerboardDarkColor) }
            .border(1.dp, colors.borderColor, shape),
    ) {
        if (originalColor != null) {
            val label = stringResource(R.string.cccp_original_color)
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .drawBehind { drawRect(originalColor) }
                    .then(
                        if (onOriginalClick != null) {
                            Modifier.clickable(onClickLabel = label, role = Role.Button, onClick = onOriginalClick)
                        } else {
                            Modifier
                        },
                    )
                    .semantics { contentDescription = label },
            )
        }
        Box(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .drawBehind { drawRect(color) },
        )
    }
}

/**
 * A text field for the color's hex code, after a '#'. It shows "RRGGBB", or "AARRGGBB" with
 * [showAlpha], and applies what is typed as soon as it is a whole code; "RGB" and "ARGB" short forms
 * apply when editing ends. Codes without alpha keep the current alpha. Stateless, like [HueSlider].
 */
@Composable
public fun ColorHexField(
    hsvColor: HsvColor,
    onHsvColorChange: (HsvColor) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showAlpha: Boolean = true,
    colors: ComposeColorPickerColors = ComposeColorPickerDefaults.colors(),
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
    shape: Shape = ComposeColorPickerDefaults.FieldShape,
    onColorChangeFinished: (() -> Unit)? = null,
) {
    val argb = hsvColor.toArgb()
    val alpha = argb ushr 24
    val length = if (showAlpha) 8 else 6
    var text by remember { mutableStateOf(formatHex(argb, showAlpha)) }
    var focused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val label = stringResource(R.string.cccp_hex)

    // Follow changes made elsewhere (drags, swatches) unless the text already spells this color.
    LaunchedEffect(argb, showAlpha) {
        if (parseHex(text, alpha) != argb || (!focused && text.length != length)) text = formatHex(argb, showAlpha)
    }

    fun apply(digits: String) {
        val parsed = parseHex(digits, alpha) ?: return
        if (parsed != argb) onHsvColorChange(hsvFromArgb(parsed, previous = hsvColor))
    }

    BasicTextField(
        value = text,
        onValueChange = { input ->
            val digits = sanitizeHex(input, length)
            text = digits
            if (digits.length == 6 || digits.length == 8) apply(digits)
        },
        modifier = modifier
            .height(ComposeColorPickerDefaults.FieldHeight)
            .onFocusChanged { state ->
                if (focused && !state.isFocused) {
                    // Editing ended: take a short form, then show the full code of whatever color is current.
                    apply(text)
                    onColorChangeFinished?.invoke()
                }
                focused = state.isFocused
                if (!state.isFocused) text = formatHex(hsvColor.toArgb(), showAlpha)
            }
            .semantics { contentDescription = label },
        enabled = enabled,
        singleLine = true,
        textStyle = textStyle.copy(color = colors.fieldTextColor),
        cursorBrush = SolidColor(colors.fieldFocusedBorderColor),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Characters,
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Ascii,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        decorationBox = { innerTextField ->
            Row(
                Modifier
                    .alpha(if (enabled) 1f else ComposeColorPickerDefaults.DisabledAlpha)
                    .clip(shape)
                    .drawBehind { drawRect(colors.fieldContainerColor) }
                    .border(
                        width = if (focused) 2.dp else 1.dp,
                        color = if (focused) colors.fieldFocusedBorderColor else colors.fieldBorderColor,
                        shape = shape,
                    )
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("#", style = textStyle, color = colors.fieldLabelColor)
                Spacer(Modifier.width(4.dp))
                Box(Modifier.weight(1f)) { innerTextField() }
            }
        },
    )
}
