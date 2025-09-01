package net.pneumono.locator_lodestones;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WaypointTracking {
    public static List<ClientWaypoint> WAYPOINTS = new ArrayList<>();

    public static List<ClientWaypoint> updateWaypoints(PlayerEntity player) {
        WAYPOINTS.clear();

        List<ItemStack> stacks = new ArrayList<>();
        DefaultedList<ItemStack> mainStacks = player.getInventory().getMainStacks();
        if (mainStacks != null) {
            stacks.addAll(player.getInventory().getMainStacks());
        }
        ItemStack offHandStack = player.getOffHandStack();
        if (offHandStack != null) {
            stacks.add(player.getOffHandStack());
        }

        for (ItemStack stack : stacks) {
            WAYPOINTS.addAll(getWaypointsFromStack(player, player.getWorld().getRegistryKey(), stack));
        }

        return WAYPOINTS;
    }

    private static List<ClientWaypoint> getWaypointsFromStack(PlayerEntity player, RegistryKey<World> dimension, ItemStack stack) {
        List<ClientWaypoint> waypoints = new ArrayList<>();

        Optional<GlobalPos> lastDeathPos = player.getLastDeathPos();
        if (lastDeathPos.isPresent() && stack.isOf(Items.RECOVERY_COMPASS)) {
            GlobalPos pos = lastDeathPos.get();
            if (pos.dimension() == dimension && pos.pos() != null) {
                waypoints.add(new ClientWaypoint(
                        Vec3d.ofCenter(pos.pos()),
                        getText(stack),
                        LocatorLodestones.id("death"),
                        Optional.of(ColorHandler.getColor(stack).orElse(0xBCE0EB))
                ));
            }
        }

        LodestoneTrackerComponent trackerComponent = stack.get(DataComponentTypes.LODESTONE_TRACKER);
        if (trackerComponent != null && trackerComponent.target().isPresent()) {

            GlobalPos pos = trackerComponent.target().get();
            if (pos.dimension() == dimension && pos.pos() != null) {
                waypoints.add(new ClientWaypoint(
                        Vec3d.ofCenter(pos.pos()),
                        getText(stack),
                        LocatorLodestones.id("lodestone"),
                        ColorHandler.getColor(stack)
                ));
            }
        }

        BundleContentsComponent contentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
        if (contentsComponent != null) {
            contentsComponent.stream().forEach(
                    bundledStack -> waypoints.addAll(getWaypointsFromStack(player, dimension, bundledStack))
            );
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
