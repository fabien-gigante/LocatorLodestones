package net.pneumono.locator_lodestones.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity {
    private ClientPlayerEntityMixin(World world, GameProfile profile) { super(world, profile); }

    @Inject(method = "setExperience", at = @At("HEAD"), cancellable = true)
    public void setExperience(float progress, int total, int level, CallbackInfo ci) {
        if (progress==experienceProgress && total==totalExperience && level==experienceLevel)
            ci.cancel();
    }
}