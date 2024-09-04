package io.github.elihuso.simpleteleport.listener;

import io.github.elihuso.simpleteleport.SimpleTeleport;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener implements Listener {
    private final SimpleTeleport plugin;

    public PlayerTeleportListener(SimpleTeleport plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) {
            return;
        }

        switch (event.getCause()) {
            case DISMOUNT, EXIT_BED, UNKNOWN, SPECTATE, ENDER_PEARL, CHORUS_FRUIT, NETHER_PORTAL, END_PORTAL, END_GATEWAY:
                return;
        }

        var player = event.getPlayer();
        var from = event.getFrom();
        plugin.getDataManager().saveLocation(player.getUniqueId(), from);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        var player = event.getPlayer();
        var location = event.getPlayer().getLocation();
        plugin.getDataManager().saveLocation(player.getUniqueId(), location);
    }
}
