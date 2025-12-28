package net.pneumono.locator_lodestones.server.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.slot.Slot;

import net.pneumono.locator_lodestones.server.ISlotListener;

@Mixin(targets = "net/minecraft/screen/GrindstoneScreenHandler$2")
public class GrindstoneScreenHandlerTopInputSlotMixin extends Slot {
	@Unique @Final private GrindstoneScreenHandler grindstoneHandler;

	public GrindstoneScreenHandlerTopInputSlotMixin(Inventory inventory, int index, int x, int y) { super(inventory, index, x, y); }

	// Cache grindstone parent
	@Inject(method = "<init>", at = @At("TAIL"))
	private void init(GrindstoneScreenHandler grindstoneScreenHandler, Inventory inventory, int i, int j, int k, CallbackInfo ci) {
		this.grindstoneHandler = grindstoneScreenHandler;
	}

	// Grindstone parent can allow additional items as input 
	@ModifyReturnValue(method = "canInsert", at = @At("RETURN"))
	private boolean modifyCanInsert(boolean original, ItemStack stack) {
		return original || ((ISlotListener) grindstoneHandler).isValidInput(this, stack);
	}
}