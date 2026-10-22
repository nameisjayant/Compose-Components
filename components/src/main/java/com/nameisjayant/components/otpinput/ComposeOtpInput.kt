package com.nameisjayant.components.otpinput

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.password
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import com.nameisjayant.components.R
import kotlinx.coroutines.delay

/**
 * A one-time-code field split into cells. Typing fills the cells one by one, backspace clears them,
 * and pasting or SMS autofill drops a whole code in at once (anything [charFilter] rejects, like the
 * dash in "482-913", is skipped).
 *
 * The input is stateless: hold the code yourself and update it in [onValueChange].
 *
 * ```
 * var code by rememberSaveable { mutableStateOf("") }
 * ComposeOtpInput(value = code, onValueChange = { code = it }, onComplete = { verify(it) })
 * ```
 *
 * Cells always run left to right, also in right-to-left layouts, since codes are read that way.
 *
 * @param value the code entered so far; only the first [length] characters are shown.
 * @param onValueChange called with the new code as the user types, deletes or pastes.
 * @param length number of cells.
 * @param onComplete called once the user fills the last cell, with the full code.
 * @param enabled when false the input ignores focus and input and uses the disabled colors.
 * @param readOnly the code can be focused and copied but not changed.
 * @param isError draws the error colors; with [animation] the row shakes when this turns true.
 * @param inputType which characters are accepted and which keyboard opens; see [OtpInputType].
 * @param charFilter overrides which characters [inputType] accepts.
 * @param uppercase turns letters into capitals as they are typed or pasted.
 * @param mask shows [maskChar] instead of each character, like a password.
 * @param maskChar the character a masked cell shows.
 * @param revealMillis how long a masked input shows the character just typed; 0 hides it right away.
 * @param placeholder shown in empty cells, e.g. '0' or '-'; null shows nothing.
 * @param cellStyle outlined, filled or underlined cells; pair it with [ComposeOtpInputDefaults.colors] for
 * the same style.
 * @param cellSize width and height of each cell. An unspecified width ([Dp.Unspecified]) shares the
 * available width between the cells; an unspecified height makes them square.
 * @param cellSpacing gap between cells (and around separators).
 * @param cellShape outline of each cell; see [ComposeOtpInputDefaults.CircleCellShape] for round cells.
 * Underlined cells only use it for their container.
 * @param borderWidth border (or underline) width of idle cells.
 * @param focusedBorderWidth border width of the active and error cells.
 * @param textStyle style of the characters; its color is replaced by [colors].
 * @param colors see [ComposeOtpInputDefaults.colors].
 * @param groupSize splits the cells into groups of this many with [separator] between them, e.g. 3
 * for "123 – 456"; 0 = no groups.
 * @param separator drawn between groups.
 * @param showCursor draw a cursor in the active cell.
 * @param autoFocus focus the input and open the keyboard when it first appears.
 * @param clearFocusOnComplete close the keyboard once the last cell is filled.
 * @param keyboardType the keyboard to open; follows [inputType] by default.
 * @param imeAction the keyboard's action button.
 * @param keyboardActions what the keyboard's action button does.
 * @param animation entry, cursor and error animations; [OtpInputAnimation.None] turns them off.
 * @param hapticFeedbackEnabled a confirm tick when the code is complete and a buzz on error.
 * @param interactionSource observe focus and press interactions of the underlying text field.
 * @param cellContent draw each cell yourself from its [OtpCell] state instead of the default cell; it is
 * placed in a centered box of [cellSize].
 */
@Composable
public fun ComposeOtpInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    onComplete: ((String) -> Unit)? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    inputType: OtpInputType = OtpInputType.Digits,
    charFilter: (Char) -> Boolean = inputType::accepts,
    uppercase: Boolean = false,
    mask: Boolean = false,
    maskChar: Char = ComposeOtpInputDefaults.MaskChar,
    revealMillis: Long = ComposeOtpInputDefaults.RevealMillis,
    placeholder: Char? = null,
    cellStyle: OtpCellStyle = OtpCellStyle.Outlined,
    cellSize: DpSize = ComposeOtpInputDefaults.CellSize,
    cellSpacing: Dp = ComposeOtpInputDefaults.CellSpacing,
    cellShape: Shape = ComposeOtpInputDefaults.CellShape,
    borderWidth: Dp = ComposeOtpInputDefaults.BorderWidth,
    focusedBorderWidth: Dp = ComposeOtpInputDefaults.FocusedBorderWidth,
    textStyle: TextStyle = ComposeOtpInputDefaults.textStyle(),
    colors: ComposeOtpInputColors = ComposeOtpInputDefaults.colors(cellStyle),
    groupSize: Int = 0,
    separator: @Composable () -> Unit = { ComposeOtpInputDefaults.Separator() },
    showCursor: Boolean = true,
    autoFocus: Boolean = false,
    clearFocusOnComplete: Boolean = true,
    keyboardType: KeyboardType = inputType.keyboardType,
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    animation: OtpInputAnimation = OtpInputAnimation.Default,
    hapticFeedbackEnabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    cellContent: (@Composable (OtpCell) -> Unit)? = null,
) {
    require(length >= 1) { "length must be at least 1, was $length" }
    require(groupSize >= 0) { "groupSize can't be negative, was $groupSize" }
    require(revealMillis >= 0L) { "revealMillis can't be negative, was $revealMillis" }

    val code = value.take(length)
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val haptics = LocalHapticFeedback.current
    var focused by remember { mutableStateOf(false) }
    // Masked cell that briefly shows its real character; -1 = none.
    var revealIndex by remember { mutableIntStateOf(-1) }

    // The cursor always sits at the end, so typing appends and backspace removes the last character.
    var fieldValue by remember { mutableStateOf(TextFieldValue(code, TextRange(code.length))) }
    val shownValue = if (fieldValue.text == code) fieldValue else TextFieldValue(code, TextRange(code.length))

    LaunchedEffect(Unit) {
        if (autoFocus && enabled) {
            focusRequester.requestFocus()
            keyboard?.show()
        }
    }
    LaunchedEffect(revealIndex) {
        if (revealIndex >= 0) {
            delay(revealMillis)
            revealIndex = -1
        }
    }

    val shake = remember { Animatable(0f) }
    val shakePx = with(LocalDensity.current) { animation.shakeDistance.toPx() }
    var wasError by remember { mutableStateOf(isError) }
    LaunchedEffect(isError) {
        if (isError && !wasError) {
            if (hapticFeedbackEnabled) haptics.performHapticFeedback(HapticFeedbackType.Reject)
            if (animation.enabled && animation.shakeOnError) {
                shake.animateTo(
                    targetValue = 0f,
                    animationSpec = keyframes {
                        durationMillis = 420
                        -shakePx at 60
                        shakePx at 130
                        -shakePx * 0.7f at 200
                        shakePx * 0.7f at 270
                        -shakePx * 0.3f at 340
                    },
                )
            }
        }
        wasError = isError
    }

    val stateText = stringResource(R.string.ccotp_state_description, code.length, length)
    val selectionColors = TextSelectionColors(handleColor = colors.cursorColor, backgroundColor = Color.Transparent)

    CompositionLocalProvider(LocalTextSelectionColors provides selectionColors) {
        BasicTextField(
            value = shownValue,
            onValueChange = { new ->
                val clean = if (new.text == code) code else sanitizeOtp(code, new.text, length, uppercase, charFilter)
                fieldValue = TextFieldValue(
                    text = clean,
                    selection = TextRange(clean.length),
                    composition = new.composition.takeIf { clean == new.text },
                )
                if (clean == code) return@BasicTextField
                revealIndex = if (mask && revealMillis > 0L && clean.length == code.length + 1) clean.lastIndex else -1
                onValueChange(clean)
                if (clean.length == length) {
                    if (hapticFeedbackEnabled) haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                    onComplete?.invoke(clean)
                    if (clearFocusOnComplete) {
                        keyboard?.hide()
                        focusManager.clearFocus()
                    }
                }
            },
            modifier = modifier
                .focusRequester(focusRequester)
                .onFocusChanged { focused = it.isFocused }
                .semantics {
                    contentType = ContentType.SmsOtpCode
                    stateDescription = stateText
                    if (mask) password()
                },
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle.copy(color = Color.Transparent),
            keyboardOptions = KeyboardOptions(
                capitalization = if (uppercase) KeyboardCapitalization.Characters else KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                keyboardType = keyboardType,
                imeAction = imeAction,
            ),
            keyboardActions = keyboardActions,
            singleLine = true,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(Color.Transparent),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.Center) {
                    // The real text field stays invisible; it only handles the keyboard, paste and autofill.
                    Box(Modifier.matchParentSize().alpha(0f)) { innerTextField() }
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Row(
                            Modifier
                                .then(if (cellSize.width.isSpecified) Modifier else Modifier.fillMaxWidth())
                                .graphicsLayer { translationX = shake.value },
                            horizontalArrangement = Arrangement.spacedBy(cellSpacing),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            val active = if (focused && enabled && !readOnly) activeCellIndex(code.length, length) else -1
                            for (index in 0 until length) {
                                val char = code.getOrNull(index)
                                val text = when {
                                    char == null -> ""
                                    mask && index != revealIndex -> maskChar.toString()
                                    else -> char.toString()
                                }
                                val cell = OtpCell(
                                    index = index,
                                    char = char,
                                    text = text,
                                    isActive = index == active,
                                    isError = isError,
                                    isEnabled = enabled,
                                )
                                val cellModifier = cellModifier(cellSize)
                                if (cellContent != null) {
                                    Box(cellModifier, contentAlignment = Alignment.Center) { cellContent(cell) }
                                } else {
                                    OtpCellBox(
                                        modifier = cellModifier,
                                        cell = cell,
                                        style = cellStyle,
                                        cursorHeight = if (cellSize.height.isSpecified) cellSize.height * 0.42f else 24.dp,
                                        shape = cellShape,
                                        borderWidth = borderWidth,
                                        focusedBorderWidth = focusedBorderWidth,
                                        textStyle = textStyle,
                                        colors = colors,
                                        placeholder = placeholder,
                                        showCursor = showCursor && index == active && char == null,
                                        animation = animation,
                                    )
                                }
                                if (separatorAfter(index, groupSize, length)) separator()
                            }
                        }
                    }
                }
            },
        )
    }
}

/** Fixed, width-sharing or square cells; see the `cellSize` parameter. */
private fun RowScope.cellModifier(size: DpSize): Modifier = when {
    size.width.isSpecified && size.height.isSpecified -> Modifier.size(size)
    size.width.isSpecified -> Modifier.size(size.width)
    size.height.isSpecified -> Modifier.weight(1f).height(size.height)
    else -> Modifier.weight(1f).aspectRatio(1f)
}

/** The default cell: container, border or underline, the character, and the cursor. */
@Composable
private fun OtpCellBox(
    modifier: Modifier,
    cell: OtpCell,
    style: OtpCellStyle,
    cursorHeight: Dp,
    shape: Shape,
    borderWidth: Dp,
    focusedBorderWidth: Dp,
    textStyle: TextStyle,
    colors: ComposeOtpInputColors,
    placeholder: Char?,
    showCursor: Boolean,
    animation: OtpInputAnimation,
) {
    val colorSpec = tween<Color>(durationMillis = 200)
    val containerColor by animateColorAsState(colors.container(cell), colorSpec, label = "OtpContainer")
    val borderColor by animateColorAsState(colors.border(cell), colorSpec, label = "OtpBorder")
    val textColor by animateColorAsState(colors.text(cell), colorSpec, label = "OtpText")
    val strokeWidth by animateDpAsState(
        targetValue = if (cell.isActive || cell.isError) focusedBorderWidth else borderWidth,
        animationSpec = tween(200),
        label = "OtpBorderWidth",
    )

    // Bump the cell when a character lands in it (not for characters it already had).
    val scale = remember { Animatable(1f) }
    var wasFilled by remember { mutableStateOf(cell.isFilled) }
    LaunchedEffect(cell.isFilled) {
        if (cell.isFilled && !wasFilled && animation.enabled && animation.popScale != 1f) {
            scale.animateTo(animation.popScale, tween(durationMillis = 80))
            scale.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMedium))
        }
        wasFilled = cell.isFilled
    }

    Box(
        modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .drawWithCache {
                val strokePx = strokeWidth.toPx()
                val outline = shape.createOutline(this.size, layoutDirection, this)
                // Inset by half the stroke so the border stays inside the cell.
                val borderPath = Path().apply {
                    val inner = Size(
                        (this@drawWithCache.size.width - strokePx).coerceAtLeast(0f),
                        (this@drawWithCache.size.height - strokePx).coerceAtLeast(0f),
                    )
                    addOutline(shape.createOutline(inner, layoutDirection, this@drawWithCache))
                    translate(Offset(strokePx / 2f, strokePx / 2f))
                }
                val stroke = Stroke(width = strokePx, join = StrokeJoin.Round)
                onDrawBehind {
                    if (containerColor.alpha > 0f) drawOutline(outline, containerColor)
                    if (strokePx <= 0f || borderColor.alpha <= 0f) return@onDrawBehind
                    when (style) {
                        OtpCellStyle.Underlined -> drawRect(
                            color = borderColor,
                            topLeft = Offset(0f, size.height - strokePx),
                            size = Size(size.width, strokePx),
                        )
                        else -> drawPath(borderPath, borderColor, style = stroke)
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        val slide = animation.enabled && animation.slideInText
        AnimatedContent(
            targetState = cell.text,
            transitionSpec = {
                val enter = if (slide && targetState.isNotEmpty()) {
                    slideInVertically(tween(180)) { it / 2 } + fadeIn(tween(180))
                } else {
                    fadeIn(tween(if (animation.enabled) 120 else 0))
                }
                (enter togetherWith fadeOut(tween(if (animation.enabled) 90 else 0)))
                    .using(SizeTransform(clip = false))
            },
            label = "OtpChar",
        ) { text ->
            when {
                text.isNotEmpty() -> Text(text = text, style = textStyle, color = textColor, maxLines = 1)
                placeholder != null && !showCursor -> Text(
                    text = placeholder.toString(),
                    style = textStyle,
                    color = colors.placeholderColor,
                    maxLines = 1,
                )
            }
        }
        if (showCursor) OtpCursor(cursorHeight, colors.cursorColor, blink = animation.enabled && animation.blinkCursor)
    }
}

/** A thin bar that blinks; the alpha is read in the draw phase so the cell doesn't recompose. */
@Composable
private fun OtpCursor(height: Dp, color: Color, blink: Boolean) {
    val alpha = remember { Animatable(1f) }
    LaunchedEffect(blink) {
        alpha.snapTo(1f)
        if (!blink) return@LaunchedEffect
        while (true) {
            delay(500)
            alpha.snapTo(0f)
            delay(500)
            alpha.snapTo(1f)
        }
    }
    Box(
        Modifier
            .width(ComposeOtpInputDefaults.FocusedBorderWidth)
            .height(height)
            .graphicsLayer { this.alpha = alpha.value }
            .background(color),
    )
}
