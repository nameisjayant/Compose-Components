package com.nameisjayant.components.datepicker

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

// Hand-drawn so the library doesn't need material-icons. Paths match the Material Symbols glyphs.
internal object DatePickerIcons {

    val ChevronLeft: ImageVector by lazy {
        icon("ChevronLeft") {
            moveTo(15.41f, 7.41f)
            lineTo(14f, 6f)
            lineToRelative(-6f, 6f)
            lineToRelative(6f, 6f)
            lineToRelative(1.41f, -1.41f)
            lineTo(10.83f, 12f)
            close()
        }
    }

    val ChevronRight: ImageVector by lazy {
        icon("ChevronRight") {
            moveTo(10f, 6f)
            lineTo(8.59f, 7.41f)
            lineTo(13.17f, 12f)
            lineToRelative(-4.58f, 4.59f)
            lineTo(10f, 18f)
            lineToRelative(6f, -6f)
            close()
        }
    }

    val DropDown: ImageVector by lazy {
        icon("DropDown", autoMirror = false) {
            moveTo(7f, 10f)
            lineToRelative(5f, 5f)
            lineToRelative(5f, -5f)
            close()
        }
    }

    private fun icon(
        name: String,
        autoMirror: Boolean = true,
        path: androidx.compose.ui.graphics.vector.PathBuilder.() -> Unit,
    ): ImageVector = ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
        autoMirror = autoMirror,
    ).path(fill = SolidColor(Color.Black), pathBuilder = path).build()
}
