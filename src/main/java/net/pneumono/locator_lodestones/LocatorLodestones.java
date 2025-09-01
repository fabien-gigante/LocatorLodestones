package net.pneumono.locator_lodestones;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
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

	public static List<Lodestone> updateLodestonePositions(PlayerEntity player) {
		LodestoneBarRendering.LODESTONES.clear();

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
			LodestoneBarRendering.LODESTONES.addAll(getLodestonePositions(player, player.getWorld().getRegistryKey(), stack));
		}

		return LodestoneBarRendering.LODESTONES;
	}

	private static List<Lodestone> getLodestonePositions(PlayerEntity player, RegistryKey<World> dimension, ItemStack stack) {
		List<Lodestone> lodestones = new ArrayList<>();

		Optional<GlobalPos> lastDeathPos = player.getLastDeathPos();
		if (lastDeathPos.isPresent() && stack.isOf(Items.RECOVERY_COMPASS)) {
			GlobalPos pos = lastDeathPos.get();
			if (pos.dimension() == dimension && pos.pos() != null) {
				lodestones.add(new Lodestone(
						new Vec3d(pos.pos().getX() + 0.5, pos.pos().getY(), pos.pos().getZ() + 0.5),
						getText(stack),
						LocatorLodestones.id("death"),
						Optional.of(getColor(stack).orElse(0xBCE0EB))
				));
			}
		}

		LodestoneTrackerComponent trackerComponent = stack.get(DataComponentTypes.LODESTONE_TRACKER);
		if (trackerComponent != null && trackerComponent.target().isPresent()) {

			GlobalPos pos = trackerComponent.target().get();
			if (pos.dimension() == dimension && pos.pos() != null) {
				lodestones.add(
						new Lodestone(
								new Vec3d(pos.pos().getX() + 0.5, pos.pos().getY(), pos.pos().getZ() + 0.5),
								getText(stack),
								LocatorLodestones.id("lodestone"),
								getColor(stack)
						)
				);
			}
		}

		BundleContentsComponent contentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
		if (contentsComponent != null) {
			contentsComponent.stream().forEach(
					bundledStack -> lodestones.addAll(getLodestonePositions(player, dimension, bundledStack))
			);
		}

		return lodestones;
	}

	private static Optional<Integer> getColor(ItemStack stack) {
		Optional<Integer> color = ColorHandler.getColor(stack.get(DataComponentTypes.CUSTOM_NAME));
		if (color.isEmpty()) {
			color = ColorHandler.getColor(stack.get(DataComponentTypes.ITEM_NAME));
		}
		return color;
	}

	@Nullable
	private static Text getText(ItemStack stack) {
		Text text = stack.get(DataComponentTypes.CUSTOM_NAME);
		if (text == null) {
			text = stack.get(DataComponentTypes.ITEM_NAME);
		}
		return ColorHandler.removeColorCode(text);
	}
}