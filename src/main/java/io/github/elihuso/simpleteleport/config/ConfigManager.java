package io.github.elihuso.simpleteleport.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigManager {
    private final FileConfiguration config;

    public ConfigManager(Plugin plugin) {
        plugin.saveDefaultConfig();

        config = plugin.getConfig();
    }

    public boolean ListenDeath() {
        return config.getBoolean("listenDeath", true);
    }

    public int Await() {
        return config.getInt("await", 5);
    }
}
