package net.pneumono.locator_lodestones.config;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.ColorHelper;
import net.pneumono.locator_lodestones.LocatorLodestones;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public record ColorProvider(int color) {
    public static final Codec<ColorProvider> CODEC = Codec.STRING.xmap(ColorProvider::parse, ColorProvider::asString);
    public static final int RANDOM_COLOR = -1;
  
    private static Optional<ColorProvider> validate(String color) {
        if  (color.equals("random")) 
            return Optional.of(new ColorProvider(RANDOM_COLOR));
        try {
            String hex = color.startsWith("#") ? color.substring(1) : color;
            return Optional.of(new ColorProvider(Integer.parseInt(hex, 16)));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static boolean isValid(String color) { return validate(color).isPresent(); }

    public static ColorProvider parse(String color) {
        Optional<ColorProvider> result = validate(color);
        if (result.isEmpty())
            LocatorLodestones.LOGGER.error("Invalid config value '{}'", color);
        return result.orElse(new ColorProvider(RANDOM_COLOR));
    }

    public @Nullable Integer getColorWithAlpha(int alpha) {
        return color == RANDOM_COLOR ? null : ColorHelper.withAlpha(alpha, color);
    }
    public @Nullable Integer getColorWithAlpha() {
        return getColorWithAlpha(255);
    }

    public String asString() {
        if (color != RANDOM_COLOR) {
            return "#" + Integer.toString(color, 16);
        } else {
            return "random";
        }
    }
}
