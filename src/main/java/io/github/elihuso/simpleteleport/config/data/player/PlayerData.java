package io.github.elihuso.simpleteleport.config.data.player;

import io.github.elihuso.simpleteleport.config.data.enums.TeleportType;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class PlayerData {

    @Nullable
    private Location previousLocation;
    private final Map<TeleportType, Boolean> locationRecordingPreferences = new HashMap<>();

    public PlayerData() {
    }

    @Nullable
    public Location getPreviousLocation() {
        return previousLocation;
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
}
