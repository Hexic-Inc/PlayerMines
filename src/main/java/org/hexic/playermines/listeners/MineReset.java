package org.hexic.playermines.listeners;

import me.jet315.prisonmines.events.MinePostResetEvent;
import me.jet315.prisonmines.events.MinePreResetEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.hexic.playermines.PlayerMines;
import org.hexic.playermines.world.PlayerMine;

public class MineReset implements Listener {

    @EventHandler
    public void preReset(MinePreResetEvent e){
        PlayerMine playerMine = new PlayerMine().setUuid(PlayerMine.mineOwner(e.getMine().getMineRegion().getMinPoint()));
        for (Player player : playerMine.playersAtMine(e.getMine().getCustomName())) {
            playerMine.runUpgrades(player);
        }
    }
}
