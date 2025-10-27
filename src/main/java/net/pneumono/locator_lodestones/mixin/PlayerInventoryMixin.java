package net.pneumono.locator_lodestones.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.pneumono.locator_lodestones.WaypointTracking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Inject(
            method = "markDirty",
            at = @At("RETURN")
    )
    private void updateWaypoints(CallbackInfo ci) {
        WaypointTracking.markWaypointsDirty();
    }

    @Inject(
            method = "dropSelectedItem",
            at = @At("RETURN")
    )
    private void updateWaypointsOnItemDrop(boolean entireStack, CallbackInfoReturnable<ItemStack> cir) {
        WaypointTracking.markWaypointsDirty();
    }
}
