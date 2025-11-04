package net.pneumono.locator_lodestones.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public record Config(
    boolean tabForcesLocatorBar, boolean tabShowsNames, HoldingLocation holdingLocation,
    boolean showRecovery, boolean showSpawn, boolean showMaps, boolean holdingBundles, boolean showInSpectator,
    boolean showCompassDial, boolean showDistance, HoldingLocation clockLocation, SoundEvent clockSound,
    ColorSettings colors) {

    public static final Config DEFAULT = new Config(
        true, true, HoldingLocation.INVENTORY,
        true, false, false, true, false,
        false, true, HoldingLocation.NONE, SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(),
        new ColorSettings(true,
            new ColorProvider(null), new ColorProvider(0xBCE0EB),
            new ColorProvider(0x6BCF6D), new ColorProvider(0x879E7B))
    );

    public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("tab_forces_locator_bar").forGetter(Config::tabForcesLocatorBar),
            Codec.BOOL.fieldOf("tab_shows_names").forGetter(Config::tabShowsNames),
            HoldingLocation.CODEC.fieldOf("holding_location").forGetter(Config::holdingLocation),
            Codec.BOOL.fieldOf("show_recovery_compasses").forGetter(Config::showRecovery),
            Codec.BOOL.fieldOf("show_spawn").forGetter(Config::showSpawn),
            Codec.BOOL.fieldOf("show_maps").forGetter(Config::showMaps),
            Codec.BOOL.fieldOf("holding_bundles").forGetter(Config::holdingBundles),
            Codec.BOOL.fieldOf("show_in_spectator").forGetter(Config::showInSpectator),
            Codec.BOOL.fieldOf("show_compass_dial").forGetter(Config::showCompassDial),
            Codec.BOOL.fieldOf("show_distance").forGetter(Config::showDistance),
            HoldingLocation.CODEC.fieldOf("clock_location").forGetter(Config::clockLocation),
            SoundEvent.CODEC.fieldOf("clock_sound").forGetter(Config::clockSound),
            ColorSettings.CODEC.fieldOf("colors").forGetter(Config::colors)
    ).apply(instance, Config::new));

    public enum HoldingLocation {
        NONE, INVENTORY, HOTBAR, HANDS;

        public static final Codec<HoldingLocation> CODEC = Codec.STRING.xmap(
            str -> HoldingLocation.valueOf(str.toUpperCase()),
            loc -> loc.name().toLowerCase()
        );
    }

    public record ColorSettings(
        boolean colorCustomization,
        ColorProvider lodestoneColor, ColorProvider recoveryColor, 
        ColorProvider spawnColor, ColorProvider dialColor) {

        public static final Codec<ColorSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("color_customization").forGetter(ColorSettings::colorCustomization),
            ColorProvider.CODEC.fieldOf("lodestone_color").forGetter(ColorSettings::lodestoneColor),
            ColorProvider.CODEC.fieldOf("recovery_color").forGetter(ColorSettings::recoveryColor),
            ColorProvider.CODEC.fieldOf("spawn_color").forGetter(ColorSettings::spawnColor),
            ColorProvider.CODEC.fieldOf("dial_color").forGetter(ColorSettings::dialColor)
        ).apply(instance, ColorSettings::new));
    }
}
