package net.pneumono.locator_lodestones.server.mixin;

import net.pneumono.locator_lodestones.server.ColorChromaHelper;

import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.component.type.MapColorComponent;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.GlobalPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.jetbrains.annotations.Nullable;

@Mixin(CompassItem.class)
public abstract class CompassItemMixin {
    @Redirect(
        method = "useOnBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;set(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Ljava/lang/Object;"
        )
    )
    private <T> T setLodestoneComponents(ItemStack stack, ComponentType<T> type, T value) {
        assert(value instanceof LodestoneTrackerComponent);
        GlobalPos pos = ((LodestoneTrackerComponent)value).target().get();
        MapColorComponent mapColor = pos.equals(getLodestoneTarget(stack)) ? stack.get(DataComponentTypes.MAP_COLOR) : null;
        int color = mapColor == null ? ColorChromaHelper.colorFromPos(pos.pos())
                                     : ColorChromaHelper.cycleHueSaturation(mapColor.rgb());
        stack.set(DataComponentTypes.MAP_COLOR, new MapColorComponent(ColorHelper.withAlpha(255,color)));
        return stack.set(type, value);
    }
    private static @Nullable GlobalPos getLodestoneTarget(ItemStack stack) {
        LodestoneTrackerComponent tracker = stack.get(DataComponentTypes.LODESTONE_TRACKER);
        return tracker != null ? tracker.target().get() : null;
    }
}