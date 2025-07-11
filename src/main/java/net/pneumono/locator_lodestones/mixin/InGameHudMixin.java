package net.pneumono.locator_lodestones.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.world.ClientWaypointHandler;
import net.pneumono.locator_lodestones.LocatorLodestones;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow @Final private MinecraftClient client;

    @WrapOperation(
            method = "getCurrentBarType",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/world/ClientWaypointHandler;hasWaypoint()Z"
            )
    )
    private boolean getCurrentBarType(ClientWaypointHandler instance, Operation<Boolean> original) {
        if (client.player != null && !LocatorLodestones.getLodestonePositions(client.player).isEmpty()) {
            return true;
        } else {
            return original.call(instance);
        }
    }
}
