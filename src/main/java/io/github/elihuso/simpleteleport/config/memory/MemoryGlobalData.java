package io.github.elihuso.simpleteleport.config.memory;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.time.ZonedDateTime;
import java.util.*;

public class MemoryGlobalData {
    /**
     * Table<UUID requester, UUID requested, long expireTime>
     */
    private final Table<UUID, UUID, ZonedDateTime> requestTimeouts = HashBasedTable.create();

    public void addRequest(UUID requester, UUID requested, long timeout) {
        var expire = ZonedDateTime.now().plusSeconds(timeout);
        requestTimeouts.put(requester, requested, expire);
    }

    public void removeRequest(UUID requester, UUID requested) {
        requestTimeouts.remove(requester, requested);
    }

    public boolean isRequesting(UUID requester) {
        return !requestTimeouts.row(requester).isEmpty();
    }

    public boolean wasRequested(UUID requested) {
        return !requestTimeouts.column(requested).isEmpty();
    }

    public boolean hasRequested(UUID requester, UUID requested) {
        var row = requestTimeouts.row(requester);
        var now = ZonedDateTime.now();
        if (row.containsKey(requested)) {
            if (row.get(requested).isBefore(now)) {
                requestTimeouts.remove(requester, requested);
                return false;
            } else {
                return true;
            }
        }

        return false;
    }
}
