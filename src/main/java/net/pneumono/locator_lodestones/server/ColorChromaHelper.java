package net.pneumono.locator_lodestones.server;

import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;

public class ColorChromaHelper {
    private static final float BRIGHTNESS = 0.9f;
    
    public static int cycleHueSaturation(int color) {
        LocalRandom rand = new LocalRandom(color);
        return ColorHelper.withBrightness(rand.next(24), BRIGHTNESS);
    }

    public static int colorFromPos(BlockPos pos) {
        LocalRandom rand = new LocalRandom(pos.asLong());
        return ColorHelper.withBrightness(rand.next(24), BRIGHTNESS);
    }
}
