package net.pneumono.locator_lodestones;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.waypoint.WaypointStyle;
import net.minecraft.world.waypoint.WaypointStyles;
import net.pneumono.locator_lodestones.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocatorLodestones implements ClientModInitializer {
	public static final String MOD_ID = "locator_lodestones";

	public static final Logger LOGGER = LoggerFactory.getLogger("Locator Lodestones");

	public static final RegistryKey<WaypointStyle> LODESTONE_STYLE = style("lodestone");
	public static final RegistryKey<WaypointStyle> DEATH_STYLE = style("death");

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing Locator Lodestones");
		ConfigManager.initConfig();
		ClientTickEvents.END_CLIENT_TICK.register(client -> WaypointTracking.updateWaypoints(client.player));
		ClientTickEvents.START_WORLD_TICK.register(world -> WaypointTracking.resetWaypoints());
	}

	private static RegistryKey<WaypointStyle> style(String path) {
		return RegistryKey.of(WaypointStyles.REGISTRY, id(path));
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}