package net.pneumono.locator_lodestones.server;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.component.type.MapColorComponent;
import net.minecraft.component.type.MapDecorationsComponent;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapBannerMarker;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.item.map.MapDecorationTypes;
import net.minecraft.item.map.MapState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.world.World;
import net.pneumono.locator_lodestones.IDecorationExt;

public class MapComponentsHelper {
    private static Set<RegistryEntry<MapDecorationType>> BANNER_TYPES = Set.of(
        MapDecorationTypes.BANNER_WHITE, MapDecorationTypes.BANNER_ORANGE, MapDecorationTypes.BANNER_MAGENTA, MapDecorationTypes.BANNER_LIGHT_BLUE,
	    MapDecorationTypes.BANNER_YELLOW, MapDecorationTypes.BANNER_LIME, MapDecorationTypes.BANNER_PINK, MapDecorationTypes.BANNER_GRAY,
        MapDecorationTypes.BANNER_LIGHT_GRAY, MapDecorationTypes.BANNER_CYAN, MapDecorationTypes.BANNER_PURPLE, MapDecorationTypes.BANNER_BLUE,
        MapDecorationTypes.BANNER_BROWN, MapDecorationTypes.BANNER_GREEN, MapDecorationTypes.BANNER_RED, MapDecorationTypes.BANNER_BLACK);

    public static boolean isBanner(RegistryEntry<MapDecorationType> type) { return BANNER_TYPES.contains(type); }

    private static void addBannerComponent(ItemStack stack, World world, BlockPos pos, String id, RegistryEntry<MapDecorationType> decorationType, Optional<Text> name) {
        MapDecorationsComponent.Decoration decoration = new MapDecorationsComponent.Decoration(decorationType, (double)pos.getX(), (double)pos.getZ(), 180.0F);
        if ((Object)decoration instanceof IDecorationExt ext) {
            ext.setName(name);  ext.setY(Optional.of((double)pos.getY()));
            ext.setDimension(Optional.of(world.getRegistryKey()));
        }
        stack.apply(DataComponentTypes.MAP_DECORATIONS, MapDecorationsComponent.DEFAULT, decorations -> decorations.with(id, decoration));
    }

    private static void removeBannerComponents(ItemStack stack, Predicate<String> condition) {
        MapDecorationsComponent component = stack.get(DataComponentTypes.MAP_DECORATIONS);
        if (component == null || component.decorations().isEmpty()) return;
        var map = new HashMap<>(component.decorations());
        if (map.entrySet().removeIf(entry -> isBanner(entry.getValue().type()) && condition.test(entry.getKey())))
            stack.set(DataComponentTypes.MAP_DECORATIONS, new MapDecorationsComponent(map));
    }

    private static boolean needsUpdate(MapState mapState, World world) {
        return mapState != null && mapState.isDirty() & mapState.dimension == world.getRegistryKey();
    }

    public static void updateBannerComponent(World world, ItemStack stack, BlockPos pos) {
        MapState mapState = FilledMapItem.getMapState(stack, world);
        if (!needsUpdate(mapState, world)) return;
        var found = mapState.getBanners().stream()
            .filter(m -> m.pos().getX() == pos.getX() && m.pos().getZ() == pos.getZ() && isBanner(m.getDecorationType()))
            .findAny();
        if (found.isPresent())
            addBannerComponent(stack, world, pos, found.get().getKey(), found.get().getDecorationType(), found.get().name());
        else {
            MapBannerMarker marker = MapBannerMarker.fromWorldBlock(world, pos);
            removeBannerComponents(stack, key -> key.equals(marker.getKey()));
        }
    }

    public static void updateBannerComponents(World world, ItemStack stack) {
        MapState mapState = FilledMapItem.getMapState(stack, world);
        if (!needsUpdate(mapState, world)) return;
        var keys = mapState.getBanners().stream().map(MapBannerMarker::getKey).collect(Collectors.toSet());
        removeBannerComponents(stack, key -> !keys.contains(key));
    }

    private static int getRandomColor(long seed) {
        int color = new LocalRandom(seed).next(24);
        return ColorHelper.withAlpha(255, ColorHelper.withBrightness(color, 0.9f));
    }
    
    public static void setRandomColorComponent(ItemStack stack, LodestoneTrackerComponent newTracker) {
        LodestoneTrackerComponent currentTracker = stack.get(DataComponentTypes.LODESTONE_TRACKER);
        GlobalPos currentTarget = currentTracker == null ? null : currentTracker.target().orElse(null);
        GlobalPos newTarget = newTracker.target().orElse(null);
        MapColorComponent mapColor = (currentTarget != null && currentTarget.equals(newTarget)) ? stack.get(DataComponentTypes.MAP_COLOR) : null;
        int color = getRandomColor(mapColor == null ? newTarget.pos().asLong() : mapColor.rgb());
        stack.set(DataComponentTypes.MAP_COLOR, new MapColorComponent(color));
    }
}