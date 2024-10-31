package al.yn.simpleteleport.config.data.player;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import al.yn.simpleteleport.config.data.TeleportType;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.*;

@ConfigSerializable
public class PlayerData {

    private PlayerDataLoader loader;
    private UUID uuid;

    @Setting
    @Nullable
    private Location previousLocation;
    @Setting
    private final Map<TeleportType, Boolean> locationRecordingPreferences = new HashMap<>();

    @Setting
    private final Map<String, Location> homes = new HashMap<>();

    protected void setLoader(PlayerDataLoader loader, UUID uuid) {
        this.loader = loader;
        this.uuid = uuid;
    }

    public void save() {
        loader.save(uuid, this);
    }

    @Nullable
    public Location getPreviousLocation() {
        return previousLocation != null ? previousLocation.clone() : null;
    }

    public void setPreviousLocation(@NotNull Location previousLocation) {
        this.previousLocation = previousLocation;
    }

    public void addLocationRecordingPreference(TeleportType cause, boolean allow) {
        locationRecordingPreferences.put(cause, allow);
    }

    public void removeLocationRecordingPreference(TeleportType cause) {
        locationRecordingPreferences.remove(cause);
    }

    public void clearLocationRecordingPreference() {
        locationRecordingPreferences.clear();
    }

    public boolean shouldRecordLocation(TeleportType cause, boolean defValue) {
        if (locationRecordingPreferences.containsKey(cause)) {
            return locationRecordingPreferences.get(cause);
        }

        return defValue;
    }

    public Map<String, Location> getHomes() {
        var builder = ImmutableMap.<String, Location>builder();
        for (var entry : homes.entrySet()) {
            builder.put(entry.getKey(), entry.getValue().clone());
        }
        return builder.build();
    }

    public void addHome(String name, Location home) {
        homes.put(name, home.clone());
    }

    public void delHome(String name) {
        homes.remove(name);
    }

    public Location getHome(String name) {
        return homes.get(name).clone();
    }
}
