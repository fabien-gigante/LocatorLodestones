package net.pneumono.locator_lodestones;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWaypointHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.waypoint.TrackedWaypoint;
import net.minecraft.world.waypoint.Waypoint;
import net.pneumono.locator_lodestones.config.ConfigManager;
import net.pneumono.locator_lodestones.waypoints.DialWaypoint;
import net.pneumono.locator_lodestones.waypoints.MapWaypoint;
import net.pneumono.locator_lodestones.waypoints.NamedWaypoint;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

//? if >=1.21.9 {
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.waypoint.EntityTickProgress;
//?}

public class WaypointRenderer {

    protected static record WaypointMatch(TrackedWaypoint waypoint, double yaw) {}
    protected static WaypointMatch NO_MATCH = new WaypointMatch(null, Double.NaN);
    private static boolean distanceRendered = false;

    protected static WaypointMatch getBestWaypoint(MinecraftClient client, RenderTickCounter tickCounter, Stream<TrackedWaypoint> waypoints) {
        Camera camera = client.gameRenderer.getCamera();
        //? if >=1.21.9 {
        Entity cameraEntity = client.getCameraEntity();
        if (cameraEntity == null) return NO_MATCH;
        World world = cameraEntity.getEntityWorld();
        EntityTickProgress entityTickProgress = (tickedEntity) -> tickCounter.getTickProgress(
                !world.getTickManager().shouldSkipTick(tickedEntity)
        );
        //?}

        return waypoints
            //? if >=1.21.9 {        
            .map(waypoint -> new WaypointMatch(waypoint, waypoint.getRelativeYaw(client.world, camera, entityTickProgress)))
            //?} else {
            /*.map(waypoint -> new WaypointMatch(waypoint, waypoint.getRelativeYaw(client.world, camera)))
            *///?}
            .sorted(Comparator.comparingDouble(match -> Math.abs(match.yaw)))
            .findFirst().orElse(NO_MATCH);
    }

    public static void render(MinecraftClient client, DrawContext context, RenderTickCounter tickCounter, int centerY) {
        distanceRendered = false;
        if (ConfigManager.getConfig().showDistance())
            renderDistance(client, context, tickCounter, centerY);
        if (ConfigManager.getConfig().tabShowsNames() && client.options.playerListKey.isPressed())
            renderNames(client, context, tickCounter, centerY);
    }

    protected static void renderNames(MinecraftClient client, DrawContext context, RenderTickCounter tickCounter, int centerY) {
        WaypointTracker waypointTracker = LocatorLodestones.waypointTracker;
        Stream<TrackedWaypoint> waypoints = waypointTracker.getWaypoints().stream().filter(waypoint -> waypoint instanceof NamedWaypoint);
        WaypointMatch best = getBestWaypoint(client, tickCounter, waypoints);
        if (!(best instanceof WaypointMatch(NamedWaypoint waypoint, double yaw)) ||  Math.abs(yaw) > 60) return;

        Optional<Text> textOptional = waypoint.getName();
        if (textOptional.isPresent()) {
            Text text = textOptional.get();
            TextRenderer textRenderer = client.textRenderer;
            int width = textRenderer.getWidth(text);
            int x = getXFromYaw(context, yaw) - width / 2;
            context.fill(x + 5 - 2, centerY - 10 - 2, x + width + 5 + 2, centerY - 10 + 9 + 2, ColorHelper.withAlpha(0.5F, Colors.BLACK));
            context.drawTextWithShadow(textRenderer, text, x + 5, centerY - 10, Colors.WHITE);
        }
    }

    private static int getXFromYaw(DrawContext context, double relativeYaw) {
        return MathHelper.ceil((context.getScaledWindowWidth() - 9) / 2.0F) + (int)(relativeYaw * 173.0 / 2.0 / 60.0);
    }


    protected static void renderDistance(MinecraftClient client, DrawContext context, RenderTickCounter tickCounter, int centerY) {
        ClientWaypointHandler handler = client.player.networkHandler.getWaypointHandler();
        if (!(handler instanceof IWaypointAccessor accessor)) return;
        Stream<TrackedWaypoint> waypoints = accessor.getWaypointsUnsorted().stream().filter(waypoint -> !(waypoint instanceof DialWaypoint));
        WaypointMatch best = getBestWaypoint(client, tickCounter, waypoints);
        if (best.waypoint == null || Math.abs(best.yaw) > 10) return;

        double dist = Math.sqrt(best.waypoint.squaredDistanceTo(client.player));
        String label = getDistanceShortString(dist);
        if (label.isEmpty()) return;
        
        distanceRendered = true;
        TextRenderer textRenderer = client.textRenderer;
        int width = textRenderer.getWidth(label);
        int x = Math.round((context.getScaledWindowWidth() - width) / 2);
        int y = centerY - 10;

        context.drawText(client.textRenderer, label, x-1, y, Colors.BLACK, false);
        context.drawText(client.textRenderer, label, x+1, y, Colors.BLACK, false);
        context.drawText(client.textRenderer, label, x, y-1, Colors.BLACK, false);
        context.drawText(client.textRenderer, label, x, y+1, Colors.BLACK, false);
        context.drawText(client.textRenderer, label, x, y, getColor(best.waypoint), false);
    }

    private static String getDistanceShortString(double distance) {
        if (Double.isNaN(distance)) return "";
        if (Double.isInfinite(distance)) return "∞";
        if (distance >= 10_000_000) return Math.round(distance / 1_000_000) + "M";
        if (distance >= 1_000_000) return Math.round(distance / 100_000) / 10f + "M";
        if (distance >= 10_000) return Math.round(distance / 1_000) + "k";
        if (distance >= 1_000) return Math.round(distance / 100) / 10f + "k";
        return String.valueOf(Math.round(distance));
    }

    private static int getColor(TrackedWaypoint waypoint) {
        Waypoint.Config config = waypoint.getConfig();
        Optional<Integer> color = config instanceof MapWaypoint.Config mapConfig ? mapConfig.textColor : config.color;
        return color.orElseGet( () -> {
            int hash = waypoint.getSource().map(uuid -> uuid.hashCode(), name -> name.hashCode());
            return ColorHelper.withBrightness(ColorHelper.withAlpha(255, hash), 0.9F);
        });
    }

    public static boolean shouldDrawExperienceLevel() { return !distanceRendered; }
}
