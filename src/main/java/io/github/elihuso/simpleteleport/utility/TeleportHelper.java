package io.github.elihuso.simpleteleport.utility;

import io.papermc.paper.entity.TeleportFlag;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class TeleportHelper {
    public static boolean teleportTo(Entity entity, Location location) {
        return entity.teleport(location, TeleportFlag.EntityState.RETAIN_PASSENGERS);
    }
}
