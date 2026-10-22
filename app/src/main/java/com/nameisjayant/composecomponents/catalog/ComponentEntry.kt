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
        description = "Snapping wheel for numbers, times and lists, curved or flat, looping and multi-wheel.",
        icon = AppIcons.Wheel,
        available = true,
    ),
    OtpInput(
        title = "OTP Input",
        description = "Segmented code field with paste, SMS autofill, masking, groups and error shake.",
        icon = AppIcons.Pin,
        available = true,
    ),
    RatingBar(
        title = "Rating Bar",
        description = "Tap or drag to rate, with half and free steps, custom shapes and smooth animations.",
        icon = AppIcons.Star,
        available = true,
    ),
    Timeline(
        title = "Timeline",
        description = "Steps joined by a line that fills as you progress: vertical, horizontal or alternating, lazy too.",
        icon = AppIcons.Timeline,
        available = true,
    ),
    ColorPicker(
        title = "Color Picker",
        description = "Hue wheel, swatches and alpha.",
        icon = AppIcons.Drop,
        available = false,
    ),
}
