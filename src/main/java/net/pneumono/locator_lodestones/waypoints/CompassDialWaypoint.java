package net.pneumono.locator_lodestones.waypoints;

import java.util.Optional;

import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
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

public class CompassDialWaypoint extends TrackedWaypoint.Azimuth {
    public CompassDialWaypoint(String source, Config config, float azimuth) {
        super(Either.right(source), config, bufFromFloat(azimuth));
    }
    
    public CompassDialWaypoint(String source, RegistryKey<WaypointStyle> style, float azimuth) {
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

    private static Config configFromStyle(RegistryKey<WaypointStyle> style) {
        Waypoint.Config config = new Waypoint.Config();
        config.style = style;
        config.color = Optional.of(ConfigManager.getDialColor().getColorWithAlpha());
        return config;
    }
    
    private static PacketByteBuf bufFromFloat(float f) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(f);
        return buf;
    }
}
