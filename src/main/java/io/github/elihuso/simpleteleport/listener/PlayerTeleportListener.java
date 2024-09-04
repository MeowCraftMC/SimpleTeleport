package io.github.elihuso.simpleteleport.listener;

import io.github.elihuso.simpleteleport.SimpleTeleport;
import io.github.elihuso.simpleteleport.config.data.enums.TeleportType;
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

        var type = TeleportType.fromBukkit(event.getCause());

        var player = event.getPlayer();
        var from = event.getFrom();
        var data = plugin.getDataManager().getPlayerData(player);
        // Todo: defValue from config.
        if (data.shouldRecordLocation(type, type == TeleportType.SYSTEM)) {
            plugin.getDataManager().getPlayerData(player).setPreviousLocation(from);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        var player = event.getPlayer();
        var location = event.getPlayer().getLocation();
        var data = plugin.getDataManager().getPlayerData(player);
        // Todo: defValue from config.
        if (data.shouldRecordLocation(TeleportType.DEATH, true)) {
            plugin.getDataManager().getPlayerData(player).setPreviousLocation(location);
        }
    }
}
