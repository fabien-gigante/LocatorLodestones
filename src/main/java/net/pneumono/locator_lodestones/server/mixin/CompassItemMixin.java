package net.pneumono.locator_lodestones.server.mixin;

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
        if (value instanceof LodestoneTrackerComponent tracker) {
            GlobalPos pos = tracker.target().get();
            MapColorComponent mapColor = stack.get(DataComponentTypes.MAP_COLOR);
            String key = "lodestone_" + pos; if (mapColor !=null) key += "_" + mapColor.rgb();
            int color = ColorHelper.withBrightness(ColorHelper.withAlpha(255, key.hashCode()), 0.9F);
            stack.set(DataComponentTypes.MAP_COLOR, new MapColorComponent(color));
        }
        return stack.set(type, value);   
    }
}