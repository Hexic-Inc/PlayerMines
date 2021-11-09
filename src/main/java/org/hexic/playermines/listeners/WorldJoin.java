package org.hexic.playermines.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.hexic.playermines.world.PlayerMine;

public class WorldJoin implements Listener {

    @EventHandler
    public void onWorldJoin(PlayerJoinEvent e){
        if(e.getPlayer().getWorld() == PlayerMine.getMineWorld()){
            if(new PlayerMine(e.getPlayer()).hasMine()){
               new PlayerMine(e.getPlayer()).setBorder(e.getPlayer());
            }
        }
    }
}