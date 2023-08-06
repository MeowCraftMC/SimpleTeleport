package io.github.elihuso.simpleteleport;

import io.github.elihuso.simpleteleport.listener.PlayerLogoutListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.logging.Level;

public final class SimpleTeleport extends JavaPlugin {

    public static ArrayList<String[]> req = new ArrayList<>();
    public static FileConfiguration config = new YamlConfiguration();
    int await = 5;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(new PlayerLogoutListener(), this);
        try {
            config.load(this.getDataFolder() + "/config");
            await = config.getInt("await");
        }
        catch (Exception exception) {
            config.set("await", 5);
            await = 5;
            try {
                config.save(this.getDataFolder() + "/config");
            }
            catch (Exception ex) {
                getLogger().log(Level.WARNING, ex.toString());
            }
        }
        getLogger().log(Level.INFO, "Await set as" + Integer.toString(await));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, String[] args) {
        if (!(sender instanceof Player))
            return false;
        Player player = (Player)sender;
        if (command.getName().equalsIgnoreCase("tpa")) {
            if (args.length == 0)
                return false;
            Player target = getServer().getPlayer(args[0]);
            if (target == null)
                return false;
            String[] pair = new String[]{player.getName(), target.getName()};
            req.add(pair);
            target.sendMessage(ChatColor.AQUA + player.getName() + ChatColor.WHITE + " wants to teleport to your location");
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                if (req.contains(pair)){
                    req.remove(pair);
                    player.sendMessage(ChatColor.YELLOW + "Your request was denied because of it takes too long time to respond.");
                }
            }, (long)await * 60 * 20);
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
                            target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 4, true, false, true));
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
                target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 4, true, false, true));
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
            }
            req.removeAll(buffer);
            return true;
        }
        return false;
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
