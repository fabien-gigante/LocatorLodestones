package net.pneumono.locator_lodestones;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ResourceManager;
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
	public static final RegistryKey<WaypointStyle> SPAWN_STYLE = style("spawn");
	public static final RegistryKey<WaypointStyle> COMPASS_DIVISION_STYLE = style("compass_division");
	public static final RegistryKey<WaypointStyle> COMPASS_DIVISION_SMALL_STYLE = style("compass_division_small");
	public static final List<RegistryKey<WaypointStyle>> COMPASS_CARDINAL_STYLE = List.of( style("compass_south"), style("compass_west"), style("compass_north"), style("compass_east") );

	public static WaypointTracker waypointTracker = new WaypointTracker();
	public static BedtimeTracker bedtimeTracker = new BedtimeTracker();

	@SuppressWarnings("deprecation")
	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing Locator Lodestones");
		ConfigManager.initConfig(this::reset);
		waypointTracker.init();
		ClientTickEvents.END_CLIENT_TICK.register(this::tick);
		ServerLifecycleEvents.SERVER_STARTING.register(server -> this.reset());
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() { return id("waypoint_style_assets_listener"); }
			@Override
			public void reload(ResourceManager manager) { MapWaypointStyleAssets.reload(); }
		});
	}

	public void tick(MinecraftClient client) {
		waypointTracker.tick(client);
		bedtimeTracker.tick(client);
	}

	public void reset() {
		waypointTracker.reset();
		bedtimeTracker.reset();
	}

	public static void onInventoryChanged() {
		waypointTracker.markDirty();
		bedtimeTracker.markDirty();
	}

	public static RegistryKey<WaypointStyle> style(String path) {
		return RegistryKey.of(WaypointStyles.REGISTRY, id(path));
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}