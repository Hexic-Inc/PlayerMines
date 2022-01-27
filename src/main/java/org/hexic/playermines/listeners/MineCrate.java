package org.hexic.playermines.listeners;


import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.hexic.playermines.handlers.MineCrateHandler;
import org.hexic.playermines.world.PlayerMine;

import java.util.Objects;

public class MineCrate implements Listener {

    @EventHandler
    public void MineCrateHit(PlayerInteractEvent e) {
        if (e.getPlayer().getWorld() != PlayerMine.getMineWorld()) {
            return;
        }
        if(e.getClickedBlock()== null || e.getClickedBlock().getType() != new MineCrateHandler().getMaterial()){
            return;
        }
        if (Objects.requireNonNull(e.getClickedBlock()).getType() == new MineCrateHandler().getMaterial()){
            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (new PlayerMine(e.getPlayer()).getPrisonMine().isLocationInRegion(e.getClickedBlock().getLocation())) {
                    e.getClickedBlock().setType(Material.AIR);
                    new MineCrateHandler().giveRewards(e.getPlayer());
                    new PlayerMine(e.getPlayer().getUniqueId().toString()).removeHologram(e.getClickedBlock().getLocation(), 1);
                }
            }
        }
    }
}
