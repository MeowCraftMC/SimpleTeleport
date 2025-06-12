package al.yn.simpleteleport.utility;

import io.papermc.paper.entity.TeleportFlag;
import org.bukkit.Location;
import org.bukkit.block.Block;
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

    public static boolean isSafePlace(Location loc) {
        var current = loc.getBlock();
        var above = loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ());
        var below = loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());
        return isEmpty(current) && isEmpty(above) && !isEmpty(below);
    }

    private static boolean isEmpty(Block b) {
        return b.isEmpty() || (!b.isCollidable() && !b.isLiquid());
    }
}
