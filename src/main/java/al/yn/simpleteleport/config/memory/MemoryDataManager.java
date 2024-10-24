package al.yn.simpleteleport.config.memory;

import al.yn.simpleteleport.SimpleTeleport;

public class MemoryDataManager {

    private final MemoryGlobalData globalData = new MemoryGlobalData();

    public MemoryDataManager(SimpleTeleport plugin) {
    }

    public MemoryGlobalData getGlobalData() {
        return globalData;
    }
}
