package io.github.elihuso.simpleteleport.config.data;

import io.github.elihuso.simpleteleport.SimpleTeleport;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerDataManager {

    private final SimpleTeleport plugin;
    private final File dataDir;

    public PlayerDataManager(SimpleTeleport plugin) {
        this.plugin = plugin;
        this.dataDir = new File(plugin.getDataFolder(), "data");
    }

    private File getPlayerDataFile(UUID uuid) {
        return new File(dataDir, uuid.toString() + ".yml");
    }

    private YamlConfiguration getPlayerData(UUID uuid) {
        var file = getPlayerDataFile(uuid);
        if (file.exists()) {
            var config = new YamlConfiguration();
            try {
                config.load(file);
            } catch (IOException | InvalidConfigurationException ex) {
                plugin.getSLF4JLogger().error("Couldn't load player data {}", uuid, ex);
            }
            return config;
        } else {
            return new YamlConfiguration();
        }
    }

    private void savePlayerData(UUID uuid, YamlConfiguration config) {
        var file = getPlayerDataFile(uuid);
        try {
            config.save(file);
        } catch (IOException ex) {
            plugin.getSLF4JLogger().error("Couldn't save player data {}", uuid, ex);
        }
    }

    public void saveLocation(UUID uuid, Location location) {
        var config = getPlayerData(uuid);
        config.set("previous-location", location);
        savePlayerData(uuid, config);
    }

    public Location getLocation(UUID uuid) {
        var config = getPlayerData(uuid);
        return config.getLocation("previous-location");
    }
}
