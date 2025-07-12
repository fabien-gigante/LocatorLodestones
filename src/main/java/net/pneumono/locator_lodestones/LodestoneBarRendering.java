package net.pneumono.locator_lodestones;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.resource.waypoint.WaypointStyleAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.waypoint.TrackedWaypoint;
import net.minecraft.world.waypoint.Waypoint;
import net.minecraft.world.waypoint.WaypointStyles;

import java.util.Comparator;
import java.util.List;

public class LodestoneBarRendering {
    private static final Identifier ARROW_UP = Identifier.ofVanilla("hud/locator_bar_arrow_up");
    private static final Identifier ARROW_DOWN = Identifier.ofVanilla("hud/locator_bar_arrow_down");

    public static void renderLodestoneWaypoints(MinecraftClient client, DrawContext context, int centerY) {
        if (client.player == null || client.cameraEntity == null) return;

        List<BlockPos> lodestones = LocatorLodestones.getLodestonePositions(client.player);

        lodestones.stream().map(
                blockPos -> new Vec3d(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5)
        ).sorted(
                Comparator.comparingDouble(pos -> pos.squaredDistanceTo(client.cameraEntity.getPos()))
        ).forEachOrdered(pos -> renderLodestoneWaypoint(
                client, context, centerY, pos
        ));
    }

    private static void renderLodestoneWaypoint(MinecraftClient client, DrawContext context, int centerY, Vec3d pos) {
        if (client.player == null || client.cameraEntity == null) return;

        double relativeYaw = getRelativeYaw(pos, client.gameRenderer.getCamera());
        if (relativeYaw <= -61.0 || relativeYaw > 60.0)  return;

        Waypoint.Config config = new Waypoint.Config();
        config.style = RegistryKey.of(WaypointStyles.REGISTRY, LocatorLodestones.id("lodestone"));

        WaypointStyleAsset waypointStyleAsset = client.getWaypointStyleAssetManager().get(config.style);
        Identifier identifier = waypointStyleAsset.getSpriteForDistance(
                (float) Math.sqrt(pos.squaredDistanceTo(client.cameraEntity.getPos()))
        );
        int color = config.color.orElseGet(() -> ColorHelper.withBrightness(
                ColorHelper.withAlpha(255, pos.toString().hashCode()), 0.9F
        ));

        int x = MathHelper.ceil((context.getScaledWindowWidth() - 9) / 2.0F) + (int)(relativeYaw * 173.0 / 2.0 / 60.0);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, x, centerY - 2, 9, 9, color);

        TrackedWaypoint.Pitch pitch = getPitch(pos, client.gameRenderer);
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
