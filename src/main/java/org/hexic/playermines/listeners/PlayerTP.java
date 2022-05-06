package org.hexic.playermines.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.hexic.playermines.Main;
import org.hexic.playermines.PlayerMine.PlayerMine;

public class PlayerTP implements Listener {

    @EventHandler
    public void playerTeleport(PlayerTeleportEvent e){
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInitalizer().getPlugin(), new Runnable() {
                @Override
                public void run() {
                if (e.getPlayer().getLocation().getWorld() == PlayerMine.getMineWorld()) {
                    new PlayerMine(PlayerMine.mineOwner(e.getPlayer().getLocation()).getPlayer()).setBorder(e.getPlayer());
                }
            }
         }, 1L);
        }

    }
}
