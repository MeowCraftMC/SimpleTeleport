package io.github.elihuso.simpleteleport.listener;

import io.github.elihuso.simpleteleport.config.ConfigManager;
import io.github.elihuso.simpleteleport.config.data.DataManager;
import io.github.elihuso.simpleteleport.config.data.enums.TeleportType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener implements Listener {
    private final ConfigManager configManager;
    private final DataManager dataManager;

    public PlayerTeleportListener(ConfigManager configManager, DataManager dataManager) {
        this.configManager = configManager;
        this.dataManager = dataManager;
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) {
            return;
        }

        var player = event.getPlayer();
        var location = event.getFrom();
        var type = TeleportType.fromBukkit(event.getCause());
        var data = dataManager.getPlayerData(player);

        if ((configManager.getBackPlayerCustomPreference() && data.shouldRecordLocation(type, configManager.getBackPreferenceDefault(type)))
                || configManager.getBackPreferenceDefault(type)) {
            data.setPreviousLocation(location);
            data.save();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        var player = event.getPlayer();
        var location = event.getPlayer().getLocation();
        var data = dataManager.getPlayerData(player);
        var type = TeleportType.DEATH;
        if ((configManager.getBackPlayerCustomPreference() && data.shouldRecordLocation(type, configManager.getBackPreferenceDefault(type)))
                || configManager.getBackPreferenceDefault(type)) {
            data.setPreviousLocation(location);
            data.save();
        }
    }
}
