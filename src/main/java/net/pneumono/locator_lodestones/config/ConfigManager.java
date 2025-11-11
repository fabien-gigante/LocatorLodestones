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

import org.jetbrains.annotations.Nullable;

public class ConfigManager {
    private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve(LocatorLodestones.MOD_ID + ".json");
    private static Config CONFIG = Config.DEFAULT;

    public static void initConfig(@Nullable Runnable reset) {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommandManager.literal(LocatorLodestones.id("reloadconfig").toString()).executes(context -> {
                    reloadConfig();
                    context.getSource().sendFeedback(Text.translatable("locator_lodestones.reload"));
                    if (reset != null) reset.run();
                    return 1;
                })
        ));
        reloadConfig();
    }

    public static Config getConfig() {
         return CONFIG; 
    }

    public static void setConfig(Config config) {
        CONFIG = config;
        writeConfigToFile(CONFIG);
    }

    public static void reloadConfig() {
        CONFIG = readConfigFromFile();
        writeConfigToFile(CONFIG);
    }

    public static Config readConfigFromFile() {
        if (CONFIG_FILE.toFile().exists()) {
            JsonElement element = readObjectFromFile();
            DataResult<Pair<Config, JsonElement>> result = Config.CODEC.decode(JsonOps.INSTANCE, element);
            if (result.isSuccess()) {
                return result.getOrThrow().getFirst();
            }
        }
        return Config.DEFAULT;
    }

    public static JsonElement readObjectFromFile() {
        try (Reader reader = Files.newBufferedReader(CONFIG_FILE)) {
            return new GsonBuilder().setPrettyPrinting().create().fromJson(reader, JsonElement.class);
        } catch (IOException e) {
            LocatorLodestones.LOGGER.error("Could not read config file. Default values will be used instead", e);
            return null;
        } catch (JsonSyntaxException e) {
            LocatorLodestones.LOGGER.error("Config file did not use valid syntax. Default config values will be used instead", e);
            return null;
        }
    }

    public static void writeConfigToFile(Config config) {
        DataResult<JsonElement> result = Config.CODEC.encodeStart(JsonOps.INSTANCE, config);
        if (result.isSuccess()) {
            writeObjectToFile(result.getOrThrow());
        } else {
            LocatorLodestones.LOGGER.error("Could not create default config object!");
        }
    }

    public static void writeObjectToFile(JsonElement element) {
        try {
            Writer writer = Files.newBufferedWriter(CONFIG_FILE);
            new GsonBuilder().setPrettyPrinting().create().toJson(element, writer);
            writer.close();
        } catch (IOException e) {
            LocatorLodestones.LOGGER.error("Could not write configuration file.", e);
        }
    }
}
