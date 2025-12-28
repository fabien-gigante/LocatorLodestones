package net.pneumono.locator_lodestones.server;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public interface ISlotListener {
	public default boolean isValidInput(Slot slot, ItemStack stack) { return false; }
}