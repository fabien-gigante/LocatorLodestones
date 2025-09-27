package net.pneumono.locator_lodestones.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.pneumono.locator_lodestones.config.ConfigManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
        if (ConfigManager.tabForcesLocatorBar() && client.options.playerListKey.isPressed()) {
            info.setReturnValue(InGameHud.BarType.LOCATOR);
        }
    }
}
