package net.pneumono.locator_lodestones.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.pneumono.locator_lodestones.WaypointTracking;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow @Final public PlayerEntity player;

    private void updateWaypoints() {
        if (this.player instanceof ClientPlayerEntity clientPlayer) {
            WaypointTracking.updateWaypoints(clientPlayer, true);
        }
    }

    @Inject(
            method = "markDirty",
            at = @At("RETURN")
    )
    private void onMarkDirty(CallbackInfo ci) {
        updateWaypoints();
    }

    @Inject(
            method = "dropSelectedItem",
            at = @At("RETURN")
    )
    private void onDropSelectedItem(boolean entireStack, CallbackInfoReturnable<ItemStack> ci) {
        updateWaypoints();
    }

}
