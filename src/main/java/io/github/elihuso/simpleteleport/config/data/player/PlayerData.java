package io.github.elihuso.simpleteleport.config.data.player;

import io.github.elihuso.simpleteleport.config.data.enums.TeleportType;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ConfigSerializable
public class PlayerData {

    private PlayerDataLoader loader;
    private UUID uuid;

    @Setting
    @Nullable
    private Location previousLocation;
    @Setting
    private final Map<TeleportType, Boolean> locationRecordingPreferences = new HashMap<>();

    protected void setLoader(PlayerDataLoader loader, UUID uuid) {
        this.loader = loader;
        this.uuid = uuid;
    }

    public void save() {
        loader.save(uuid, this);
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
