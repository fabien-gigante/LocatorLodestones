package net.pneumono.locator_lodestones.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.pneumono.locator_lodestones.LocatorLodestones;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve(LocatorLodestones.MOD_ID + ".json");
    public static Config CONFIG = Config.DEFAULT;

    public static void initConfig() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommandManager.literal(LocatorLodestones.id("reloadconfig").toString()).executes(context -> {
                    if (shouldShowBundled()) {
                        context.getSource().sendFeedback(Text.literal("yippee 1"));
                    } else {
                        context.getSource().sendFeedback(Text.literal("woohoo 1"));
                    }
                    reloadConfig();
                    if (shouldShowBundled()) {
                        context.getSource().sendFeedback(Text.literal("yippee 2"));
                    } else {
                        context.getSource().sendFeedback(Text.literal("woohoo 2"));
                    }
                    return 1;
                })
        ));

        reloadConfig();
    }

    public static void reloadConfig() {
        if (CONFIG_FILE.toFile().exists()) {
            JsonElement element = readFromFile();
            DataResult<Pair<Config, JsonElement>> result = Config.CODEC.decode(JsonOps.INSTANCE, element);
            if (result.isSuccess()) {
                CONFIG = result.getOrThrow().getFirst();
            }

        } else {
            DataResult<JsonElement> result = Config.CODEC.encodeStart(JsonOps.INSTANCE, Config.DEFAULT);
            if (result.isSuccess()) {
                writeObject(result.getOrThrow());
            } else {
                LocatorLodestones.LOGGER.error("Could not create default config object!");
            }
        }
    }

    private static JsonElement readFromFile() {
        try {
            Reader reader = Files.newBufferedReader(CONFIG_FILE);
            JsonElement element = new GsonBuilder().setPrettyPrinting().create().fromJson(reader, JsonElement.class);
            reader.close();
            return element;
        } catch (IOException e) {
            LocatorLodestones.LOGGER.error("Could not read config file. Default values will be used instead", e);
            return null;
        } catch (JsonSyntaxException e) {
            LocatorLodestones.LOGGER.error("Config file did not use valid syntax. Default config values will be used instead", e);
            return null;
        }
    }

    private static void writeObject(JsonElement element) {
        try {
            Writer writer = Files.newBufferedWriter(CONFIG_FILE);
            new GsonBuilder().setPrettyPrinting().create().toJson(element, writer);
            writer.close();
        } catch (IOException e) {
            LocatorLodestones.LOGGER.error("Could not write configuration file.", e);
        }
    }

    public static boolean tabForcesLocatorBar() {
        return CONFIG.tabForcesLocatorBar();
    }

    public static boolean tabShowsNames() {
        return CONFIG.tabShowsNames();
    }

    public static boolean shouldShowRecovery() {
        return CONFIG.shouldShowRecovery();
    }

    public static boolean shouldShowBundled() {
        return CONFIG.shouldShowBundled();
    }

    public static boolean colorCustomization() {
        return CONFIG.colorCustomization();
    }

    public static ColorProvider getLodestoneColor() {
        return CONFIG.getLodestoneColor();
    }

    public static ColorProvider getRecoveryColor() {
        return CONFIG.getRecoveryColor();
    }
}
