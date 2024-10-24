package al.yn.simpleteleport.utility;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CommandHelper {
    public static boolean ensureAsPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return true;
        } else {
            sender.sendMessage(ComponentHelper.createCommandNotPlayer());
            return false;
        }
    }

    public static boolean ensureAsPlayer(CommandSender sender, Entity executor) {
        if (executor instanceof Player) {
            return true;
        } else {
            sender.sendMessage(ComponentHelper.createCommandNotPlayer());
            return false;
        }
    }

    public static boolean ensurePlayerOnline(CommandSender sender, Player player) {
        if (!player.isConnected()) {
            sender.sendMessage(ComponentHelper.createPlayerOffline());
            return false;
        }
        return true;
    }
}
