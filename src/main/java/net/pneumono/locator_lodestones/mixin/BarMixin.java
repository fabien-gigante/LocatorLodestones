package net.pneumono.locator_lodestones.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.Bar;
import net.pneumono.locator_lodestones.WaypointRendering;
import net.minecraft.client.font.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Bar.class)
public interface BarMixin {
    @Inject(method = "drawExperienceLevel", at = @At("HEAD"), cancellable = true)
    private static void onDrawExperienceLevel(DrawContext context, TextRenderer textRenderer, int level, CallbackInfo ci) {
        if (!WaypointRendering.shouldDrawExperienceLevel()) ci.cancel();
    }
}
