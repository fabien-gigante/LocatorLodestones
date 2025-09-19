package net.pneumono.locator_lodestones.config;

import com.mojang.serialization.Codec;
import net.pneumono.locator_lodestones.LocatorLodestones;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ColorProvider {
    public static final Codec<ColorProvider> CODEC = Codec.STRING.xmap(ColorProvider::validate, ColorProvider::asString);

    private final @Nullable Integer color;

    public ColorProvider(@Nullable Integer color) {
        this.color = color;
    }

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

    public Optional<Integer> getColor() {
        return Optional.ofNullable(this.color);
    }

    public String asString() {
        if (color != null) {
            return Integer.toString(color, 16);
        } else {
            return "random";
        }
    }
}
