package org.hexic.playermines.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.hexic.playermines.PlayerMines;
import org.hexic.playermines.handlers.GuiHolder;
import org.hexic.playermines.handlers.MenuHandler;

public class GuiOpen implements Listener {

    private MenuHandler menuHandler = PlayerMines.getInitalizer().getMenuHandler();

    @EventHandler
    public void GuiOpen(InventoryOpenEvent e){
        if(e.getInventory().getHolder() instanceof GuiHolder){
            if(menuHandler.getAllInventories().containsKey((Player) e.getPlayer()) &&menuHandler.getInventories((Player) e.getPlayer()).contains(e.getInventory())){
                return;
            }
            menuHandler.addInventory((Player)e.getPlayer(), e.getInventory());
            Bukkit.getConsoleSender().sendMessage(menuHandler.getAllInventories() + "");
        }
    }
}
