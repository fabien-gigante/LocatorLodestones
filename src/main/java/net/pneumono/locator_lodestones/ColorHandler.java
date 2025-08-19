package net.pneumono.locator_lodestones;

import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorHandler {
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

        List<String> strings = new ArrayList<>();
        while (matcher.find()) {
            strings.add(matcher.group());
        }

        if (strings.isEmpty()) {
            return Optional.empty();
        } else {
            return parseCode(strings.getFirst());
        }
    }

    public static Optional<Integer> parseCode(String string) {
        try {
            return Optional.of(Integer.parseInt(string, 1, 7, 16));
        } catch (NullPointerException | IndexOutOfBoundsException | NumberFormatException e) {
            LocatorLodestones.LOGGER.error("String '{}' is not a valid color code!", string, e);
            return Optional.empty();
        }
    }
}
