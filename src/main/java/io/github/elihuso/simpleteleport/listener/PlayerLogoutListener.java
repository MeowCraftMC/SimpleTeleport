package io.github.elihuso.simpleteleport.listener;

import io.github.elihuso.simpleteleport.SimpleTeleport;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

public class PlayerLogoutListener implements Listener {

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ArrayList<String[]> buffer = new ArrayList<>();
        for (String[] v : SimpleTeleport.req) {
            if ((v[0].equalsIgnoreCase(player.getName())) || (v[1].equalsIgnoreCase(player.getName())))
                buffer.add(v);
        }
        SimpleTeleport.req.removeAll(buffer);
    }
}
