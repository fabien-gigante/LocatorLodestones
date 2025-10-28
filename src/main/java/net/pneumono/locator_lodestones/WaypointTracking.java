package net.pneumono.locator_lodestones;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWaypointHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraft.world.waypoint.TrackedWaypoint;
import net.pneumono.locator_lodestones.config.Config;
import net.pneumono.locator_lodestones.config.ConfigManager;
import net.pneumono.locator_lodestones.waypoints.CompassDialWaypoint;
import net.pneumono.locator_lodestones.waypoints.NamedPositionalWaypoint;

import java.util.*;

public class WaypointTracking {
    private static int UPDATE_COOLDOWN = 10;
    private static final Map<Either<UUID, String>, TrackedWaypoint> WAYPOINTS = new HashMap<>();
    private static final List<TrackedWaypoint> COMPASS_DIAL_WAYPOINTS = new ArrayList<>();
    private static boolean dirty = false;
    private static long lastUpdateTime = 0;

    public static Collection<TrackedWaypoint> getWaypoints() {
        return WAYPOINTS.values();
    }

    public static void markWaypointsDirty() {
        dirty = true;
    }

    public static void resetWaypoints() {
        WAYPOINTS.clear();
        lastUpdateTime = 0;
        markWaypointsDirty();
    }

    public static void updateWaypoints(ClientPlayerEntity player) {
        if (!dirty || player == null || (lastUpdateTime + UPDATE_COOLDOWN > player.age && lastUpdateTime < player.age)) return;
        lastUpdateTime = player.age;
        dirty = false;

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

    public static void init() {
        for(int azimuth = 0; azimuth < 360; azimuth += 15) {
            var style = azimuth % 90 == 0 ? LocatorLodestones.COMPASS_CARDINAL_STYLE.get(azimuth / 90) :
                        azimuth % 45 == 0 ? LocatorLodestones.COMPASS_DIVISION_STYLE : LocatorLodestones.COMPASS_DIVISION_SMALL_STYLE;
            COMPASS_DIAL_WAYPOINTS.add(new CompassDialWaypoint("dial_" + azimuth, style, (float)(azimuth * Math.PI / 180)));
        }
    }

    private static List<ItemStack> getPlayerStacks(PlayerEntity player) {
        List<ItemStack> stacks = new ArrayList<>();
        switch(ConfigManager.getConfig().holdingLocation()) {
            case Config.HoldingLocation.HANDS:
                ItemStack selectedStack = player.getInventory().getSelectedStack();
                if (selectedStack != null) stacks.add(selectedStack);
                break;
            case Config.HoldingLocation.HOTBAR: 
                for (int slot = 0; slot < PlayerInventory.getHotbarSize(); slot++) {
                    ItemStack stack = player.getInventory().getStack(slot);
                    if (stack != null) stacks.add(stack);
                }
                break;
            default:
                DefaultedList<ItemStack> mainStacks = player.getInventory().getMainStacks();
                if (mainStacks != null) stacks.addAll(mainStacks);
        }
        ItemStack offHandStack = player.getOffHandStack();
        if (offHandStack != null) stacks.add(offHandStack);

        if (ConfigManager.getConfig().showBundled()) {
            ListIterator<ItemStack> it = stacks.listIterator();
            while (it.hasNext()) {
                BundleContentsComponent contentsComponent = it.next().get(DataComponentTypes.BUNDLE_CONTENTS);
                if (contentsComponent != null) contentsComponent.stream().forEach(stack -> it.add(stack));
            }
        }
        return stacks;
    }

    private static List<TrackedWaypoint> getWaypointsFromPlayer(PlayerEntity player) {
        List<TrackedWaypoint> waypoints = new ArrayList<>();
        List<ItemStack> stacks = getPlayerStacks(player);

        if (ConfigManager.getConfig().showCompassDial() &&
                stacks.stream().anyMatch(stack -> stack.isOf(Items.COMPASS) || stack.isOf(Items.RECOVERY_COMPASS)))
            waypoints.addAll(COMPASS_DIAL_WAYPOINTS);

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

        if (ConfigManager.getConfig().showRecovery()) {
            Optional<GlobalPos> lastDeathPos = player.getLastDeathPos();
            if (lastDeathPos.isPresent() && stack.isOf(Items.RECOVERY_COMPASS)) {
                GlobalPos pos = lastDeathPos.get();
                if (pos.dimension() == dimension && pos.pos() != null) {
                    Integer color = ColorHandler.getColor(stack).orElse(ConfigManager.getConfig().recoveryColor().getColorWithAlpha());
                    TrackedWaypoint waypoint = new NamedPositionalWaypoint("death_" + pos, LocatorLodestones.DEATH_STYLE, color, pos.pos(), getText(stack));
                    waypoints.add(waypoint);
                }
            }
        }

        LodestoneTrackerComponent trackerComponent = stack.get(DataComponentTypes.LODESTONE_TRACKER);
        if (trackerComponent != null && trackerComponent.target().isPresent()) {
            GlobalPos pos = trackerComponent.target().get();
            if (pos.dimension() == dimension && pos.pos() != null) {
                Integer color = ColorHandler.getColor(stack).orElse(ConfigManager.getConfig().lodestoneColor().getColorWithAlpha());
                TrackedWaypoint waypoint = new NamedPositionalWaypoint("lodestone_" + pos, LocatorLodestones.LODESTONE_STYLE, color, pos.pos(), getText(stack));
                waypoints.add(waypoint);
            }
        }

        if (ConfigManager.getConfig().showSpawn() && stack.isOf(Items.COMPASS) && trackerComponent == null) {
            GlobalPos pos = player.getEntityWorld().getSpawnPoint().globalPos();
            if (pos.dimension() == dimension && pos.pos() != null) {
                Integer color = ColorHandler.getColor(stack).orElse(ConfigManager.getConfig().spawnColor().getColorWithAlpha());
                TrackedWaypoint waypoint = new NamedPositionalWaypoint("spawn_" + pos, LocatorLodestones.SPAWN_STYLE, color, pos.pos(), getText(stack));
                waypoints.add(waypoint);
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
