package io.github.elihuso.simpleteleport.utility;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class TeleportHelper {
    public static boolean teleportTo(Entity entity, Location location) {
        if (entity.isInsideVehicle()) {
            if (!entity.leaveVehicle()) {
                return false;
            }
        }

        for (var passenger : entity.getPassengers()) {
            if (!entity.removePassenger(passenger)) {
                return false;
            }
        }

        return entity.teleport(location);
    }
}
