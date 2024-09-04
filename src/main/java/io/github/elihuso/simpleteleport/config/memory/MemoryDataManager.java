package io.github.elihuso.simpleteleport.config.memory;

import io.github.elihuso.simpleteleport.SimpleTeleport;

public class MemoryDataManager {

    private final SimpleTeleport plugin;
    private final MemoryGlobalData globalData = new MemoryGlobalData();

    public MemoryDataManager(SimpleTeleport plugin) {
        this.plugin = plugin;
    }

    public MemoryGlobalData getGlobalData() {
        return globalData;
    }
}
