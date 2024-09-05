package io.github.elihuso.simpleteleport.config.memory;

import io.github.elihuso.simpleteleport.SimpleTeleport;

public class MemoryDataManager {

    private final MemoryGlobalData globalData = new MemoryGlobalData();

    public MemoryDataManager(SimpleTeleport plugin) {
    }

    public MemoryGlobalData getGlobalData() {
        return globalData;
    }
}
