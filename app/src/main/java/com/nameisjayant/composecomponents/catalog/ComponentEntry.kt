package com.nameisjayant.composecomponents.catalog

import androidx.compose.ui.graphics.vector.ImageVector
import com.nameisjayant.composecomponents.ui.components.AppIcons

/**
 * Every component the library has or plans to have. The catalog lists them in this order;
 * [available] ones open their demo, the rest show "Soon".
 * Adding a component = flip [available] + add its demo branch in MainActivity's ComponentDemo.
 */
enum class ComponentEntry(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val available: Boolean,
) {
    DatePicker(
        title = "Date Picker",
        description = "Single, range and multi-date selection with disabled dates, decorations and theming.",
        icon = AppIcons.Calendar,
        available = true,
    ),
    WheelPicker(
        title = "Wheel Picker",
        description = "Scrollable wheel for numbers, times and lists.",
        icon = AppIcons.Wheel,
        available = false,
    ),
    OtpInput(
        title = "OTP Input",
        description = "Segmented code field with paste and auto-advance.",
        icon = AppIcons.Pin,
        available = false,
    ),
    RatingBar(
        title = "Rating Bar",
        description = "Star rating with half steps and custom shapes.",
        icon = AppIcons.Star,
        available = false,
    ),
    Timeline(
        title = "Timeline",
        description = "Vertical steps connected by a track.",
        icon = AppIcons.Timeline,
        available = false,
    ),
    ColorPicker(
        title = "Color Picker",
        description = "Hue wheel, swatches and alpha.",
        icon = AppIcons.Drop,
        available = false,
    ),
}
