package org.hexic.playermines.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.hexic.playermines.Main;
import org.hexic.playermines.handlers.GuiHolder;
import org.hexic.playermines.handlers.MenuHandler;

public class GuiClose implements Listener {

    private MenuHandler menuHandler = Main.getInitalizer().getMenuHandler();

    @EventHandler
    public void GuiClose(InventoryCloseEvent e){
        if(e.getInventory().getHolder() instanceof GuiHolder) {
            if(!menuHandler.hasRecentlyClosed((Player) e.getPlayer()) && menuHandler.getInventories((Player) e.getPlayer()).size() == 1){
                menuHandler.removePlayer((Player) e.getPlayer());
                return;
            }
            if (menuHandler.hasRecentlyClosed((Player) e.getPlayer())) {
                menuHandler.setRecentlyClosed((Player) e.getPlayer(),false);

            } else if(!menuHandler.hasRecentlyClosed((Player) e.getPlayer()) && menuHandler.getInventories((Player) e.getPlayer()).size() > 1){
                if(menuHandler.getCount((Player)e.getPlayer()) < 1){
                    return;
                }
                e.getPlayer().openInventory(menuHandler.getInventories((Player) e.getPlayer()).get(menuHandler.getCount((Player) e.getPlayer()) - 1));
                menuHandler.removeCount((Player)e.getPlayer(), 1);
                menuHandler.setRecentlyClosed((Player) e.getPlayer(),false);
            }
        }
    }

}

