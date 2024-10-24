package al.yn.simpleteleport.listener;

import al.yn.simpleteleport.config.ConfigManager;
import al.yn.simpleteleport.config.data.DataManager;
import al.yn.simpleteleport.config.data.TeleportType;
import al.yn.simpleteleport.config.data.player.PlayerData;
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

        if (shouldRecord(data, type)) {
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
        if (shouldRecord(data, type)) {
            data.setPreviousLocation(location);
            data.save();
        }
    }

    private boolean shouldRecord(PlayerData data, TeleportType type) {
        var def = configManager.getBackPreferenceDefault(type);
        if (configManager.getBackEnablePlayerCustomPreference()) {
            return data.shouldRecordLocation(type, def);
        } else {
            return def;
        }
    }
}
