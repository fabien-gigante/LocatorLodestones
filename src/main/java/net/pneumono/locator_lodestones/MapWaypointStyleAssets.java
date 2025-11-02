package net.pneumono.locator_lodestones;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.MapColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.waypoint.WaypointStyleAsset;
import net.minecraft.client.resource.waypoint.WaypointStyleAssetManager;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.item.map.MapDecorationTypes;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.waypoint.WaypointStyle;

public class MapWaypointStyleAssets {
    private static Map<RegistryEntry<MapDecorationType>, RegistryKey<WaypointStyle>> STYLES
        = new HashMap<RegistryEntry<MapDecorationType>, RegistryKey<WaypointStyle>>();

    private static final Map<RegistryEntry<MapDecorationType>, Integer> COLORS = Map.ofEntries(
        Map.entry(MapDecorationTypes.RED_X,             MapColor.BRIGHT_RED.color),
        Map.entry(MapDecorationTypes.RED_MARKER,        MapColor.BRIGHT_RED.color),
        Map.entry(MapDecorationTypes.BLUE_MARKER,       MapColor.BLUE.color),
        Map.entry(MapDecorationTypes.TARGET_POINT,      MapColor.BRIGHT_RED.color),
        Map.entry(MapDecorationTypes.TARGET_X,          MapColor.WHITE.color),
        Map.entry(MapDecorationTypes.FRAME,             MapColor.GREEN.color),
        Map.entry(MapDecorationTypes.BANNER_WHITE,      MapColor.WHITE.color), 
        Map.entry(MapDecorationTypes.BANNER_ORANGE,     MapColor.ORANGE.color), 
        Map.entry(MapDecorationTypes.BANNER_MAGENTA,    MapColor.MAGENTA.color),
        Map.entry(MapDecorationTypes.BANNER_LIGHT_BLUE, MapColor.LIGHT_BLUE.color),
        Map.entry(MapDecorationTypes.BANNER_YELLOW,     MapColor.YELLOW.color),
        Map.entry(MapDecorationTypes.BANNER_LIME,       MapColor.LIME.color),
        Map.entry(MapDecorationTypes.BANNER_PINK,       MapColor.PINK.color),
        Map.entry(MapDecorationTypes.BANNER_GRAY,       MapColor.GRAY.color),
        Map.entry(MapDecorationTypes.BANNER_LIGHT_GRAY, MapColor.LIGHT_GRAY.color),
        Map.entry(MapDecorationTypes.BANNER_CYAN,       MapColor.CYAN.color),
        Map.entry(MapDecorationTypes.BANNER_PURPLE,     MapColor.PURPLE.color),
        Map.entry(MapDecorationTypes.BANNER_BLUE,       MapColor.BLUE.color),
        Map.entry(MapDecorationTypes.BANNER_BROWN,      MapColor.BROWN.color),
        Map.entry(MapDecorationTypes.BANNER_GREEN,      MapColor.GREEN.color),
        Map.entry(MapDecorationTypes.BANNER_RED,        MapColor.RED.color),
        Map.entry(MapDecorationTypes.BANNER_BLACK,      MapColor.BLACK.color));

    public static void reload() {
        MinecraftClient client = MinecraftClient.getInstance();
        WaypointStyleAssetManager assetManager = client.getWaypointStyleAssetManager();
        assetManager.registry = new HashMap<>(assetManager.registry); // Make it mutable
        STYLES.clear();
        Registries.MAP_DECORATION_TYPE.streamEntries().forEach(type -> {
            Identifier id = LocatorLodestones.id(type.value().assetId().getPath());
            RegistryKey<WaypointStyle> style = LocatorLodestones.style("map_" + id.getPath());
            STYLES.put(type, style);
            WaypointStyleAsset asset = new WaypointStyleAsset(WaypointStyleAsset.DEFAULT_NEAR_DISTANCE, WaypointStyleAsset.DEFAULT_FAR_DISTANCE,
                List.of(id.withSuffixedPath("_0"), id.withSuffixedPath("_1")),
                List.of(id.withPrefixedPath("hud/locator_bar_dot/map/"), id.withPrefixedPath("hud/locator_bar_dot/map/small/")));
            assetManager.registry.put(style, asset);
        });
    }

    public static RegistryKey<WaypointStyle> getStyle(RegistryEntry<MapDecorationType> type) {
        return STYLES.get(type);
    }
    public static int getColor(RegistryEntry<MapDecorationType> type) {
        int color = type.value().mapColor();
        if (color == -1) color = COLORS.getOrDefault(type, -1);
        return ColorHelper.fullAlpha(color);
    }
}
