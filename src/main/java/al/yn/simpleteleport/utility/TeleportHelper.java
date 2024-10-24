package al.yn.simpleteleport.utility;

import io.papermc.paper.entity.TeleportFlag;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class TeleportHelper {
    public static boolean teleportTo(Entity entity, Location location) {
        if (entity.isInsideVehicle()) {
            if (!entity.leaveVehicle()) {
                return false;
            }
        }

        if (!entity.getWorld().equals(location.getWorld())) {
            for (var passenger : entity.getPassengers()) {
                if (!entity.removePassenger(passenger)) {
                    return false;
                }
            }
        }

        return entity.teleport(location, TeleportFlag.EntityState.RETAIN_PASSENGERS);
    }

    public static boolean teleportTo(Entity entity, Entity target) {
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

        return entity.teleport(target);
    }
}
