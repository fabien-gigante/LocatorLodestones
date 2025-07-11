package net.pneumono.locator_lodestones;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

	public static List<BlockPos> getLodestones(PlayerEntity player) {
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

			getLodestone(player, stack).ifPresent(lodestones::add);

			BundleContentsComponent contentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
			if (contentsComponent != null) {
				contentsComponent.stream().forEach(
						bundledStack -> getLodestone(player, bundledStack).ifPresent(lodestones::add)
				);
			}
		}

		return lodestones;
	}

	private static Optional<BlockPos> getLodestone(PlayerEntity player, ItemStack stack) {
		LodestoneTrackerComponent trackerComponent = stack.get(DataComponentTypes.LODESTONE_TRACKER);
		if (trackerComponent != null && trackerComponent.target().isPresent()) {

			GlobalPos pos = trackerComponent.target().get();
			if (pos.dimension() == player.getWorld().getRegistryKey()) {
				return Optional.ofNullable(pos.pos());
			}
		}
		return Optional.empty();
	}
}