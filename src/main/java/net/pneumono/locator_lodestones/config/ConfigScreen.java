package net.pneumono.locator_lodestones.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.pneumono.locator_lodestones.config.Config.ColorSettings;
import net.pneumono.locator_lodestones.config.Config.HoldingLocation;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ConfigScreen {
    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Text.translatable("locator_lodestones.config.title"));
        ConfigEntryBuilder entries = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("locator_lodestones.config.general"));
        ConfigCategory colors = builder.getOrCreateCategory(Text.translatable("locator_lodestones.config.colors"));

        Config cfg = ConfigManager.getConfig();

        // Store mutable holders for new values
        AtomicBoolean tabForcesLocatorBar = new AtomicBoolean(cfg.tabForcesLocatorBar());
        AtomicBoolean tabShowsNames = new AtomicBoolean(cfg.tabShowsNames());
        AtomicReference<HoldingLocation> holdingLocation = new AtomicReference<>(cfg.holdingLocation());
        AtomicInteger dialResolution = new AtomicInteger(cfg.dialResolution());
        AtomicBoolean showDistance = new AtomicBoolean(cfg.showDistance());
        AtomicBoolean showRecovery = new AtomicBoolean(cfg.showRecovery());
        AtomicBoolean showSpawn = new AtomicBoolean(cfg.showSpawn());
        AtomicBoolean showMaps = new AtomicBoolean(cfg.showMaps());
        AtomicBoolean showInSpectator = new AtomicBoolean(cfg.showInSpectator());
        AtomicBoolean holdingBundles = new AtomicBoolean(cfg.holdingBundles());
        AtomicReference<HoldingLocation> clockLocation = new AtomicReference<>(cfg.clockLocation());
        
        AtomicBoolean colorCustomization = new AtomicBoolean(cfg.colors().colorCustomization());
        AtomicInteger lodestoneColor = new AtomicInteger(cfg.colors().lodestoneColor().color());
        AtomicInteger recoveryColor = new AtomicInteger(cfg.colors().recoveryColor().color());
        AtomicInteger spawnColor = new AtomicInteger(cfg.colors().spawnColor().color());
        AtomicInteger dialColor = new AtomicInteger(cfg.colors().dialColor().color());

        // === General ===
        general.addEntry(entries.startBooleanToggle(Text.translatable("locator_lodestones.config.tab_forces_locator_bar"), cfg.tabForcesLocatorBar())
            .setDefaultValue(Config.DEFAULT.tabForcesLocatorBar()).setSaveConsumer(tabForcesLocatorBar::set).build());
        general.addEntry(entries.startBooleanToggle(Text.translatable("locator_lodestones.config.tab_shows_names"), cfg.tabShowsNames())
            .setSaveConsumer(tabShowsNames::set).build());
        general.addEntry(entries.startEnumSelector(Text.translatable("locator_lodestones.config.holding_location"), HoldingLocation.class, cfg.holdingLocation())
            .setDefaultValue(Config.DEFAULT.holdingLocation()).setSaveConsumer(holdingLocation::set).build());
        general.addEntry(entries.startIntField(Text.translatable("locator_lodestones.config.dial_resolution"), cfg.dialResolution())
            .setMin(0).setMax(72).setDefaultValue(Config.DEFAULT.dialResolution()).setSaveConsumer(dialResolution::set).build());
        general.addEntry(entries.startBooleanToggle(Text.translatable("locator_lodestones.config.show_distance"), cfg.showDistance())
            .setDefaultValue(Config.DEFAULT.showDistance()).setSaveConsumer(showDistance::set).build());
        general.addEntry(entries.startBooleanToggle(Text.translatable("locator_lodestones.config.show_recovery"), cfg.showRecovery())
            .setDefaultValue(Config.DEFAULT.showRecovery()).setSaveConsumer(showRecovery::set).build());
        general.addEntry(entries.startBooleanToggle(Text.translatable("locator_lodestones.config.show_spawn"), cfg.showSpawn())
            .setDefaultValue(Config.DEFAULT.showSpawn()).setSaveConsumer(showSpawn::set).build());
        general.addEntry(entries.startBooleanToggle(Text.translatable("locator_lodestones.config.show_maps"), cfg.showMaps())
            .setDefaultValue(Config.DEFAULT.showMaps()).setSaveConsumer(showMaps::set).build());
        general.addEntry(entries.startBooleanToggle(Text.translatable("locator_lodestones.config.show_in_spectator"), cfg.showInSpectator())
            .setDefaultValue(Config.DEFAULT.showInSpectator()).setSaveConsumer(showInSpectator::set).build());
        general.addEntry(entries.startBooleanToggle(Text.translatable("locator_lodestones.config.holding_bundles"), cfg.holdingBundles())
            .setDefaultValue(Config.DEFAULT.holdingBundles()).setSaveConsumer(holdingBundles::set).build());
        general.addEntry(entries.startEnumSelector(Text.translatable("locator_lodestones.config.clock_location"), HoldingLocation.class, cfg.clockLocation())
            .setDefaultValue(Config.DEFAULT.clockLocation()).setSaveConsumer(clockLocation::set).build());      

        // === Colors ===
        colors.addEntry(entries.startBooleanToggle(Text.translatable("locator_lodestones.config.colors.enable_customization"), cfg.colors().colorCustomization())
                .setDefaultValue(Config.DEFAULT.colors().colorCustomization()).setSaveConsumer(colorCustomization::set).build());
        colors.addEntry(entries.startDropdownMenu(Text.translatable("locator_lodestones.config.colors.lodestone"), cfg.colors().lodestoneColor().asString(), v -> v)
                .setSelections(List.of("random", cfg.colors().lodestoneColor().asString())).setSuggestionMode(true)
                .setDefaultValue(Config.DEFAULT.colors().lodestoneColor().asString()).setTooltip(Text.translatable("locator_lodestones.config.colors.tooltip"))
                .setSaveConsumer(value -> lodestoneColor.set(ColorProvider.validate(value).color())).build());
        colors.addEntry(entries.startColorField(Text.translatable("locator_lodestones.config.colors.recovery"), cfg.colors().recoveryColor().color())
                .setDefaultValue(Config.DEFAULT.colors().recoveryColor().color()).setSaveConsumer(recoveryColor::set).build());
        colors.addEntry(entries.startColorField(Text.translatable("locator_lodestones.config.colors.spawn"), cfg.colors().spawnColor().color())
                .setDefaultValue(Config.DEFAULT.colors().spawnColor().color()).setSaveConsumer(spawnColor::set).build());
        colors.addEntry(entries.startColorField(Text.translatable("locator_lodestones.config.colors.dial"), cfg.colors().dialColor().color())
                .setDefaultValue(Config.DEFAULT.colors().dialColor().color()).setSaveConsumer(dialColor::set).build());

        // === When "Save" is clicked ===
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
}