package org.hexic.playermines.listeners;

import com.sk89q.worldedit.event.platform.BlockInteractEvent;
import com.sk89q.worldedit.event.platform.Interaction;
import me.drawethree.ultraprisoncore.mines.commands.impl.MineCreateCommand;
import me.jet315.prisonmines.JetsPrisonMines;
import me.jet315.prisonmines.JetsPrisonMinesAPI;
import me.jet315.prisonmines.mine.Mine;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.hexic.playermines.handlers.MineCrateHandler;
import org.hexic.playermines.world.PlayerMine;

import java.util.Objects;

public class MineCrate implements Listener {

    private final JetsPrisonMinesAPI jetsPrisonMinesAPI = ((JetsPrisonMines) Bukkit.getPluginManager().getPlugin("JetsPrisonMines")).getAPI();

    @EventHandler
    public void MineCrateHit(PlayerInteractEvent e) {
        if (e.getMaterial() != null && e.getClickedBlock().getType() == (new MineCrateHandler().getMaterial())) {
            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (e.getPlayer().getWorld() != PlayerMine.getMineWorld()) {
                    return;
                }
                if (jetsPrisonMinesAPI.getMineManager().getMineByName(e.getPlayer().getUniqueId().toString()).isLocationInRegion(e.getClickedBlock().getLocation())) {
                    new MineCrateHandler().giveRewards(e.getPlayer());
                    e.getClickedBlock().setType(Material.AIR);
                }
            }
        }
    }
}
