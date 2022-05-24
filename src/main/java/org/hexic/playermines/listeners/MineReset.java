package org.hexic.playermines.listeners;

import me.jet315.prisonmines.events.MinePreResetEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.hexic.playermines.PlayerMine.PlayerMine;

public class MineReset implements Listener {

    @EventHandler
    public void preReset(MinePreResetEvent e){
        if(e.getMine().getMineRegion().getWorld() == PlayerMine.getMineWorld()) {
            PlayerMine playerMine = new PlayerMine(PlayerMine.mineOwner(e.getMine().getMineRegion().getMinPoint()));
            for (Player player : playerMine.playersAtMine()) {
                playerMine.runUpgrades(player);
            }
        }
    }
}
