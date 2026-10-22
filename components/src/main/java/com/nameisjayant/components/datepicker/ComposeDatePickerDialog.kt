package com.nameisjayant.components.datepicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * A dialog that hosts a [ComposeDatePicker], with action buttons along the bottom.
 *
 * ```
 * ComposeDatePickerDialog(
 *     onDismissRequest = { show = false },
 *     confirmButton = { TextButton(onClick = { save(state.selectedDate); show = false }) { Text("OK") } },
 *     dismissButton = { TextButton(onClick = { show = false }) { Text("Cancel") } },
 * ) {
 *     ComposeDatePicker(state)
 * }
 * ```
 *
 * @param containerColor dialog background; match it to the picker's
 *   [ComposeDatePickerColors.containerColor] (both default to `surfaceContainerHigh`).
 */
@Composable
public fun ComposeDatePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: (@Composable () -> Unit)? = null,
    shape: Shape = ComposeDatePickerDefaults.DialogShape,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    tonalElevation: Dp = 6.dp,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        Surface(
            modifier = modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = 380.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = shape,
            color = containerColor,
            contentColor = contentColorFor(containerColor),
            tonalElevation = tonalElevation,
        ) {
            Column {
                content()
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(start = 12.dp, end = 12.dp, bottom = 8.dp),
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
