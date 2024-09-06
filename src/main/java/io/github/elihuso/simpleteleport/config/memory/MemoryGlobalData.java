package io.github.elihuso.simpleteleport.config.memory;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MemoryGlobalData {
    /**
     * Table<UUID requester, UUID requested, long expireTime>
     */
    private final Table<UUID, UUID, ZonedDateTime> tpaRequests = HashBasedTable.create();

    /**
     * Table<UUID requester, UUID requested, long expireTime>
     */
    private final Table<UUID, UUID, ZonedDateTime> tphereRequests = HashBasedTable.create();

    public void addTpaRequest(UUID requester, UUID requested, long timeout) {
        var expire = ZonedDateTime.now().plusSeconds(timeout);
        tpaRequests.put(requester, requested, expire);
    }

    public void addTpHereRequest(UUID requester, UUID requested, long timeout) {
        var expire = ZonedDateTime.now().plusSeconds(timeout);
        tphereRequests.put(requester, requested, expire);
    }

    public Set<UUID> getTpaRequested(UUID requested) {
        var now = ZonedDateTime.now();
        return tpaRequests.column(requested).entrySet().stream()
                .filter(e -> now.isBefore(e.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Set<UUID> getTpHereRequested(UUID requested) {
        var now = ZonedDateTime.now();
        return tphereRequests.column(requested).entrySet().stream()
                .filter(e -> now.isBefore(e.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private boolean hasTpRequested(Table<UUID, UUID, ZonedDateTime> table, UUID requester, UUID requested) {
        var row = table.row(requester);
        var now = ZonedDateTime.now();
        return row.containsKey(requested) && now.isBefore(row.get(requested));
    }

    public boolean hasTpaRequested(UUID requester, UUID requested) {
        return hasTpRequested(tpaRequests, requester, requested);
    }

    public boolean hasTpHereRequested(UUID requester, UUID requested) {
        return hasTpRequested(tphereRequests, requester, requested);
    }

    public boolean hasTpRequested(UUID requester, UUID requested) {
        return hasTpaRequested(requester, requested) || hasTpHereRequested(requester, requested);
    }

    public Set<UUID> removeTpRequest(UUID requester) {
        var list = new HashSet<UUID>();
        var now = ZonedDateTime.now();

        for (var entry : tpaRequests.row(requester).entrySet()) {
            if (now.isBefore(entry.getValue())) {
                list.add(entry.getKey());
            }
            tpaRequests.remove(requester, entry.getKey());
        }

        for (var entry : tphereRequests.row(requester).entrySet()) {
            if (now.isBefore(entry.getValue())) {
                list.add(entry.getKey());
            }
            tphereRequests.remove(requester, entry.getKey());
        }

        return list;
    }

    public boolean removeTpRequest(UUID requester, UUID requested) {
        var result = false;
        var expire = tpaRequests.remove(requester, requested);
        if (expire != null && ZonedDateTime.now().isAfter(expire)) {
            result = true;
        }

        expire = tphereRequests.remove(requester, requested);
        if (expire != null && ZonedDateTime.now().isAfter(expire)) {
            result = true;
        }

        return result;
    }
}
