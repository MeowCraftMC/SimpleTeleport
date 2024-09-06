package io.github.elihuso.simpleteleport;

import io.github.elihuso.simpleteleport.command.BackCommand;
import io.github.elihuso.simpleteleport.command.TeleportCommand;
import io.github.elihuso.simpleteleport.command.WorldTeleportCommand;
import io.github.elihuso.simpleteleport.config.ConfigManager;
import io.github.elihuso.simpleteleport.config.data.DataManager;
import io.github.elihuso.simpleteleport.config.memory.MemoryDataManager;
import io.github.elihuso.simpleteleport.listener.PlayerLogoutListener;
import io.github.elihuso.simpleteleport.listener.PlayerTeleportListener;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public final class SimpleTeleport extends JavaPlugin {

    private static SimpleTeleport INSTANCE;

    private final ConfigManager configManager;
    private final DataManager dataManager;
    private final MemoryDataManager memoryDataManager;

    public SimpleTeleport() {
        INSTANCE = this;

        configManager = new ConfigManager(this);
        dataManager = new DataManager(getDataFolder(), getSLF4JLogger());
        memoryDataManager = new MemoryDataManager(this);
    }

    public static SimpleTeleport getInstance() {
        return INSTANCE;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public MemoryDataManager getMemoryDataManager() {
        return memoryDataManager;
    }

    @Override
    public void onEnable() {
        var manager = getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, this::registerCommands);

        getServer().getPluginManager().registerEvents(new PlayerTeleportListener(configManager, dataManager), this);
        getServer().getPluginManager().registerEvents(new PlayerLogoutListener(memoryDataManager), this);
    }

    private void registerCommands(@NotNull ReloadableRegistrarEvent<Commands> event) {
        var commands = event.registrar();
        new BackCommand(configManager, dataManager).register(commands);
        new TeleportCommand(configManager, memoryDataManager).register(commands);
        new WorldTeleportCommand().register(commands);
    }

    @Override
    public void onDisable() {
        dataManager.saveData();
    }
}
