package org.hexic.playermines.handlers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MenuHandler {

   private Map<Player,ArrayList<Inventory>> inventories;
   private Map<Player, Boolean> recentlyClosed;
   private Map<Player, Integer> counts;

    public MenuHandler(){
        inventories = new HashMap<>();
        recentlyClosed = new HashMap<>();
        counts = new HashMap<>();
    }

    public void addCount(Player player, int count){
        counts.put(player,count);
    }

    public void addPlayer(Player player, Inventory firstInv){
        ArrayList<Inventory> invs = new ArrayList<>();
        invs.add(firstInv);
        this.inventories.put(player,invs);
        this.recentlyClosed.put(player,false);
        addCount(player, 1);
    }

    public void addInventory(Player player, Inventory inv){
        if(this.inventories.containsKey(player)){
            //ArrayList<Inventory> inventories = this.inventories.get(player);
            ArrayList<Inventory> itemStacks = this.inventories.get(player);
            itemStacks.add(inv);
            this.inventories.put(player,itemStacks);
            //this.inventories.replace(player,inventories);
        } else {
            addPlayer(player, inv);
        }
    }

    public boolean hasRecentlyClosed(Player player){
        return recentlyClosed.get(player);
    }

    public void setRecentlyClosed(Player player, boolean value){
        this.recentlyClosed.replace(player,value);
    }

    public ArrayList<Inventory> getInventories(Player player){return inventories.get(player);}

    public Map<Player,ArrayList<Inventory>> getAllInventories(){ return inventories;}

    public Map<Player,Integer> getCounts(){return counts;}

    public int getCount(Player player){return counts.get(player);}

    public void removeInventory(Player player, Inventory inventory){
        this.inventories.get(player).remove(inventory.getContents().clone());
        removeCount(player, 1);
    }

    public void removeCount(Player player, int count){
        counts.put(player,counts.get(player) - count);
    }

    public void removePlayer(Player player){
        inventories.remove(player);
        recentlyClosed.remove(player);
    }
}
