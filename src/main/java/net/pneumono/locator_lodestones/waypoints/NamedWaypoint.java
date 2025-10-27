package net.pneumono.locator_lodestones.waypoints;

import java.util.Optional;
import org.jetbrains.annotations.Nullable;

import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.waypoint.TrackedWaypoint;
import net.minecraft.world.waypoint.Waypoint;
import net.minecraft.world.waypoint.WaypointStyle;

public class NamedWaypoint extends TrackedWaypoint.Positional {
    protected Optional<Text> name = Optional.empty();

    public NamedWaypoint(String source, Config config, Vec3i pos) {
        super(Either.right(source), config, bufFromPos(pos));
    }
    public NamedWaypoint(String source, RegistryKey<WaypointStyle> style, @Nullable Integer color, Vec3i pos) {
        this(source, configFromStyle(style, color), pos);
    }
    public NamedWaypoint(String source, RegistryKey<WaypointStyle> style, @Nullable Integer color, Vec3i pos, Optional<Text> name) {
        this(source, style, color, pos);
        this.name = name;
    }

    public Optional<Text> GetName() { return name; }

    private static Config configFromStyle(RegistryKey<WaypointStyle> style, @Nullable Integer color) {
        Waypoint.Config config = new Waypoint.Config();
        config.style = style;
        config.color = Optional.ofNullable(color);
        return config;
    }
    
    private static PacketByteBuf bufFromPos(Vec3i pos) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(pos.getX());
        buf.writeVarInt(pos.getY());
        buf.writeVarInt(pos.getZ());
        return buf;
    }
}
