package net.pneumono.locator_lodestones.config;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.ColorHelper;
import net.pneumono.locator_lodestones.LocatorLodestones;
import org.jetbrains.annotations.Nullable;

public record ColorProvider(int color) {
    public static final Codec<ColorProvider> CODEC = Codec.STRING.xmap(ColorProvider::validate, ColorProvider::asString);
    public static final int RANDOM_COLOR = -1;

    public static ColorProvider validate(String color) {
        try {
            return new ColorProvider(Integer.parseInt(color.replace("#",""), 16));
        } catch (NumberFormatException e) {
            if (!color.equals("random")) {
                LocatorLodestones.LOGGER.error("Invalid config value '{}'", color);
            }
            return new ColorProvider(RANDOM_COLOR);
        }
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
