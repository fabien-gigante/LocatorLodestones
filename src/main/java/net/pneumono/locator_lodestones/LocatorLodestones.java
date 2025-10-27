package net.pneumono.locator_lodestones;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.waypoint.WaypointStyle;
import net.minecraft.world.waypoint.WaypointStyles;
import net.pneumono.locator_lodestones.config.ConfigManager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocatorLodestones implements ClientModInitializer {
	public static final String MOD_ID = "locator_lodestones";

	public static final Logger LOGGER = LoggerFactory.getLogger("Locator Lodestones");

	public static final RegistryKey<WaypointStyle> LODESTONE_STYLE = style("lodestone");
	public static final RegistryKey<WaypointStyle> DEATH_STYLE = style("death");
	public static final RegistryKey<WaypointStyle> COMPASS_DIVISION_STYLE = style("compass_division");
	public static final RegistryKey<WaypointStyle> COMPASS_DIVISION_SMALL_STYLE = style("compass_division_small");
	public static final List<RegistryKey<WaypointStyle>> COMPASS_CARDINAL_STYLE = List.of( style("compass_south"), style("compass_west"), style("compass_north"), style("compass_east") );

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing Locator Lodestones");
		ConfigManager.initConfig();
		WaypointTracking.init();
		ClientTickEvents.END_CLIENT_TICK.register(client -> WaypointTracking.updateWaypoints(client.player));
		ServerLifecycleEvents.SERVER_STARTING.register(server -> WaypointTracking.resetWaypoints());
	}

	private static RegistryKey<WaypointStyle> style(String path) {
		return RegistryKey.of(WaypointStyles.REGISTRY, id(path));
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}