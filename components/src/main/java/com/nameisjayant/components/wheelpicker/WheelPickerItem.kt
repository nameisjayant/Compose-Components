package com.nameisjayant.components.wheelpicker

import androidx.compose.runtime.Immutable

/**
 * One row of [ComposeWheelPicker], handed to its `itemContent` slot.
 *
 * @property index position in the picker's items, 0-based.
 * @property value the item itself.
 * @property isSelected the row sits under the selection line.
 * @property isEnabled the item can be picked (see the picker's `isItemEnabled`) and the picker is enabled.
 */
@Immutable
public class WheelPickerItem<out T>(
    public val index: Int,
    public val value: T,
    public val isSelected: Boolean,
    public val isEnabled: Boolean,
) {

    override fun equals(other: Any?): Boolean =
        this === other || (
            other is WheelPickerItem<*> &&
                index == other.index &&
                value == other.value &&
                isSelected == other.isSelected &&
                isEnabled == other.isEnabled
            )

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + value.hashCode()
        result = 31 * result + isSelected.hashCode()
        result = 31 * result + isEnabled.hashCode()
        return result
    }

    override fun toString(): String = "WheelPickerItem(index=$index, value=$value, isSelected=$isSelected, isEnabled=$isEnabled)"
}
