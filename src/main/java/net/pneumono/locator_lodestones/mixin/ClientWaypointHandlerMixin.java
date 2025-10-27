package net.pneumono.locator_lodestones.mixin;

import net.minecraft.client.world.ClientWaypointHandler;
import net.minecraft.world.waypoint.TrackedWaypoint;
import net.pneumono.locator_lodestones.IWaypointAccessor;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.datafixers.util.Either;

@Mixin(ClientWaypointHandler.class)
public abstract class ClientWaypointHandlerMixin implements IWaypointAccessor {
	@Shadow @Final private Map<Either<UUID, String>, TrackedWaypoint> waypoints;

    public Collection<TrackedWaypoint> getWaypointsUnsorted() {
        return this.waypoints.values();
    }
}
