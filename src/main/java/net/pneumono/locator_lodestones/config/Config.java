package net.pneumono.locator_lodestones.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class Config {
    public static final Config DEFAULT = new Config(
            true, true,
            true, true, true, false, false,
            true, new ColorProvider(null), new ColorProvider(0xBCE0EB), new ColorProvider(0x879E7B)
    );

    public boolean tabForcesLocatorBar;
    public boolean tabShowsNames;
    public boolean showRecovery;
    public boolean showBundled;
    public boolean showInSpectator;
    public boolean showHotbarOnly;
    public boolean showCompassDial;
    public boolean colorCustomization;
    public ColorProvider lodestoneColor;
    public ColorProvider recoveryColor;
    public ColorProvider dialColor;

    public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("tab_forces_locator_bar").forGetter(Config::tabForcesLocatorBar),
            Codec.BOOL.fieldOf("tab_shows_names").forGetter(Config::tabShowsNames),
            Codec.BOOL.fieldOf("show_recovery_compasses").forGetter(Config::shouldShowRecovery),
            Codec.BOOL.fieldOf("show_bundled_compasses").forGetter(Config::shouldShowBundled),
            Codec.BOOL.fieldOf("show_in_spectator").forGetter(Config::shouldShowInSpectator),
            Codec.BOOL.fieldOf("show_hotbar_only").forGetter(Config::shouldShowHotbarOnly),
            Codec.BOOL.fieldOf("show_compass_dial").forGetter(Config::shouldShowCompassDial),
            Codec.BOOL.fieldOf("color_customization").forGetter(Config::colorCustomization),
            ColorProvider.CODEC.fieldOf("lodestone_color").forGetter(Config::getLodestoneColor),
            ColorProvider.CODEC.fieldOf("recovery_color").forGetter(Config::getRecoveryColor),
            ColorProvider.CODEC.fieldOf("dial_color").forGetter(Config::getRecoveryColor)
    ).apply(instance, Config::new));

    public Config(
            boolean tabForcesLocatorBar, boolean tabShowsNames, boolean showRecovery, boolean showBundled,
            boolean showInSpectator, boolean showHotbarOnly, boolean showCompassDial, boolean colorCustomization,
            ColorProvider lodestoneColor, ColorProvider recoveryColor, ColorProvider dialColor
    ) {
        this.tabForcesLocatorBar = tabForcesLocatorBar;
        this.tabShowsNames = tabShowsNames;
        this.showRecovery = showRecovery;
        this.showBundled = showBundled;
        this.showInSpectator = showInSpectator;
        this.showHotbarOnly = showHotbarOnly;
        this.showCompassDial = showCompassDial;
        this.colorCustomization = colorCustomization;
        this.lodestoneColor = lodestoneColor;
        this.recoveryColor = recoveryColor;
        this.dialColor = dialColor;
    }

    public boolean tabForcesLocatorBar() {
        return tabForcesLocatorBar;
    }

    public boolean tabShowsNames() {
        return tabShowsNames;
    }

    public boolean shouldShowRecovery() {
        return showRecovery;
    }

    public boolean shouldShowBundled() {
        return showBundled;
    }

    public boolean shouldShowInSpectator() {
        return showInSpectator;
    }

    public boolean shouldShowHotbarOnly() {
        return showHotbarOnly;
    }

    public boolean shouldShowCompassDial() {
        return showCompassDial;
    }

    public boolean colorCustomization() {
        return colorCustomization;
    }

    public ColorProvider getLodestoneColor() {
        return lodestoneColor;
    }

    public ColorProvider getRecoveryColor() {
        return recoveryColor;
    }

    public ColorProvider getDialColor() {
        return dialColor;
    }
}
