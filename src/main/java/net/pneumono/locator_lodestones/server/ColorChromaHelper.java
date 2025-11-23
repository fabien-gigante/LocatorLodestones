package net.pneumono.locator_lodestones.server;

import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.BlockPos;

public class ColorChromaHelper {
    public static void rgbToHsb(int r, int g, int b, float[] hsb) {
        float rf = r / 255f, gf = g / 255f, bf = b / 255f;
        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float delta = max - min,  h = 0f;
        if (delta != 0f) {
            if (max == rf) h = ((gf - bf) / delta) % 6f;
            else if (max == gf) h = ((bf - rf) / delta) + 2f;
            else h = ((rf - gf) / delta) + 4f;
            h /= 6f;
            if (h < 0f) h += 1f;
        }
        float s = (max == 0f) ? 0f : (delta / max), v = max;
        hsb[0] = h; hsb[1] = s; hsb[2] = v;
    }

    public static int hsbToRgb(float h, float s, float v) {
        float c = v * s, x = c * (1 - Math.abs((h * 6) % 2 - 1)), m = v - c;
        float rf = 0, gf = 0, bf = 0, h6 = h * 6;
        if      (0 <= h6 && h6 < 1) { rf = c; gf = x; bf = 0; }
        else if (1 <= h6 && h6 < 2) { rf = x; gf = c; bf = 0; }
        else if (2 <= h6 && h6 < 3) { rf = 0; gf = c; bf = x; }
        else if (3 <= h6 && h6 < 4) { rf = 0; gf = x; bf = c; }
        else if (4 <= h6 && h6 < 5) { rf = x; gf = 0; bf = c; }
        else                        { rf = c; gf = 0; bf = x; }
        int r = Math.round((rf + m) * 255);
        int g = Math.round((gf + m) * 255);
        int b = Math.round((bf + m) * 255);
        return (r << 16) | (g << 8) | b;
    }

    private static final float HUE_STEP = 0.137f, SATURATION_STEP = 0.278f; // irrational steps to avoid cycles

    public static int cycleHueSaturation(int color) {
        int r = (color >> 16) & 0xff, g = (color >> 8) & 0xff, b = color & 0xff;
        float[] hsb = new float[3]; rgbToHsb(r, g, b, hsb);
        float h = (hsb[0] + HUE_STEP) % 1f;
        float s = (float) Math.pow((hsb[1] * hsb[1] + SATURATION_STEP) % 1f, 0.5f);
        return hsbToRgb(h, s, hsb[2]);
    }

    public static int colorFromPos(BlockPos pos) {
        LocalRandom rand = new LocalRandom(pos.asLong());
        float h = rand.nextFloat(), s = (float) Math.pow(rand.nextFloat(), 0.5f);
        return hsbToRgb(h, s, 0.9f);
    }
}
