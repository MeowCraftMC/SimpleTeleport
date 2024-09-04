package io.github.elihuso.simpleteleport.config.memory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Table;

import java.util.UUID;

public class MemoryGlobalData {
    /**
     * BiMap<UUID requester, UUID requested>
     */
    private final BiMap<UUID, UUID> teleportRequests = HashBiMap.create();

    /**
     * Table<UUID requester, UUID requested, long timeout>
     */
    private final Table<UUID, UUID, Long> teleportRequestTimeouts = HashBasedTable.create();


}
