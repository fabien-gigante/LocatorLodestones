package net.pneumono.locator_lodestones;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.util.Identifier;
import net.pneumono.locator_lodestones.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocatorLodestones implements ClientModInitializer {
	public static final String MOD_ID = "locator_lodestones";

	public static final Logger LOGGER = LoggerFactory.getLogger("Locator Lodestones");

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing Locator Lodestones");
		ConfigManager.initConfig();
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}