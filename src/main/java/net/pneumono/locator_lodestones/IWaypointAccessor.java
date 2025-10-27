package net.pneumono.locator_lodestones;

import java.util.Collection;

import net.minecraft.world.waypoint.TrackedWaypoint;

public interface IWaypointAccessor {
    public Collection<TrackedWaypoint> getWaypointsUnsorted();
}