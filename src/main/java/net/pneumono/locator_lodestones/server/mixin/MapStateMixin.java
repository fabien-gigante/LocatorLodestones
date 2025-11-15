package net.pneumono.locator_lodestones.server.mixin;

import net.pneumono.locator_lodestones.server.MapDecorationsHelper;

import net.minecraft.item.map.MapDecorationType;
import net.minecraft.item.map.MapState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapState.class)
public abstract class MapStateMixin {
    @Shadow @Final
    public RegistryKey<World> dimension;
    
    // MC-142687 : on Nether maps, banners appear randomly rotated, let's fix that
    @Inject(method = "getMarker", at = @At("RETURN"), cancellable = true)
    private void fixBannerRotation(RegistryEntry<MapDecorationType> type, @Nullable WorldAccess world, double rotation, float dx, float dz, CallbackInfoReturnable<MapState.Marker> cir) {
        MapState.Marker marker = cir.getReturnValue();
        if (marker != null && this.dimension == World.NETHER && MapDecorationsHelper.isBanner(type))
            cir.setReturnValue(new MapState.Marker(type, marker.x(), marker.y(), (byte)8));
    }

    // MC-142686 : banners can be added / removed on maps from other dimensions, let's fix that
    @Inject(method = "addBanner", at = @At("HEAD"), cancellable = true)
    public void checkBannerDimension(WorldAccess worldAccess, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (worldAccess instanceof World world && world.getRegistryKey() != this.dimension) {
            cir.setReturnValue(false); cir.cancel();
        }
    }
}