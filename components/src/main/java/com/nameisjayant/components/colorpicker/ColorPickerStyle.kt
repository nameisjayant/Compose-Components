package com.nameisjayant.components.colorpicker

/** How [ComposeColorPicker] lays out its main color area. */
public enum class ColorPickerStyle {
    /** A hue and saturation disc ([HueSaturationWheel]) with a brightness slider under it. */
    Wheel,

    /** A hue ring with a saturation and brightness square inside it ([HueRingPanel]). */
    Ring,

    /** A saturation and brightness panel ([SaturationValuePanel]) with a hue slider under it. */
    Square,
}
