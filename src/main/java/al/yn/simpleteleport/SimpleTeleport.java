package al.yn.simpleteleport;

import al.yn.simpleteleport.command.BackCommand;
import al.yn.simpleteleport.command.HomeCommand;
import al.yn.simpleteleport.command.TeleportCommand;
import al.yn.simpleteleport.command.WorldTeleportCommand;
import al.yn.simpleteleport.config.ConfigManager;
import al.yn.simpleteleport.config.data.DataManager;
import al.yn.simpleteleport.config.memory.MemoryDataManager;
import al.yn.simpleteleport.listener.PlayerLogoutListener;
import al.yn.simpleteleport.listener.PlayerTeleportListener;
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

    private void registerCommands(@NotNull ReloadableRegistrarEvent<@NotNull Commands> event) {
        var commands = event.registrar();
        new BackCommand(configManager, dataManager).register(commands);
        new TeleportCommand(configManager, memoryDataManager).register(commands);
        new WorldTeleportCommand().register(commands);
        new HomeCommand(configManager, dataManager).register(commands);
    }

    @Override
    public void onDisable() {
        dataManager.saveData();
    }
}
