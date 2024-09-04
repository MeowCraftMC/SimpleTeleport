package io.github.elihuso.simpleteleport;

import io.github.elihuso.simpleteleport.command.BackCommand;
import io.github.elihuso.simpleteleport.config.ConfigManager;
import io.github.elihuso.simpleteleport.config.data.PlayerDataManager;
import io.github.elihuso.simpleteleport.listener.PlayerLogoutListener;
import io.github.elihuso.simpleteleport.listener.PlayerTeleportListener;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public final class SimpleTeleport extends JavaPlugin {

    private static SimpleTeleport INSTANCE;

    private final PlayerDataManager dataManager;

    public static ArrayList<String[]> req = new ArrayList<>();
    public static ArrayList<UUID> progynova = new ArrayList<>();
    private final ConfigManager configManager;
    int await = 5;

    public SimpleTeleport() {
        INSTANCE = this;

        dataManager = new PlayerDataManager(this);

        configManager = new ConfigManager(this);
    }

    public static SimpleTeleport getInstance() {
        return INSTANCE;
    }

    public PlayerDataManager getDataManager() {
        return dataManager;
    }

    @Override
    public void onEnable() {
        var manager = getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, this::registerCommands);

        getServer().getPluginManager().registerEvents(new PlayerTeleportListener(this), this);

        // Plugin startup logic
        if (configManager.ListenDeath()) Bukkit.getPluginManager().registerEvents(new PlayerLogoutListener(), this);
        await = configManager.Await();
    }

    private void registerCommands(@NotNull ReloadableRegistrarEvent<Commands> event) {
        var commands = event.registrar();
        new BackCommand(this).register(commands);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, String[] args) {
        if (!(sender instanceof Player))
            return false;
        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("tpa")) {
            if (args.length == 0)
                return false;
            Player target = getServer().getPlayer(args[0]);
            if (target == null)
                return false;
            String[] pair = new String[]{player.getName(), target.getName()};
            req.add(pair);
            target.sendMessage(ChatColor.AQUA + player.getName() + ChatColor.WHITE + " wants to teleport to your location");
            target.sendMessage(ChatColor.WHITE + "Type " + ChatColor.RED + "/tpacc " + player.getName() + ChatColor.WHITE + " to accept teleport request.");
            target.sendMessage(ChatColor.WHITE + "Type " + ChatColor.RED + "/tpdn " + player.getName() + ChatColor.WHITE + " to deny teleport request.");
            target.sendMessage(ChatColor.WHITE + "Type " + ChatColor.RED + "/tpacc" + ChatColor.WHITE + " to accept all teleport request.");
            target.sendMessage(ChatColor.WHITE + "Type " + ChatColor.RED + "/tpdn" + ChatColor.WHITE + " to deny all teleport request.");
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                if (req.contains(pair)) {
                    req.remove(pair);
                    player.sendMessage(ChatColor.YELLOW + "Your request has been denied because it takes too long time to respond.");
                }
            }, (long) await * 60 * 20);
            return true;
        }
        if (command.getName().equalsIgnoreCase("tpcancel")) {
            ArrayList<String[]> buffer = new ArrayList<>();
            if (args.length == 0) {
                for (String[] v : req) {
                    if (v[0].equalsIgnoreCase(player.getName()))
                        buffer.add(v);
                }
                req.removeAll(buffer);
                player.sendMessage(ChatColor.AQUA + "All your requests were cancelled.");
                return true;
            }
            else {
                Player target = getServer().getPlayer(args[0]);
                if (target == null)
                    return false;
                for (String[] v : req) {
                    if (v[0].equalsIgnoreCase(player.getName()) && v[1].equalsIgnoreCase(player.getName()))
                        buffer.add(v);
                }
                req.removeAll(buffer);
                player.sendMessage(ChatColor.AQUA + "All your request to " + ChatColor.WHITE + target.getName() + ChatColor.AQUA + " was cancelled.");
            }
        }
        if (command.getName().equalsIgnoreCase("tpacc")) {
            ArrayList<String[]> buffer = new ArrayList<>();
            if (args.length == 0) {
                for (String[] v : req) {
                    if (v[1].equalsIgnoreCase(player.getName())) {
                        Player target = getServer().getPlayer(v[0]);
                        if (!(target == null)) {
                            target.teleport(player.getLocation());
                            target.sendMessage(ChatColor.GREEN + "Teleport to " + ChatColor.WHITE + player.getName());
                            target.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20, 4, true, false, true));
                            buffer.add(v);
                        }
                    }
                }
            }
            else {
                Player target = getServer().getPlayer(args[0]);
                if (target == null)
                    return false;
                for (String[] v : req) {
                    if (v[0].equalsIgnoreCase(target.getName()) && v[1].equalsIgnoreCase(player.getName()))
                        buffer.add(v);
                }
                if (buffer.isEmpty())
                    return false;
                target.teleport(player.getLocation());
                target.sendMessage(ChatColor.GREEN + "Teleport to " + ChatColor.WHITE + player.getName());
                target.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20, 4, true, false, true));
            }
            req.removeAll(buffer);
            return true;
        }
        if (command.getName().equalsIgnoreCase("tpdn")) {
            ArrayList<String[]> buffer = new ArrayList<>();
            if (args.length == 0) {
                for (String[] v : req) {
                    if (v[1].equalsIgnoreCase(player.getName())) {
                        Player target = getServer().getPlayer(v[0]);
                        if (!(target == null)) {
                            target.sendMessage(ChatColor.RED + "Teleport request denied by " + ChatColor.WHITE + player.getName());
                            buffer.add(v);
                        }
                    }
                }
                player.sendMessage(ChatColor.LIGHT_PURPLE + "All request has been denied by you.");
            }
            else {
                Player target = getServer().getPlayer(args[0]);
                if (target == null)
                    return false;
                for (String[] v : req) {
                    if (v[0].equalsIgnoreCase(target.getName()) && v[1].equalsIgnoreCase(player.getName()))
                        buffer.add(v);
                }
                if (buffer.isEmpty())
                    return false;
                target.sendMessage(ChatColor.RED + "Teleport request denied by " + ChatColor.WHITE + player.getName());
                player.sendMessage(ChatColor.LIGHT_PURPLE + "All request from " + ChatColor.WHITE + target.getName() + ChatColor.LIGHT_PURPLE + " has been denied by you.");
            }
            req.removeAll(buffer);
            return true;
        }
        if (command.getName().equalsIgnoreCase("tprandom")) {
            int maxSize = Bukkit.getMaxWorldSize();
            double x = Math.random() * maxSize - (maxSize / 2.0);
            double z = Math.random() * maxSize - (maxSize / 2.0);
            double y = 256;
            if (args.length == 0) {
                Location location = player.getLocation();
                player.getWorld().loadChunk((x > 0) ? (int) (x / 16) : ((int) (x / 16) - 1), (z > 0) ? (int) (z / 16) : ((int) (z / 16) - 1));
                while (player.getWorld().getBlockAt((int) x, (int) --y, (int) z).getType().isAir() && (y > 0)) ;
                location.set(x, y + 2, z);
                player.teleport(location);
            }
            else {
                Player target = getServer().getPlayer(args[0]);
                if (target == null)
                    return false;
                Location location = target.getLocation();
                target.getWorld().loadChunk((x > 0) ? (int) (x / 16) : ((int) (x / 16) - 1), (z > 0) ? (int) (z / 16) : ((int) (z / 16) - 1));
                while (target.getWorld().getBlockAt((int) x, (int) --y, (int) z).getType().isAir() && (y > 0)) ;
                location.set(x, y + 2, z);
                target.teleport(location);
            }
            return true;
        }
        if (command.getName().equalsIgnoreCase("tp2p")) {
            if (args.length == 0)
                return false;
            if (args.length == 1) {
                Player target = getServer().getPlayer(args[0]);
                if (target == null)
                    return false;
                player.teleport(target.getLocation());
                return true;
            }
            else {
                Player from = getServer().getPlayer(args[0]);
                Player to = getServer().getPlayer(args[1]);
                if ((from == null) || (to == null))
                    return false;
                from.teleport(to.getLocation());
                return true;
            }
        }
        if (command.getName().equalsIgnoreCase("tppos")) {
            if (args.length != 3)
                return false;
            double x, y, z;
            try {
                x = Double.parseDouble(args[0]);
                y = Double.parseDouble(args[1]);
                z = Double.parseDouble(args[2]);
            } catch (Exception exception) {
                return false;
            }
            Location location = player.getLocation();
            location.set(x, y, z);
            player.teleport(location);
            return true;
        }
        if (command.getName().equalsIgnoreCase("spawn")) {
            player.teleport(player.getWorld().getSpawnLocation());
        }
        if (command.getName().equalsIgnoreCase("top")) {
            Location location = player.getLocation();
            int y = 255;
            while (player.getWorld().getBlockAt(location.getBlockX(), y--, location.getBlockZ()).getType().isAir()) ;
            location.setY(y + 2);
            player.teleport(location);
        }
        if (command.getName().equalsIgnoreCase("bottom")) {
            Location location = player.getLocation();
            location.setY(location.getY() - 2);
            while (!player.getWorld().getBlockAt(location).getType().isAir()) {
                location.setY(location.getY() - 1);
            }
            location.setY(location.getY() - 2);
            player.teleport(location);
        }
        if (command.getName().equalsIgnoreCase("world")) {
            World[] worlds = getServer().getWorlds().toArray(World[]::new);
            World playerWorld = player.getWorld();
            for (int i = 0; i < worlds.length; i++) {
                if (playerWorld.equals(worlds[i])) {
                    if (i == worlds.length - 1)
                        player.teleport(worlds[0].getSpawnLocation());
                    else
                        player.teleport(worlds[i + 1].getSpawnLocation());
                }
            }
        }
        if (command.getName().equalsIgnoreCase("🍥")) {
            if (progynova.contains(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "请等一会再来w~");
                return true;
            }
            if (args.length == 0) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 5, false, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 200, 19, false, false, false));
                player.sendMessage(ChatColor.AQUA + "你吃掉了一颗补子!");
            }
            else {
                Player target = getServer().getPlayer(args[0]);
                if (target == null)
                    return false;
                target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 5, false, false, false));
                target.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 200, 19, false, false, false));
                target.sendMessage(ChatColor.WHITE + player.getName() + ChatColor.AQUA + "喂给你了一颗补子!");
            }
            progynova.add(player.getUniqueId());
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                progynova.remove(player.getUniqueId());
            }, (long) await * 60 * 20);
            return true;
        }
        return false;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
