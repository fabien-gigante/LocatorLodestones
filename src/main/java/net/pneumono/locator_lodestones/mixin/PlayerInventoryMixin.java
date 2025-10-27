package net.pneumono.locator_lodestones.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.pneumono.locator_lodestones.WaypointTracking;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow @Final public PlayerEntity player;

    @Inject(
            method = "markDirty",
            at = @At("RETURN")
    )
    private void updateWaypoints(CallbackInfo ci) {
        if (this.player instanceof ClientPlayerEntity) {
            WaypointTracking.markWaypointsDirty();
        }
    }
}
