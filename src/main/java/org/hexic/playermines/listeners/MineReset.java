package org.hexic.playermines.listeners;

import me.jet315.prisonmines.events.MinePreResetEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.hexic.playermines.world.PlayerMine;

public class MineReset implements Listener {

    @EventHandler
    public void onReset(MinePreResetEvent e){
            PlayerMine playerMine = new PlayerMine().setUuid(e.getMine().getCustomName());
            Bukkit.getConsoleSender().sendMessage(e.getMine().getCustomName() + "is resetting");
            for (Player player : playerMine.playersAtMine(e.getMine().getCustomName())) {
                playerMine.runUpgrades(player);
            }
        }
}
