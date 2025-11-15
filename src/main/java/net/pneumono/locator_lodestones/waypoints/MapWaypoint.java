package net.pneumono.locator_lodestones.waypoints;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.item.map.MapDecoration;
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
import net.pneumono.locator_lodestones.IDecorationExt;
import net.pneumono.locator_lodestones.MapWaypointStyleAssets;
import net.minecraft.component.type.MapDecorationsComponent.Decoration;

public class MapWaypoint extends NamedWaypoint {
    private boolean hasY = false;

    protected MapWaypoint(String source, RegistryKey<WaypointStyle> style, @Nullable Integer color, Vec3i pos, Optional<Text> name) {
        super(source, configFromStyle(style, color), pos, name);
    }
    public MapWaypoint(String source, Decoration deco, Optional<Text> name) {
        this(source, MapWaypointStyleAssets.getStyle(deco.type()), MapWaypointStyleAssets.getColor(deco.type()), getPosOf(deco), name);
        hasY =  (Object)deco instanceof IDecorationExt ext && ext.getY().isPresent();
    }
    public MapWaypoint(String source, MapDecoration deco, Vec3i pos) {
        this(source, MapWaypointStyleAssets.getStyle(deco.type()), MapWaypointStyleAssets.getColor(deco.type()), pos, deco.name());
    }
    
    private static Vec3i getPosOf(Decoration deco) {
        double x = Math.floor(deco.x());
        double y = (Object)deco instanceof IDecorationExt ext ? Math.floor(ext.getY().orElse(0d)) : 0d;
        double z = Math.floor(deco.z());
        return new Vec3i((int)x, (int)y, (int)z);
    }

    @Override
    public double squaredDistanceTo(Entity receiver) {
        if (hasY) return super.squaredDistanceTo(receiver);
        Vec3d pos = Vec3d.ofCenter(this.pos);
        double x = receiver.getX() - pos.x, z = receiver.getZ() - pos.z;
        return x*x + z*z;
    }

    @Override
    //? if >=1.21.9 {
    public TrackedWaypoint.Pitch getPitch(World world, TrackedWaypoint.PitchProvider cameraProvider, EntityTickProgress tickProgress) {
        return hasY ? super.getPitch(world, cameraProvider, tickProgress) : Pitch.NONE;
    }
    //?} else {
    /*public TrackedWaypoint.Pitch getPitch(World world, TrackedWaypoint.PitchProvider cameraProvider) {
        return hasY ? super.getPitch(world, cameraProvider) : Pitch.NONE;
    }
    *///?}

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