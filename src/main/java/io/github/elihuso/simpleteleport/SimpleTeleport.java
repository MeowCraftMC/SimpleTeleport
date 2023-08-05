package io.github.elihuso.simpleteleport;

import io.github.elihuso.simpleteleport.listener.PlayerLogoutListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class SimpleTeleport extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(new PlayerLogoutListener(), this);
    }

    public static ArrayList<String[]> req = new ArrayList<>();

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
            req.add(new String[]{player.getName(), target.getName()});
            target.sendMessage(ChatColor.AQUA + player.getName() + ChatColor.WHITE + " wants to teleport to your location");
            return true;
        }
        if (command.getName().equalsIgnoreCase("tpacc")) {
            ArrayList<String[]> buffer = new ArrayList<>();
            for (String[] v : req) {
                if (v[1].equalsIgnoreCase(player.getName())) {
                    Player target = getServer().getPlayer(v[0]);
                    if (!(target == null)) {
                        target.teleport(player.getLocation());
                        target.sendMessage(ChatColor.GREEN + "Teleport to " + ChatColor.WHITE + player.getName());
                        buffer.add(v);
                    }
                }
            }
            req.removeAll(buffer);
            return true;
        }
        if (command.getName().equalsIgnoreCase("tpdn")) {
            ArrayList<String[]> buffer = new ArrayList<>();
            for (String[] v : req) {
                if (v[1].equalsIgnoreCase(player.getName())) {
                    Player target = getServer().getPlayer(v[0]);
                    if (!(target == null)) {
                        target.sendMessage(ChatColor.RED + "Teleport request denied by " + ChatColor.WHITE + player.getName());
                        buffer.add(v);
                    }
                }
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
