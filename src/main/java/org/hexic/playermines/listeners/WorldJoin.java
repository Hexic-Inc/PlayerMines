package org.hexic.playermines.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.hexic.playermines.PlayerMines;
import org.hexic.playermines.world.PlayerMine;

public class WorldJoin implements Listener {

    @EventHandler
    public void onWorldJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(PlayerMines.getInitalizer().getPlugin(), new Runnable() {
            @Override
            public void run() {
                if (e.getPlayer().getLocation().getWorld() == PlayerMine.getMineWorld()) {
                    new PlayerMine(PlayerMine.mineOwner(e.getPlayer().getLocation())).teleport(e.getPlayer());
                }
            }
        }, 1L);
    }
}