package net.pneumono.locator_lodestones;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.resource.waypoint.WaypointStyleAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.waypoint.TrackedWaypoint;
import net.minecraft.world.waypoint.WaypointStyles;

import java.util.Comparator;
import java.util.Optional;

public class WaypointRendering {
    private static final Identifier ARROW_UP = Identifier.ofVanilla("hud/locator_bar_arrow_up");
    private static final Identifier ARROW_DOWN = Identifier.ofVanilla("hud/locator_bar_arrow_down");

    public static void renderWaypoints(MinecraftClient client, DrawContext context, int centerY) {
        if (client.player == null || client.cameraEntity == null) return;

        WaypointTracking.WAYPOINTS.stream()
                .sorted(Comparator.comparingDouble(
                        waypoint -> -waypoint.pos().squaredDistanceTo(client.cameraEntity.getPos())
                ))
                .forEachOrdered(pos -> renderWaypoint(client, context, centerY, pos));

        if (!client.options.playerListKey.isPressed()) return;

        Optional<Text> bestText = Optional.empty();
        double bestYaw = 61;
        for (ClientWaypoint waypoint : WaypointTracking.WAYPOINTS) {
            double yaw = getRelativeYaw(waypoint.pos(), client.gameRenderer.getCamera());
            double absYaw = Math.abs(yaw);
            if (absYaw < Math.abs(bestYaw)) {
                bestYaw = yaw;
                bestText = waypoint.text();
            }
        }

        if (bestText.isPresent()) {
            Text text = bestText.get();
            TextRenderer textRenderer = client.textRenderer;

            int x = getXFromYaw(context, bestYaw) - textRenderer.getWidth(text) / 2;
            int width = textRenderer.getWidth(text);

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

    private static void renderWaypoint(MinecraftClient client, DrawContext context, int centerY, ClientWaypoint waypoint) {
        if (client.player == null || client.cameraEntity == null) return;

        double relativeYaw = getRelativeYaw(waypoint.pos(), client.gameRenderer.getCamera());
        if (relativeYaw <= -61.0 || relativeYaw > 60.0)  return;

        WaypointStyleAsset waypointStyleAsset = client.getWaypointStyleAssetManager().get(
                RegistryKey.of(WaypointStyles.REGISTRY, waypoint.style())
        );
        Identifier identifier = waypointStyleAsset.getSpriteForDistance(
                (float) Math.sqrt(waypoint.pos().squaredDistanceTo(client.cameraEntity.getPos()))
        );

        int color = ColorHelper.withAlpha(255, waypoint.getColor());

        int x = getXFromYaw(context, relativeYaw);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, x, centerY - 2, 9, 9, color);

        TrackedWaypoint.Pitch pitch = getPitch(waypoint.pos(), client.gameRenderer);
        if (pitch != TrackedWaypoint.Pitch.NONE) {
            int yOffset;
            Identifier texture;
            if (pitch == TrackedWaypoint.Pitch.DOWN) {
                yOffset = 6;
                texture = ARROW_DOWN;
            } else {
                yOffset = -6;
                texture = ARROW_UP;
            }

            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, texture, x + 1, centerY + yOffset, 7, 5);
        }
    }

    private static int getXFromYaw(DrawContext context, double relativeYaw) {
        return MathHelper.ceil((context.getScaledWindowWidth() - 9) / 2.0F) + (int)(relativeYaw * 173.0 / 2.0 / 60.0);
    }

    /**
     * Adaptation of a vanilla method.
     *
     * @see TrackedWaypoint#getRelativeYaw(World, TrackedWaypoint.YawProvider)
     */
    private static double getRelativeYaw(Vec3d pos, TrackedWaypoint.YawProvider yawProvider) {
        Vec3d vec3d = yawProvider.getCameraPos().subtract(pos).rotateYClockwise();
        float f = (float)MathHelper.atan2(vec3d.getZ(), vec3d.getX()) * (180.0F / (float)Math.PI);
        return MathHelper.subtractAngles(yawProvider.getCameraYaw(), f);
    }

    /**
     * Adaptation of a vanilla method.
     *
     * @see TrackedWaypoint#getPitch(World, TrackedWaypoint.PitchProvider)
     */
    private static TrackedWaypoint.Pitch getPitch(Vec3d pos, TrackedWaypoint.PitchProvider cameraProvider) {
        Vec3d vec3d = cameraProvider.project(pos);
        boolean bl = vec3d.z > 1.0;
        double d = bl ? -vec3d.y : vec3d.y;
        if (d < -1.0) {
            return TrackedWaypoint.Pitch.DOWN;
        } else if (d > 1.0) {
            return TrackedWaypoint.Pitch.UP;
        } else {
            if (bl) {
                if (vec3d.y > 0.0) {
                    return TrackedWaypoint.Pitch.UP;
                }

                if (vec3d.y < 0.0) {
                    return TrackedWaypoint.Pitch.DOWN;
                }
            }

            return TrackedWaypoint.Pitch.NONE;
        }
    }
}
