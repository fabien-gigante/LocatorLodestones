package net.pneumono.locator_lodestones;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LocatorLodestones implements ClientModInitializer {
	public static final String MOD_ID = "locator_lodestones";

	public static final Logger LOGGER = LoggerFactory.getLogger("Locator Lodestones");

	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing Locator Lodestones");
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	public static List<BlockPos> getLodestonePositions(PlayerEntity player) {
		List<BlockPos> lodestones = new ArrayList<>();

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
			lodestones.addAll(getLodestonePositions(player.getWorld().getRegistryKey(), stack));
		}

		return lodestones;
	}

	private static List<BlockPos> getLodestonePositions(RegistryKey<World> dimension, ItemStack stack) {
		List<BlockPos> lodestones = new ArrayList<>();

		LodestoneTrackerComponent trackerComponent = stack.get(DataComponentTypes.LODESTONE_TRACKER);
		if (trackerComponent != null && trackerComponent.target().isPresent()) {

			GlobalPos pos = trackerComponent.target().get();
			if (pos.dimension() == dimension && pos.pos() != null) {
				lodestones.add(pos.pos());
			}
		}

		BundleContentsComponent contentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
		if (contentsComponent != null) {
			contentsComponent.stream().forEach(
					bundledStack -> lodestones.addAll(getLodestonePositions(dimension, bundledStack))
			);
		}

		return lodestones;
	}
}