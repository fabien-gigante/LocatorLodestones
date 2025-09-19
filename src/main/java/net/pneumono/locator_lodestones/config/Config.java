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
            Codec.BOOL.optionalFieldOf("tab_forces_locator_bar", DEFAULT.tabForcesLocatorBar()).forGetter(Config::tabForcesLocatorBar),
            Codec.BOOL.optionalFieldOf("tab_shows_names", DEFAULT.tabShowsNames()).forGetter(Config::tabShowsNames),
            Codec.BOOL.optionalFieldOf("show_recovery_compasses", DEFAULT.shouldShowRecovery()).forGetter(Config::shouldShowRecovery),
            Codec.BOOL.optionalFieldOf("show_bundled_compasses", DEFAULT.shouldShowBundled()).forGetter(Config::shouldShowBundled),
            Codec.BOOL.optionalFieldOf("color_customization", DEFAULT.colorCustomization()).forGetter(Config::colorCustomization),
            ColorProvider.CODEC.optionalFieldOf("lodestone_color", DEFAULT.getLodestoneColor()).forGetter(Config::getLodestoneColor),
            ColorProvider.CODEC.optionalFieldOf("recovery_color", DEFAULT.getRecoveryColor()).forGetter(Config::getRecoveryColor)
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
