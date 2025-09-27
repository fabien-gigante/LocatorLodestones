package net.pneumono.locator_lodestones;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import net.pneumono.locator_lodestones.config.ConfigManager;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorHandler {
    public static Optional<Integer> getColor(ItemStack stack) {
        if (!ConfigManager.colorCustomization()) return Optional.empty();

        Optional<Integer> color = getColor(stack.get(DataComponentTypes.CUSTOM_NAME));
        if (color.isEmpty()) {
            color = getColor(stack.get(DataComponentTypes.ITEM_NAME));
        }
        return color;
    }

    public static Optional<Integer> getColor(Text text) {
        if (text == null) {
            return Optional.empty();
        } else {
            return getColor(text.getString());
        }
    }

    public static Optional<Integer> getColor(String text) {
        if (text == null) {
            return Optional.empty();
        }

        Matcher matcher = Pattern.compile("#[0-9a-fA-F]{6}").matcher(text);

        String string = null;
        if (matcher.find()) {
            string = matcher.group();
        }

        return parseCode(string);
    }

    public static Optional<Integer> parseCode(@Nullable String string) {
        if (string == null) return Optional.empty();

        try {
            return Optional.of(ColorHelper.withAlpha(255, Integer.parseInt(string, 1, 7, 16)));
        } catch (NullPointerException | IndexOutOfBoundsException | NumberFormatException e) {
            LocatorLodestones.LOGGER.error("String '{}' is not a valid color code!", string, e);
            return Optional.empty();
        }
    }

    public static Optional<Text> removeColorCode(Text text) {
        if (text == null) return Optional.empty();

        return Optional.of(Text.literal(
                text.getString().replaceAll("( ?)([({<\\[]?)(#[0-9a-fA-F]{6})([)}>\\]]?)", "")
        ));
    }
}
