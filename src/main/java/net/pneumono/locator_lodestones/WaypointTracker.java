package net.pneumono.locator_lodestones;

import com.mojang.datafixers.util.Either;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWaypointHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.component.type.MapDecorationsComponent;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraft.world.waypoint.TrackedWaypoint;
import net.pneumono.locator_lodestones.config.ConfigManager;
import net.pneumono.locator_lodestones.waypoints.DialWaypoint;
import net.pneumono.locator_lodestones.waypoints.MapWaypoint;
import net.pneumono.locator_lodestones.waypoints.NamedWaypoint;

import java.util.*;

public class WaypointTracker extends AbstractTracker {
    private static final List<TrackedWaypoint> COMPASS_DIAL_WAYPOINTS = new ArrayList<>();
    private final Map<Either<UUID, String>, TrackedWaypoint> WAYPOINTS = new HashMap<>();

    public Collection<TrackedWaypoint> getWaypoints() {
        return WAYPOINTS.values();
    }

    @Override
    public void reset() {
        super.reset();
        WAYPOINTS.clear();
    }

    @Override
    public void update(MinecraftClient client) {
        ClientPlayerEntity player = client.player;

        Map<Either<UUID, String>, TrackedWaypoint> oldWaypoints = new HashMap<>(WAYPOINTS);
        WAYPOINTS.clear();
        getWaypointsFromPlayer(player).forEach(waypoint -> WAYPOINTS.put(waypoint.getSource(), waypoint));

        ClientWaypointHandler waypointHandler = player.networkHandler.getWaypointHandler();
        for (TrackedWaypoint newWaypoint : WAYPOINTS.values()) {
            if (oldWaypoints.containsKey(newWaypoint.getSource())) {
                waypointHandler.onUpdate(newWaypoint);
            } else {
                waypointHandler.onTrack(newWaypoint);
            }
        }
        for (TrackedWaypoint oldWaypoint : oldWaypoints.values()) {
            if (!WAYPOINTS.containsKey(oldWaypoint.getSource())) {
                waypointHandler.onUntrack(oldWaypoint);
            }
        }
    }

    @Override
    public void init() {
        super.init();
        this.buildCompassDial();
    }

    private void buildCompassDial() {
        COMPASS_DIAL_WAYPOINTS.clear();
        int dialResolution = ConfigManager.getConfig().dialResolution();
        for(int i = 0; i < dialResolution; i++) {
            int azimuth = i * 360 / dialResolution;
            var style = azimuth % 90 == 0 ? LocatorLodestones.COMPASS_CARDINAL_STYLE.get(azimuth / 90) :
                        azimuth % 45 == 0 ? LocatorLodestones.COMPASS_DIVISION_STYLE : LocatorLodestones.COMPASS_DIVISION_SMALL_STYLE;
            COMPASS_DIAL_WAYPOINTS.add(new DialWaypoint("dial_" + azimuth, style, (float)(i * Math.TAU / dialResolution)));
        }
    }

    private static List<ItemStack> getPlayerStacks(PlayerEntity player) {
        return getPlayerStacks(player, ConfigManager.getConfig().holdingLocation());
    }

    private static List<TrackedWaypoint> getWaypointsFromPlayer(PlayerEntity player) {
        List<TrackedWaypoint> waypoints = new ArrayList<>();
        List<ItemStack> stacks = getPlayerStacks(player);

        if (ConfigManager.getConfig().dialResolution() > 0) {
            boolean withMaps = ConfigManager.getConfig().showMaps();
            if (stacks.stream().anyMatch(stack -> stack.isOf(Items.COMPASS) || stack.isOf(Items.RECOVERY_COMPASS) || (withMaps && stack.isOf(Items.FILLED_MAP))))
                waypoints.addAll(COMPASS_DIAL_WAYPOINTS);
        }

        //? if >=1.21.9 {
        RegistryKey<World> dimension = player.getEntityWorld().getRegistryKey();
        //?} else {
        /*RegistryKey<World> dimension = player.getWorld().getRegistryKey();
        *///?}
        for (ItemStack stack : stacks)
            waypoints.addAll(getWaypointsFromStack(player, dimension, stack));
        return waypoints;
    }

    private static List<TrackedWaypoint> getWaypointsFromStack(PlayerEntity player, RegistryKey<World> dimension, ItemStack stack) {
        List<TrackedWaypoint> waypoints = new ArrayList<>();
        //? if >=1.21.9 {
        World world = player.getEntityWorld();
        //?} else {
        /*World world = player.getWorld();
        *///?}

        if (ConfigManager.getConfig().showRecovery()) {
            Optional<GlobalPos> lastDeathPos = player.getLastDeathPos();
            if (lastDeathPos.isPresent() && stack.isOf(Items.RECOVERY_COMPASS)) {
                GlobalPos pos = lastDeathPos.get();
                if (pos.dimension() == dimension && pos.pos() != null) {
                    Integer color = ColorHandler.getColor(stack).orElse(ConfigManager.getConfig().colors().recoveryColor().getColorWithAlpha());
                    TrackedWaypoint waypoint = new NamedWaypoint("death_" + pos, LocatorLodestones.DEATH_STYLE, color, pos.pos(), getText(stack));
                    waypoints.add(waypoint);
                }
            }
        }

        LodestoneTrackerComponent trackerComponent = stack.get(DataComponentTypes.LODESTONE_TRACKER);
        if (trackerComponent != null && trackerComponent.target().isPresent()) {
            GlobalPos pos = trackerComponent.target().get();
            if (pos.dimension() == dimension && pos.pos() != null) {
                Integer color = ColorHandler.getColor(stack).orElse(ConfigManager.getConfig().colors().lodestoneColor().getColorWithAlpha());
                TrackedWaypoint waypoint = new NamedWaypoint("lodestone_" + pos, LocatorLodestones.LODESTONE_STYLE, color, pos.pos(), getText(stack));
                waypoints.add(waypoint);
            }
        }

        if (ConfigManager.getConfig().showSpawn() && stack.isOf(Items.COMPASS) && trackerComponent == null) {
            //? if >=1.21.9 {
            GlobalPos pos = world.getSpawnPoint().globalPos();
            //?} else {
            /*GlobalPos pos = new GlobalPos(World.OVERWORLD, world.getSpawnPos());
            *///?}
            if (pos.dimension() == dimension && pos.pos() != null) {
                Integer color = ColorHandler.getColor(stack).orElse(ConfigManager.getConfig().colors().spawnColor().getColorWithAlpha());
                TrackedWaypoint waypoint = new NamedWaypoint("spawn_" + pos, LocatorLodestones.SPAWN_STYLE, color, pos.pos(), getText(stack));
                waypoints.add(waypoint);
            }
        }

        if (ConfigManager.getConfig().showMaps() && stack.isOf(Items.FILLED_MAP)) {
            MapState mapState = FilledMapItem.getMapState(stack, world);
            if (mapState != null && mapState.dimension == dimension) {
                MapIdComponent mapIdComponent = stack.get(DataComponentTypes.MAP_ID);
                MapDecorationsComponent mapDecorationsComponent = stack.get(DataComponentTypes.MAP_DECORATIONS);
                if (mapIdComponent != null && mapDecorationsComponent != null) {
                    mapDecorationsComponent.decorations().forEach((key, deco) -> {
                        Optional<Text> name = (Object)deco instanceof INamed named ? named.getName() : Optional.empty();
                        if (name.isEmpty()) name = getText(stack);
                        TrackedWaypoint waypoint = new MapWaypoint("map_" + mapIdComponent.id() + "_" + key, deco, name);
                        waypoints.add(waypoint);
                    });
                }
            }
        }

        return waypoints;
    }

    private static Optional<Text> getText(ItemStack stack) {
        Text text = stack.get(DataComponentTypes.CUSTOM_NAME);
        if (text == null) {
            text = stack.get(DataComponentTypes.ITEM_NAME);
        }
        return ColorHandler.removeColorCode(text);
    }
}
