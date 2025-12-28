package net.pneumono.locator_lodestones.server.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

import net.pneumono.locator_lodestones.server.ISlotListener;

@Mixin(GrindstoneScreenHandler.class)
public abstract class GrindstoneScreenHandlerMixin extends ScreenHandler implements ISlotListener {
	@Shadow @Final Inventory input;
	@Shadow @Final ScreenHandlerContext context;    

	protected GrindstoneScreenHandlerMixin(ScreenHandlerType<?> type, int syncId) { super(type, syncId); }

	@ModifyReturnValue(method = "getOutputStack", at = @At("RETURN"))
	private ItemStack modifyOutputStack(ItemStack original, ItemStack firstInput, ItemStack secondInput) {
		if (original != ItemStack.EMPTY || !isValidLodestoneTrackerRecipe(firstInput, secondInput)) return original;
		ItemStack result = firstInput.copy();
		result.remove(DataComponentTypes.LODESTONE_TRACKER);
		result.remove(DataComponentTypes.MAP_COLOR);
		return result;
	}

	public boolean isValidInput(Slot slot, ItemStack stack) {
		return slot == this.getSlot(0) && hasLodestoneTracker(stack);
	}

	@Unique
	private boolean hasLodestoneTracker(ItemStack stack) {
        return stack.get(DataComponentTypes.LODESTONE_TRACKER) != null;
	}

	@Unique
	private boolean isValidLodestoneTrackerRecipe(ItemStack firstInput, ItemStack secondInput) {
		return (secondInput == null || secondInput.isEmpty()) && hasLodestoneTracker(firstInput);
	}
}