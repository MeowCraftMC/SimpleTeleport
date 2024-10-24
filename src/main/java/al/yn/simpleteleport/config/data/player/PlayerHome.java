package al.yn.simpleteleport.config.data.player;

import org.bukkit.Location;

public record PlayerHome(Location location, boolean isPublic) {
}
