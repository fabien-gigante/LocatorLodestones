package net.pneumono.locator_lodestones.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.Bar;
import net.minecraft.client.gui.hud.bar.LocatorBar;
import net.minecraft.client.render.RenderTickCounter;
import net.pneumono.locator_lodestones.WaypointNameRendering;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocatorBar.class)
public abstract class LocatorBarMixin implements Bar {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(
            method = "renderAddons",
            at = @At("RETURN")
    )
    private void renderClientWaypoints(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        WaypointNameRendering.renderNames(this.client, context, this.getCenterY(this.client.getWindow()));
    }
}
