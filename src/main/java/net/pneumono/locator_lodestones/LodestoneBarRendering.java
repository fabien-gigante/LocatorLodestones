package net.pneumono.locator_lodestones;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.resource.waypoint.WaypointStyleAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
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

        List<BlockPos> lodestones = LocatorLodestones.getLodestones(client.player);

        lodestones.stream().sorted(
                Comparator.comparingDouble(pos -> pos.getSquaredDistance(client.cameraEntity.getPos()))
        ).forEachOrdered(pos -> renderLodestone(client, context, centerY, pos));
    }

    private static void renderLodestone(MinecraftClient client, DrawContext context, int centerY, BlockPos pos) {
        if (client.player == null || client.cameraEntity == null) return;

        double relativeYaw = getRelativeYaw(pos, client.gameRenderer.getCamera());
        if (relativeYaw <= -61.0 || relativeYaw > 60.0)  return;

        int j = MathHelper.ceil((context.getScaledWindowWidth() - 9) / 2.0F);
        Waypoint.Config config = new Waypoint.Config();
        config.style = RegistryKey.of(WaypointStyles.REGISTRY, LocatorLodestones.id("lodestone"));

        WaypointStyleAsset waypointStyleAsset = client.getWaypointStyleAssetManager().get(config.style);
        Identifier identifier = waypointStyleAsset.getSpriteForDistance(
                (float) Math.sqrt(pos.getSquaredDistance(client.cameraEntity.getPos()))
        );
        int color = config.color.orElseGet(() -> ColorHelper.withBrightness(
                ColorHelper.withAlpha(255, pos.toString().hashCode()), 0.9F
        ));

        int l = (int)(relativeYaw * 173.0 / 2.0 / 60.0);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, j + l, centerY - 2, 9, 9, color);
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

            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, texture, j + l + 1, centerY + yOffset, 7, 5);
        }
    }

    private static double getRelativeYaw(BlockPos pos, TrackedWaypoint.YawProvider yawProvider) {
        Vec3d vec3d = yawProvider.getCameraPos().subtract(new Vec3d(pos.getX(), pos.getY(), pos.getZ())).rotateYClockwise();
        float f = (float)MathHelper.atan2(vec3d.getZ(), vec3d.getX()) * (180.0F / (float)Math.PI);
        return MathHelper.subtractAngles(yawProvider.getCameraYaw(), f);
    }

    private static TrackedWaypoint.Pitch getPitch(BlockPos pos, TrackedWaypoint.PitchProvider cameraProvider) {
        Vec3d vec3d = cameraProvider.project(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
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
