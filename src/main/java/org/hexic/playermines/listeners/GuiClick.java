package org.hexic.playermines.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.hexic.playermines.handlers.ActionHandler;
import org.hexic.playermines.handlers.GuiHandler;
import org.hexic.playermines.handlers.GuiHolder;

public class GuiClick implements Listener {


    @EventHandler
    public void guiClick(InventoryClickEvent e){
        if(e.getInventory().getHolder() instanceof GuiHolder && e.getCursor() != null){
            e.setCancelled(true);
            ActionHandler act = new ActionHandler(new GuiHandler().getAction(e.getCurrentItem(),(Player) e.getWhoClicked()));
            act.doAction(e);
        }
    }

}
