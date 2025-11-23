package net.pneumono.locator_lodestones.server.mixin;

import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.component.type.MapColorComponent;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.random.LocalRandom;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
        LodestoneTrackerComponent tracker = stack.get(DataComponentTypes.LODESTONE_TRACKER);
        GlobalPos pos = tracker != null ? tracker.target().get() : null;
        GlobalPos newPos = ((LodestoneTrackerComponent)value).target().get();
        MapColorComponent mapColor = newPos.equals(pos) ? stack.get(DataComponentTypes.MAP_COLOR) : null;
        int color = new LocalRandom(mapColor == null ? newPos.pos().asLong() : mapColor.rgb()).next(24);
        color = ColorHelper.withAlpha(255, ColorHelper.withBrightness(color, 0.9f));
        stack.set(DataComponentTypes.MAP_COLOR, new MapColorComponent(color));
        return stack.set(type, value);
    }
}