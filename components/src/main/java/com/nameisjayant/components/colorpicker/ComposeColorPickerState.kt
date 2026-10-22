package com.nameisjayant.components.colorpicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/**
 * Holds the color a [ComposeColorPicker] shows. Read [color] (or [hsvColor]) for the current pick and
 * set either to change it from code.
 *
 * ```
 * val state = rememberComposeColorPickerState(initialColor = Color(0xFF3D5AFE))
 * ComposeColorPicker(state)
 * Button(onClick = { save(state.color) }) { Text("Save") }
 * ```
 */
@Stable
public class ComposeColorPickerState(initialColor: Color = Color.Red) {

    /** The current color as hue, saturation, value and alpha; keeps the hue of grays. */
    public var hsvColor: HsvColor by mutableStateOf(HsvColor.from(initialColor))

    /**
     * The current color. Setting a gray keeps the current hue, so the hue slider doesn't jump to red.
     */
    public var color: Color
        get() = hsvColor.toColor()
        set(value) {
            hsvColor = HsvColor.from(value, previous = hsvColor)
        }

    public companion object {
        /** Saves the color, hue included. */
        public val Saver: Saver<ComposeColorPickerState, *> = listSaver(
            save = { listOf(it.hsvColor.hue, it.hsvColor.saturation, it.hsvColor.value, it.hsvColor.alpha) },
            restore = { ComposeColorPickerState().apply { hsvColor = HsvColor(it[0], it[1], it[2], it[3]) } },
        )
    }
}

/** A [ComposeColorPickerState] that survives configuration changes and process death. */
@Composable
public fun rememberComposeColorPickerState(initialColor: Color = Color.Red): ComposeColorPickerState =
    rememberSaveable(saver = ComposeColorPickerState.Saver) { ComposeColorPickerState(initialColor) }
