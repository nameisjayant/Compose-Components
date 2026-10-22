package com.nameisjayant.components.colorpicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * A dialog that hosts a [ComposeColorPicker], with an optional title and action buttons along the
 * bottom. The content scrolls when the screen is too short for it.
 *
 * ```
 * val state = rememberComposeColorPickerState(initialColor = current)
 * ComposeColorPickerDialog(
 *     onDismissRequest = { show = false },
 *     title = { Text("Pick a color") },
 *     confirmButton = { TextButton(onClick = { save(state.color); show = false }) { Text("OK") } },
 *     dismissButton = { TextButton(onClick = { show = false }) { Text("Cancel") } },
 * ) {
 *     ComposeColorPicker(state, Modifier.padding(horizontal = 24.dp), originalColor = current)
 * }
 * ```
 */
@Composable
public fun ComposeColorPickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    shape: Shape = RoundedCornerShape(28.dp),
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    tonalElevation: Dp = 6.dp,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        Surface(
            modifier = modifier
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .widthIn(max = 380.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = shape,
            color = containerColor,
            contentColor = contentColorFor(containerColor),
            tonalElevation = tonalElevation,
        ) {
            Column {
                if (title != null) {
                    Box(Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 16.dp)) {
                        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                            ProvideTextStyle(MaterialTheme.typography.headlineSmall, title)
                        }
                    }
                }
                Column(
                    Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                        .padding(top = if (title == null) 24.dp else 0.dp),
                    content = content,
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    dismissButton?.invoke()
                    confirmButton()
                }
            }
        }
    }
}
