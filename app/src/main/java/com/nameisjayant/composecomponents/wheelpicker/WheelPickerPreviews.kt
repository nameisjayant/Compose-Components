package com.nameisjayant.composecomponents.wheelpicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.components.wheelpicker.ComposeWheelPicker
import com.nameisjayant.components.wheelpicker.ComposeWheelPickerDefaults
import com.nameisjayant.components.wheelpicker.WheelPickerStyle
import com.nameisjayant.composecomponents.ui.theme.ComposeComponentsTheme

private val Numbers = (0..59).map { "%02d".format(it) }

@Composable
private fun WheelSamples() {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ComposeWheelPicker(items = Numbers, selectedIndex = 30, onSelectedIndexChange = {}, cyclic = true)
                ComposeWheelPicker(
                    items = Numbers,
                    selectedIndex = 12,
                    onSelectedIndexChange = {},
                    style = WheelPickerStyle.Flat,
                    selectedTextStyle = ComposeWheelPickerDefaults.textStyle().copy(fontWeight = FontWeight.Bold),
                    colors = ComposeWheelPickerDefaults.accentColors(MaterialTheme.colorScheme.tertiary),
                    selectionIndicator = { ComposeWheelPickerDefaults.SelectionLines() },
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ComposeWheelPicker(
                    items = listOf("Small", "Medium", "Large", "X-Large"),
                    selectedIndex = 0,
                    onSelectedIndexChange = {},
                    visibleItemCount = 3,
                    isItemEnabled = { it != 1 },
                )
                ComposeWheelPicker(items = Numbers, selectedIndex = 5, onSelectedIndexChange = {}, enabled = false, visibleItemCount = 3)
            }
        }
    }
}

@Preview
@Composable
private fun WheelLightPreview() = ComposeComponentsTheme(darkTheme = false) { WheelSamples() }

@Preview
@Composable
private fun WheelDarkPreview() = ComposeComponentsTheme(darkTheme = true) { WheelSamples() }
