package io.github.elihuso.simpleteleport.config.data;

import com.google.common.cache.CacheLoader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataLoader extends CacheLoader<UUID, PlayerData> {

    private final Map<UUID, GsonConfigurationLoader> loaders = new HashMap<>();
    private final File dataDir;
    private final Logger logger;

    public PlayerDataLoader(File dataDir, Logger logger) {
        this.dataDir = dataDir;
        this.logger = logger;
    }

    private GsonConfigurationLoader getPlayerDataLoader(UUID uuid) {
        return loaders.computeIfAbsent(uuid, u -> GsonConfigurationLoader.builder()
                .file(new File(dataDir, u.toString() + ".yml"))
                .build());
    }

    @Override
    public @NotNull PlayerData load(@NotNull UUID key) {
        var loader = getPlayerDataLoader(key);
        if (loader.canLoad()) {
            try {
                var data = loader.load().get(PlayerData.class);
                if (data != null) {
                    return data;
                }
            } catch (ConfigurateException ex) {
                logger.error("Couldn't load player data {}", key, ex);
            }
        }

        var data = new PlayerData();
        save(key, data);
        return data;
    }

    public void save(@NotNull UUID key, @NotNull PlayerData value) {
        var loader = getPlayerDataLoader(key);
        try {
            var root = loader.createNode().set(value);
            loader.save(root);
        } catch (ConfigurateException ex) {
            logger.error("Couldn't save player data {}", key, ex);
        }
    }
}
