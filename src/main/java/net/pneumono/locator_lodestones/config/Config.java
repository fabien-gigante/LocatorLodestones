package net.pneumono.locator_lodestones.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Config(
        boolean tabForcesLocatorBar, boolean tabShowsNames, boolean showRecovery, boolean showSpawn, boolean showMaps, boolean showBundled,
        boolean showInSpectator, HoldingLocation holdingLocation, boolean showCompassDial, boolean showDistance, boolean colorCustomization,
        ColorProvider lodestoneColor, ColorProvider recoveryColor, ColorProvider spawnColor, ColorProvider dialColor) {

    public static final Config DEFAULT = new Config(
            true, true, true, false, false, true,
            true, HoldingLocation.INVENTORY, false, false, true,
            new ColorProvider(null), new ColorProvider(0xBCE0EB), new ColorProvider(0x6BCF6D), new ColorProvider(0x879E7B)
    );

    public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("tab_forces_locator_bar").forGetter(Config::tabForcesLocatorBar),
            Codec.BOOL.fieldOf("tab_shows_names").forGetter(Config::tabShowsNames),
            Codec.BOOL.fieldOf("show_recovery_compasses").forGetter(Config::showRecovery),
            Codec.BOOL.fieldOf("show_spawn").forGetter(Config::showSpawn),
            Codec.BOOL.fieldOf("show_maps").forGetter(Config::showMaps),
            Codec.BOOL.fieldOf("show_bundled_compasses").forGetter(Config::showBundled),
            Codec.BOOL.fieldOf("show_in_spectator").forGetter(Config::showInSpectator),
            HoldingLocation.CODEC.fieldOf("holding_location").forGetter(Config::holdingLocation),
            Codec.BOOL.fieldOf("show_compass_dial").forGetter(Config::showCompassDial),
            Codec.BOOL.fieldOf("show_distance").forGetter(Config::showDistance),
            Codec.BOOL.fieldOf("color_customization").forGetter(Config::colorCustomization),
            ColorProvider.CODEC.fieldOf("lodestone_color").forGetter(Config::lodestoneColor),
            ColorProvider.CODEC.fieldOf("recovery_color").forGetter(Config::recoveryColor),
            ColorProvider.CODEC.fieldOf("spawn_color").forGetter(Config::spawnColor),
            ColorProvider.CODEC.fieldOf("dial_color").forGetter(Config::dialColor)
    ).apply(instance, Config::new));

    public enum HoldingLocation {
        INVENTORY, HOTBAR, HANDS;
        public static final Codec<HoldingLocation> CODEC = Codec.STRING.xmap(
                str -> HoldingLocation.valueOf(str.toUpperCase()),
                loc -> loc.name().toLowerCase()
        );
    }
}
