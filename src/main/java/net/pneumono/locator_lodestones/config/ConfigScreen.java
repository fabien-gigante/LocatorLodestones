package net.pneumono.locator_lodestones.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.pneumono.locator_lodestones.config.Config.ColorSettings;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ConfigScreen {

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent)
            .setTitle(Text.translatable("locator_lodestones.config.title"));
        ConfigEntryBuilder entries = builder.entryBuilder();
        Config cfg = ConfigManager.getConfig();
        // General
        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("locator_lodestones.config.general"));
        var tabForcesLocatorBar = addToggle(entries,general,"tab_forces_locator_bar",cfg.tabForcesLocatorBar(),Config.DEFAULT.tabForcesLocatorBar());
        var tabShowsNames = addToggle(entries,general,"tab_shows_names",cfg.tabShowsNames(),Config.DEFAULT.tabShowsNames());
        var holdingLocation = addSelector(entries, general, "holding_location", cfg.holdingLocation(),Config.DEFAULT.holdingLocation());
        var dialResolution = addIntField(entries, general, "dial_resolution", cfg.dialResolution(),0,72,Config.DEFAULT.dialResolution());
        var showDistance = addToggle(entries,general,"show_distance",cfg.showDistance(),Config.DEFAULT.showDistance());
        var showRecovery = addToggle(entries,general,"show_recovery",cfg.showRecovery(),Config.DEFAULT.showRecovery());
        var showSpawn = addToggle(entries,general,"show_spawn",cfg.showSpawn(),Config.DEFAULT.showSpawn());
        var showMaps = addToggle(entries,general,"show_maps",cfg.showMaps(),Config.DEFAULT.showMaps());
        var showInSpectator = addToggle(entries,general,"show_in_spectator",cfg.showInSpectator(),Config.DEFAULT.showInSpectator());
        var holdingBundles = addToggle(entries,general,"holding_bundles",cfg.holdingBundles(),Config.DEFAULT.holdingBundles());
        var clockLocation = addSelector(entries, general, "clock_location", cfg.clockLocation(),Config.DEFAULT.clockLocation());
        // Colors
        ConfigCategory colors = builder.getOrCreateCategory(Text.translatable("locator_lodestones.config.colors"));
        var colorCustomization = addToggle(entries,colors,"colors.enable_customization",cfg.colors().colorCustomization(),Config.DEFAULT.colors().colorCustomization());
        var lodestoneColor = addColorOrRandomField(entries,colors,"colors.lodestone",cfg.colors().lodestoneColor(),Config.DEFAULT.colors().lodestoneColor());
        var recoveryColor = addColorField(entries,colors,"colors.recovery",cfg.colors().recoveryColor(),Config.DEFAULT.colors().recoveryColor());
        var spawnColor = addColorField(entries,colors,"colors.spawn",cfg.colors().spawnColor(),Config.DEFAULT.colors().spawnColor());
        var dialColor = addColorField(entries,colors,"colors.dial",cfg.colors().dialColor(),Config.DEFAULT.colors().dialColor());
        // When "Save" is clicked
        builder.setSavingRunnable(() -> {
            ConfigManager.setConfig( new Config(
                    tabForcesLocatorBar.get(), tabShowsNames.get(), holdingLocation.get(), showRecovery.get(),
                    showSpawn.get(), showMaps.get(), holdingBundles.get(), showInSpectator.get(),
                    dialResolution.get(), showDistance.get(), clockLocation.get(), cfg.clockSound(),
                    new ColorSettings(
                            colorCustomization.get(),
                            new ColorProvider(lodestoneColor.get()), new ColorProvider(recoveryColor.get()),
                            new ColorProvider(spawnColor.get()), new ColorProvider(dialColor.get())
                    )
            ));
        });
        return builder.build();
    } 

    private static AtomicBoolean addToggle(ConfigEntryBuilder entries, ConfigCategory category, String key, boolean value, boolean def) {
        AtomicBoolean mutable = new AtomicBoolean(value);
        category.addEntry(entries.startBooleanToggle(Text.translatable("locator_lodestones.config."+key), value)
            .setDefaultValue(def).setSaveConsumer(mutable::set).build());
        return mutable;
    }
    private static <T extends Enum<T>> AtomicReference<T> addSelector(ConfigEntryBuilder entries, ConfigCategory category, String key, T value, T def) {
        AtomicReference<T> mutable = new AtomicReference<T>(value);
        category.addEntry(entries.startEnumSelector(Text.translatable("locator_lodestones.config."+key), value.getDeclaringClass(), value)
            .setDefaultValue(def).setSaveConsumer(mutable::set).build());
        return mutable;
    }
    private static AtomicInteger addIntField(ConfigEntryBuilder entries, ConfigCategory category, String key, int value, int min, int max, int def) {
        AtomicInteger mutable = new AtomicInteger(value);
        category.addEntry(entries.startIntField(Text.translatable("locator_lodestones.config."+key), value)
            .setMin(min).setMax(max).setDefaultValue(def).setSaveConsumer(mutable::set).build());
        return mutable;
    }    
    private static AtomicInteger addColorField(ConfigEntryBuilder entries, ConfigCategory category, String key, ColorProvider value, ColorProvider def) {
        AtomicInteger mutable = new AtomicInteger(value.color());
        category.addEntry(entries.startColorField(Text.translatable("locator_lodestones.config."+key), value.color())
            .setDefaultValue(def.color()).setSaveConsumer(mutable::set).build());
        return mutable;
    }
    private static AtomicInteger addColorOrRandomField(ConfigEntryBuilder entries, ConfigCategory category, String key, ColorProvider value, ColorProvider def) {
        AtomicInteger mutable = new AtomicInteger(value.color());
        category.addEntry(entries.startDropdownMenu(Text.translatable("locator_lodestones.config."+key), value.asString(), v -> v)
                .setSelections(List.of("random",value.asString())).setSuggestionMode(true)
                .setDefaultValue(def.asString()).setTooltip(Text.translatable("locator_lodestones.config.colors.tooltip"))
                .setSaveConsumer(v -> mutable.set(ColorProvider.validate(v).color())).build());
        return mutable;
    }          
}