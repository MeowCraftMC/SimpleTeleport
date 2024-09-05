package io.github.elihuso.simpleteleport.config.data;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;
import io.github.elihuso.simpleteleport.config.data.player.PlayerData;
import io.github.elihuso.simpleteleport.config.data.player.PlayerDataLoader;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

import java.io.File;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class DataManager {

    private final Logger logger;

    private final PlayerDataLoader loader;
    private final LoadingCache<UUID, PlayerData> storage;

    public DataManager(File dataFolder, Logger logger) {
        this.logger = logger;

        this.loader = new PlayerDataLoader(new File(dataFolder, "data"), logger);
        this.storage = CacheBuilder.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(30))
                .removalListener(this::onRemoval)
                .build(loader);
    }

    private void onRemoval(RemovalNotification<UUID, PlayerData> notification) {
        loader.save(Objects.requireNonNull(notification.getKey()), Objects.requireNonNull(notification.getValue()));
    }

    public void saveData() {
        storage.invalidateAll();
    }

    public PlayerData getPlayerData(Player player) {
        try {
            return storage.get(player.getUniqueId());
        } catch (ExecutionException ex) {
            logger.error("Exception while get data of {}", player, ex);
            throw new RuntimeException(ex);
        }
    }
}
