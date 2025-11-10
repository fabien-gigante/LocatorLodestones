package net.pneumono.locator_lodestones.server.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.pneumono.locator_lodestones.server.MapDecorationsHelper;
import net.minecraft.util.ActionResult;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FilledMapItem.class)
public class FilledMapItemMixin {
    @Inject(
        method = "useOnBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/map/MapState;addBanner(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;)Z",
            shift = At.Shift.AFTER
        )
    )
    private void updateBannerComponent(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        MapDecorationsHelper.updateBannerComponent(context.getWorld(), context.getStack(), context.getBlockPos());
    }

    @Inject(method = "inventoryTick",at = @At("RETURN"))
    private void updateBannerComponents(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot, CallbackInfo ci) {
        MapDecorationsHelper.updateBannerComponents(world, stack);
    }
}
