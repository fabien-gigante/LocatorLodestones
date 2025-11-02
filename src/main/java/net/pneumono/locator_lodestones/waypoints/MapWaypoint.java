package net.pneumono.locator_lodestones.waypoints;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
//? if >=1.21.9 {
import net.minecraft.world.waypoint.EntityTickProgress;
//?}
import net.minecraft.world.waypoint.TrackedWaypoint;
import net.minecraft.world.waypoint.Waypoint;
import net.minecraft.world.waypoint.WaypointStyle;
import net.pneumono.locator_lodestones.MapWaypointStyleAssets;
import net.minecraft.component.type.MapDecorationsComponent.Decoration;

public class MapWaypoint extends NamedWaypoint {
    protected MapWaypoint(String source, RegistryKey<WaypointStyle> style, @Nullable Integer color, Vec3i pos, Optional<Text> name) {
        super(source, configFromStyle(style, color), pos, name);
    }
    public MapWaypoint(String source, Decoration deco, Optional<Text> name) {
        this(source, MapWaypointStyleAssets.getStyle(deco.type()), MapWaypointStyleAssets.getColor(deco.type()),
            new Vec3i((int)Math.floor(deco.x()), 0, (int)Math.floor(deco.z())), name);
    }

    @Override
    public double squaredDistanceTo(Entity receiver) {
        Vec3d pos = Vec3d.ofCenter(this.pos);
        double x = receiver.getX() - pos.x, z = receiver.getZ() - pos.z;
        return x*x + z*z;
    }

    @Override
    //? if >=1.21.9 {
    public TrackedWaypoint.Pitch getPitch(World world, TrackedWaypoint.PitchProvider cameraProvider, EntityTickProgress tickProgress) {
    //?} else {
    /*public TrackedWaypoint.Pitch getPitch(World world, TrackedWaypoint.PitchProvider cameraProvider) {
    *///?}
        return Pitch.NONE;
    }

    public static class Config extends Waypoint.Config {
        public Optional<Integer> textColor = Optional.empty();
    }

    private static Config configFromStyle(RegistryKey<WaypointStyle> style, @Nullable Integer color) {
        Config config = new Config();
        config.style = style;
        config.color = Optional.of(Colors.WHITE);
        config.textColor = color == -1 ? Optional.empty() : Optional.of(ColorHelper.withAlpha(255, color));
        return config;
    }
}