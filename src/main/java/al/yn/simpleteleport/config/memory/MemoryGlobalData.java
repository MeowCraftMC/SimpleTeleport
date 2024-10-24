package al.yn.simpleteleport.config.memory;

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
                .filter(e -> isAvailable(now, e.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Set<UUID> getTpHereRequested(UUID requested) {
        var now = ZonedDateTime.now();
        return tphereRequests.column(requested).entrySet().stream()
                .filter(e -> isAvailable(now, e.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private boolean hasTpRequested(Table<UUID, UUID, ZonedDateTime> table, UUID requester, UUID requested) {
        var row = table.row(requester);
        return row.containsKey(requested) && isAvailable(row.get(requested));
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
            if (isAvailable(now, entry.getValue())) {
                list.add(entry.getKey());
            }
            tpaRequests.remove(requester, entry.getKey());
        }

        for (var entry : tphereRequests.row(requester).entrySet()) {
            if (isAvailable(now, entry.getValue())) {
                list.add(entry.getKey());
            }
            tphereRequests.remove(requester, entry.getKey());
        }

        return list;
    }

    public boolean removeTpRequest(UUID requester, UUID requested) {
        var now = ZonedDateTime.now();
        var result = false;
        var expire = tpaRequests.remove(requester, requested);
        if (expire != null && isAvailable(now, expire)) {
            result = true;
        }

        expire = tphereRequests.remove(requester, requested);
        if (expire != null && isAvailable(now, expire)) {
            result = true;
        }

        return result;
    }

    private boolean isAvailable(ZonedDateTime expire) {
        return isAvailable(ZonedDateTime.now(), expire);
    }

    private boolean isAvailable(ZonedDateTime now, ZonedDateTime expire) {
        return now.isBefore(expire);
    }
}
