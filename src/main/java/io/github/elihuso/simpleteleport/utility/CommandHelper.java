package io.github.elihuso.simpleteleport.utility;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CommandHelper {
    public static boolean ensureAsPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return true;
        } else {
            sender.sendMessage(Component.text("请以玩家身份运行此命令！").color(NamedTextColor.RED));
            return false;
        }
    }

    public static boolean ensureAsPlayer(CommandSender sender, Entity executor) {
        if (executor instanceof Player) {
            return true;
        } else {
            sender.sendMessage(Component.text("请以玩家身份运行此命令！").color(NamedTextColor.RED));
            return false;
        }
    }
}
