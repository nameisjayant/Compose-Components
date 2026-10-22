package com.nameisjayant.composecomponents.colorpicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.components.colorpicker.ColorPickerStyle
import com.nameisjayant.components.colorpicker.ComposeColorPicker
import com.nameisjayant.composecomponents.ui.theme.ComposeComponentsTheme

@Composable
private fun PickerSamples() {
    Surface(color = MaterialTheme.colorScheme.background) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            ColorPickerStyle.entries.forEach { style ->
                ComposeColorPicker(
                    color = Color(0xCC3D5AFE),
                    onColorChange = {},
                    modifier = Modifier.width(280.dp),
                    style = style,
                    originalColor = Color(0xFFE91E63),
                )
            }
        }
    }
}

@Preview(widthDp = 940)
@Composable
private fun ColorPickerLightPreview() = ComposeComponentsTheme(darkTheme = false) { PickerSamples() }

@Preview(widthDp = 940)
@Composable
private fun ColorPickerDarkPreview() = ComposeComponentsTheme(darkTheme = true) { PickerSamples() }
