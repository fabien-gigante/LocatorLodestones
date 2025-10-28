package net.pneumono.locator_lodestones.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.pneumono.locator_lodestones.WaypointTracking;
import net.pneumono.locator_lodestones.config.Config;
import net.pneumono.locator_lodestones.config.ConfigManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Inject(method = "markDirty", at = @At("RETURN"))
    private void updateWaypointsOnDirty(CallbackInfo ci) {
        WaypointTracking.markWaypointsDirty();
    }

    @Inject(method = "dropSelectedItem", at = @At("RETURN"))
    private void updateWaypointsOnItemDrop(boolean entireStack, CallbackInfoReturnable<ItemStack> cir) {
        WaypointTracking.markWaypointsDirty();
    }

    @Inject(method = "setSelectedSlot", at = @At("RETURN"))
    private void updateWaypointsOnSelection(int slot, CallbackInfo ci) {
        if (ConfigManager.getConfig().holdingLocation() == Config.HoldingLocation.HANDS) 
            WaypointTracking.markWaypointsDirty();
    }
}
