package io.github.elihuso.simpleteleport.config;

import io.github.elihuso.simpleteleport.config.data.enums.TeleportType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigManager {
    private final FileConfiguration config;

    public ConfigManager(Plugin plugin) {
        plugin.saveDefaultConfig();

        config = plugin.getConfig();
    }

    public boolean getBackEnablePlayerCustomPreference() {
        return config.getBoolean("back.recording-preference.player-custom", true);
    }

    public boolean getBackPreferenceDefault(TeleportType type) {
        return switch (type) {
            case GAME_PLAY:
                yield config.getBoolean("back.recording-preference.default.game-play", false);
            case ENDER_TELEPORT:
                yield config.getBoolean("back.recording-preference.default.ender-teleport", false);
            case PORTALS:
                yield config.getBoolean("back.recording-preference.default.portals", false);
            case DEATH:
                yield config.getBoolean("back.recording-preference.default.death", true);
            case SYSTEM:
                yield config.getBoolean("back.recording-preference.default.system", true);
            case OTHER:
                yield config.getBoolean("back.recording-preference.default.other", false);
        };
    }

    public long getTpaTimeout() {
        return config.getInt("tpa.timeout", 30);
    }
}
