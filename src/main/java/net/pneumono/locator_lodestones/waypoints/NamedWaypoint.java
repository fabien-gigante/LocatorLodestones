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
import net.pneumono.locator_lodestones.INamed;

public class NamedWaypoint extends TrackedWaypoint.Positional implements INamed {
    protected Optional<Text> name = Optional.empty();

    protected NamedWaypoint(String source, Config config, Vec3i pos, Optional<Text> name) {
        super(Either.right(source), config, bufFromPos(pos));
        this.name = name;
    }
    public NamedWaypoint(String source, RegistryKey<WaypointStyle> style, @Nullable Integer color, Vec3i pos, Optional<Text> name) {
        this(source, configFromStyle(style, color), pos, name);
    }

    @Override
    public Optional<Text> getName() { return name; }
    @Override
    public void setName(Optional<Text> name) { this.name = name; }

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
