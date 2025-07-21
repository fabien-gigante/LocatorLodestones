package net.pneumono.locator_lodestones;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
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

	public static List<Lodestone> getLodestonePositions(PlayerEntity player) {
		List<Lodestone> lodestones = new ArrayList<>();

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

	private static List<Lodestone> getLodestonePositions(RegistryKey<World> dimension, ItemStack stack) {
		List<Lodestone> lodestones = new ArrayList<>();

		LodestoneTrackerComponent trackerComponent = stack.get(DataComponentTypes.LODESTONE_TRACKER);
		if (trackerComponent != null && trackerComponent.target().isPresent()) {

			GlobalPos pos = trackerComponent.target().get();
			if (pos.dimension() == dimension && pos.pos() != null) {
				lodestones.add(
						new Lodestone(
								new Vec3d(pos.pos().getX() + 0.5, pos.pos().getY(), pos.pos().getZ() + 0.5),
								getColor(stack)
						)
				);
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

	private static Optional<Integer> getColor(ItemStack stack) {
		Optional<Integer> color = checkText(stack.get(DataComponentTypes.CUSTOM_NAME));
		if (color.isEmpty()) {
			color = checkText(stack.get(DataComponentTypes.ITEM_NAME));
		}
		return color;
	}

	private static Optional<Integer> checkText(Text text) {
		if (text == null) return Optional.empty();

		String string = text.getString();
		boolean colorsChanged = false;
		int r = 0;
		int g = 0;
		int b = 0;

		stringLoop:
		for (int i = 0; i < string.length() - 6; ++i) {
			char first = string.charAt(i);
			if (first != '#') continue;

			char[] chars = new char[]{
				string.charAt(i + 1),
				string.charAt(i + 2),
				string.charAt(i + 3),
				string.charAt(i + 4),
				string.charAt(i + 5),
				string.charAt(i + 6)
			};
			for (int j = 0; j < chars.length; ++j) {
				char c = chars[j];
				Optional<Integer> optional = asInt(c);
				if (optional.isEmpty()) continue stringLoop;

				int charColor = optional.get();
				if (j % 2 == 0) charColor *= 16;

				if (j == 0 || j == 1) {
					r += charColor;
				} else if (j == 2 || j == 3) {
					g += charColor;
				} else {
					b += charColor;
				}
				colorsChanged = true;
			}
			break;
		}

		if (!colorsChanged) return Optional.empty();

		int color = r * 16 * 16 * 16 * 16;
		color += g * 16 * 16;
		color += b;

		return Optional.of(color);
	}

	private static Optional<Integer> asInt(char c) {
		return Optional.ofNullable(switch (c) {
			case '0' -> 0;
			case '1' -> 1;
			case '2' -> 2;
			case '3' -> 3;
			case '4' -> 4;
			case '5' -> 5;
			case '6' -> 6;
			case '7' -> 7;
			case '8' -> 8;
			case '9' -> 9;
			case 'a', 'A' -> 10;
			case 'b', 'B' -> 11;
			case 'c', 'C' -> 12;
			case 'd', 'D' -> 13;
			case 'e', 'E' -> 14;
			case 'f', 'F' -> 15;
            default -> null;
        });
	}
}