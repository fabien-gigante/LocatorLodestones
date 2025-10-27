package net.pneumono.locator_lodestones;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.waypoint.TrackedWaypoint;
import net.pneumono.locator_lodestones.config.ConfigManager;

import java.util.Optional;

//? if >=1.21.9 {
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.waypoint.EntityTickProgress;
//?}

public class WaypointNameRendering {
    public static void renderNames(MinecraftClient client, DrawContext context, RenderTickCounter tickCounter, int centerY) {
        if (!ConfigManager.tabShowsNames() || !client.options.playerListKey.isPressed()) return;

        //? if >=1.21.9 {
        Entity cameraEntity = client.getCameraEntity();
        if (cameraEntity == null) return;
        World world = cameraEntity.getEntityWorld();
        EntityTickProgress entityTickProgress = (tickedEntity) -> tickCounter.getTickProgress(
                !world.getTickManager().shouldSkipTick(tickedEntity)
        );
        //?}

        TrackedWaypoint bestWaypoint = null;
        double bestYaw = 61;
        for (TrackedWaypoint waypoint : WaypointTracking.getWaypoints()) {
            //? if >=1.21.9 {
            double yaw = waypoint.getRelativeYaw(client.world, client.gameRenderer.getCamera(), entityTickProgress);
            //?} else {
            /*double yaw = waypoint.getRelativeYaw(client.world, client.gameRenderer.getCamera());
            *///?}
            double absYaw = Math.abs(yaw);
            if (absYaw < Math.abs(bestYaw)) {
                bestYaw = yaw;
                bestWaypoint = waypoint;
            }
        }

        if (bestWaypoint != null) {
            Optional<Text> textOptional = WaypointTracking.getWaypointName(bestWaypoint.getSource());
            if (textOptional.isPresent()) {
                Text text = textOptional.get();
                TextRenderer textRenderer = client.textRenderer;

                int width = textRenderer.getWidth(text);
                int x = getXFromYaw(context, bestYaw) - width / 2;

                context.fill(x + 5 - 2, centerY - 10 - 2, x + width + 5 + 2, centerY - 10 + 9 + 2, ColorHelper.withAlpha(0.5F, Colors.BLACK));

                context.drawTextWithShadow(
                        textRenderer,
                        text,
                        x + 5,
                        centerY - 10,
                        Colors.WHITE
                );
            }
        }
    }

    private static int getXFromYaw(DrawContext context, double relativeYaw) {
        return MathHelper.ceil((context.getScaledWindowWidth() - 9) / 2.0F) + (int)(relativeYaw * 173.0 / 2.0 / 60.0);
    }
}
