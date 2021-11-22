package org.hexic.playermines.handlers;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class GuiHolder implements InventoryHolder {
    @NotNull
    @Override
    public Inventory getInventory() {
        return null;
    }
}
