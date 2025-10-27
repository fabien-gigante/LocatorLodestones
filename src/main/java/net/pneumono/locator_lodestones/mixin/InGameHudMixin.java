package net.pneumono.locator_lodestones.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.bar.Bar;
import net.minecraft.client.font.TextRenderer;
import net.pneumono.locator_lodestones.WaypointRendering;
import net.pneumono.locator_lodestones.config.ConfigManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(
            method = "getCurrentBarType",
            at = @At("HEAD"),
            cancellable = true
    )
    private void forceLocatorBarWhenPlayerListOpen(CallbackInfoReturnable<InGameHud.BarType> info) {
        boolean canShow = ConfigManager.getConfig().tabForcesLocatorBar()
                && (ConfigManager.getConfig().showInSpectator() || (client.player != null && !client.player.isSpectator()));
        if (canShow && client.options.playerListKey.isPressed()) {
            info.setReturnValue(InGameHud.BarType.LOCATOR);
        }
    }

    @Redirect(
        method = "renderMainHud(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/bar/Bar;drawExperienceLevel(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;I)V"
        )
    )
    private void conditionalDrawExperienceLevel(DrawContext context, TextRenderer textRenderer, int level) {
        if (WaypointRendering.shouldDrawExperienceLevel())
            Bar.drawExperienceLevel(context, textRenderer, level);
    }    
}
