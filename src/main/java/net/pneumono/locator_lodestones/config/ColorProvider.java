package net.pneumono.locator_lodestones.config;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.ColorHelper;
import net.pneumono.locator_lodestones.LocatorLodestones;
import org.jetbrains.annotations.Nullable;

public record ColorProvider(@Nullable Integer color) {
    public static final Codec<ColorProvider> CODEC = Codec.STRING.xmap(ColorProvider::validate, ColorProvider::asString);

    public static ColorProvider validate(String color) {
        try {
            return new ColorProvider(Integer.parseInt(color, 16));
        } catch (NumberFormatException e) {
            if (!color.equals("random")) {
                LocatorLodestones.LOGGER.error("Invalid config value '{}'", color);
            }
            return new ColorProvider(null);
        }
    }

    public @Nullable Integer getColorWithAlpha(int alpha) {
        return color == null ? null : ColorHelper.withAlpha(alpha, color);
    }
    public @Nullable Integer getColorWithAlpha() {
        return getColorWithAlpha(255);
    }

    public String asString() {
        if (color != null) {
            return Integer.toString(color, 16);
        } else {
            return "random";
        }
    }
}
