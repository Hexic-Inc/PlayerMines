package org.hexic.playermines.listeners;

import me.lucko.helper.menu.Gui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.hexic.playermines.PlayerMines;
import org.hexic.playermines.handlers.ActionHandler;
import org.hexic.playermines.handlers.GuiHandler;
import org.hexic.playermines.handlers.GuiHolder;
import org.hexic.playermines.handlers.MenuHandler;

import java.util.ArrayList;

public class GuiClose implements Listener {

    private MenuHandler menuHandler = PlayerMines.getInitalizer().getMenuHandler();

    @EventHandler
    public void GuiClose(InventoryCloseEvent e){
        if(e.getInventory().getHolder() instanceof GuiHolder) {
            if(!menuHandler.hasRecentlyClosed((Player) e.getPlayer()) && menuHandler.getInventories((Player) e.getPlayer()).size() == 1){
                menuHandler.removePlayer((Player) e.getPlayer());
                return;
            }
            if (menuHandler.hasRecentlyClosed((Player) e.getPlayer())) {
                menuHandler.setRecentlyClosed((Player) e.getPlayer(),false);
                return;

            } else {
                e.getPlayer().openInventory(menuHandler.getInventories((Player) e.getPlayer()).get(menuHandler.getCount((Player) e.getPlayer()) - 1));
                menuHandler.removeInventory((Player) e.getPlayer(),e.getInventory());
                menuHandler.setRecentlyClosed((Player) e.getPlayer(),false);
            }
        }
    }

}

