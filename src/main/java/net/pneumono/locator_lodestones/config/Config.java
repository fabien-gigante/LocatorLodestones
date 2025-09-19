package net.pneumono.locator_lodestones.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class Config {
    public static final Config DEFAULT = new Config(
            true, true, true, true, true,
            new ColorProvider(null), new ColorProvider(0xBCE0EB)
    );

    public boolean tabForcesLocatorBar;
    public boolean tabShowsNames;
    public boolean showRecovery;
    public boolean showBundled;
    public boolean colorCustomization;
    public ColorProvider lodestoneColor;
    public ColorProvider recoveryColor;

    public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("tab_forces_locator_bar").forGetter(Config::tabForcesLocatorBar),
            Codec.BOOL.fieldOf("tab_shows_names").forGetter(Config::tabShowsNames),
            Codec.BOOL.fieldOf("show_recovery_compasses").forGetter(Config::shouldShowRecovery),
            Codec.BOOL.fieldOf("show_bundled_compasses").forGetter(Config::shouldShowBundled),
            Codec.BOOL.fieldOf("color_customization").forGetter(Config::colorCustomization),
            ColorProvider.CODEC.fieldOf("lodestone_color").forGetter(Config::getLodestoneColor),
            ColorProvider.CODEC.fieldOf("recovery_color").forGetter(Config::getRecoveryColor)
    ).apply(instance, Config::new));

    public Config(
            boolean tabForcesLocatorBar, boolean tabShowsNames, boolean showRecovery, boolean showBundled,
            boolean colorCustomization, ColorProvider lodestoneColor, ColorProvider recoveryColor
    ) {
        this.tabForcesLocatorBar = tabForcesLocatorBar;
        this.tabShowsNames = tabShowsNames;
        this.showRecovery = showRecovery;
        this.showBundled = showBundled;
        this.colorCustomization = colorCustomization;
        this.lodestoneColor = lodestoneColor;
        this.recoveryColor = recoveryColor;
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

    public boolean colorCustomization() {
        return colorCustomization;
    }

    public ColorProvider getLodestoneColor() {
        return lodestoneColor;
    }

    public ColorProvider getRecoveryColor() {
        return recoveryColor;
    }
}
