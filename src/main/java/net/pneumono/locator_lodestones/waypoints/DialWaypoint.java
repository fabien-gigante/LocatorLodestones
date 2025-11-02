package net.pneumono.locator_lodestones.waypoints;

import java.util.Optional;

import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
//? if >=1.21.9 {
import net.minecraft.world.waypoint.EntityTickProgress;
//?}
import net.minecraft.world.waypoint.TrackedWaypoint;
import net.minecraft.world.waypoint.Waypoint;
import net.minecraft.world.waypoint.WaypointStyle;
import net.pneumono.locator_lodestones.config.ConfigManager;

public class DialWaypoint extends TrackedWaypoint.Azimuth {
    protected DialWaypoint(String source, Config config, float azimuth) {
        super(Either.right(source), config, bufFromFloat(azimuth));
    }
    
    public DialWaypoint(String source, RegistryKey<WaypointStyle> style, float azimuth) {
        this(source, configFromStyle(style), azimuth);
    }

    @Override
    //? if >=1.21.9 {
    public TrackedWaypoint.Pitch getPitch(World world, TrackedWaypoint.PitchProvider cameraProvider, EntityTickProgress tickProgress) {
    //?} else {
    /*public TrackedWaypoint.Pitch getPitch(World world, TrackedWaypoint.PitchProvider cameraProvider) {
    *///?}
        return Pitch.NONE;
    }
    
    @Override
    public double squaredDistanceTo(Entity receiver) {
        // greater than Double.POSITIVE_INFINITY, so that it always renders in the back
        return Double.NaN; 
    }

    private static Config configFromStyle(RegistryKey<WaypointStyle> style) {
        Waypoint.Config config = new Waypoint.Config();
        config.style = style;
        config.color = Optional.of(ConfigManager.getConfig().colors().dialColor().getColorWithAlpha(160));
        return config;
    }
    
    private static PacketByteBuf bufFromFloat(float f) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(f);
        return buf;
    }
}
