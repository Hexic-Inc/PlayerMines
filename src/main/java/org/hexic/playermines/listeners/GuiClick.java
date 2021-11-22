package org.hexic.playermines.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.hexic.playermines.handlers.ActionHandler;
import org.hexic.playermines.handlers.GuiHandler;

import java.util.Objects;

public class GuiClick implements Listener {

    @EventHandler
    public void guiClick(InventoryClickEvent e){
        if(e.getCurrentItem() != null && new GuiHandler().guiExists(Objects.requireNonNull(e.getClickedInventory()), (Player) e.getWhoClicked())){
            e.setCancelled(true);
            int count = 1;
            ActionHandler act = new ActionHandler(new GuiHandler().getAction(e.getCurrentItem(),(Player) e.getWhoClicked()));
            if(e.getClick().isRightClick() && e.getClick().isShiftClick()){
                count = 10;
            }
            act.doAction((Player) e.getWhoClicked(), count);
        }
    }
}
