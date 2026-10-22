package com.nameisjayant.composecomponents.otpinput

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.nameisjayant.components.otpinput.ComposeOtpInput
import com.nameisjayant.components.otpinput.ComposeOtpInputDefaults
import com.nameisjayant.components.otpinput.OtpCellStyle
import com.nameisjayant.components.otpinput.OtpInputType
import com.nameisjayant.composecomponents.ui.theme.ComposeComponentsTheme

@Composable
private fun OtpSamples() {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ComposeOtpInput(value = "1234", onValueChange = {})
            ComposeOtpInput(
                value = "482",
                onValueChange = {},
                cellStyle = OtpCellStyle.Filled,
                groupSize = 3,
                cellSize = DpSize(40.dp, 48.dp),
            )
            ComposeOtpInput(value = "12", onValueChange = {}, cellStyle = OtpCellStyle.Underlined, placeholder = '0')
            ComposeOtpInput(value = "9071", onValueChange = {}, length = 4, mask = true, isError = true)
            ComposeOtpInput(
                value = "AB1",
                onValueChange = {},
                length = 5,
                inputType = OtpInputType.Alphanumeric,
                cellShape = ComposeOtpInputDefaults.CircleCellShape,
                cellSize = DpSize(48.dp, 48.dp),
                colors = ComposeOtpInputDefaults.accentColors(MaterialTheme.colorScheme.tertiary),
            )
            ComposeOtpInput(value = "55", onValueChange = {}, length = 4, enabled = false)
        }
    }
}

@Preview
@Composable
private fun OtpLightPreview() = ComposeComponentsTheme(darkTheme = false) { OtpSamples() }

@Preview
@Composable
private fun OtpDarkPreview() = ComposeComponentsTheme(darkTheme = true) { OtpSamples() }
