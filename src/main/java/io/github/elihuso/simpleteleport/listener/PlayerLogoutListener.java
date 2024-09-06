package io.github.elihuso.simpleteleport.listener;

import io.github.elihuso.simpleteleport.config.memory.MemoryDataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLogoutListener implements Listener {

    private final MemoryDataManager memoryDataManager;

    public PlayerLogoutListener(MemoryDataManager memoryDataManager) {
        this.memoryDataManager = memoryDataManager;
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
        var globalData = memoryDataManager.getGlobalData();

        var player = event.getPlayer();
        globalData.removeTpRequest(player.getUniqueId());
    }
}
