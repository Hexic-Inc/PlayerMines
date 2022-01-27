package org.hexic.playermines.handlers;

import me.lucko.helper.menu.Gui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.hexic.playermines.data.yml.GuiConfig;
import org.hexic.playermines.data.yml.SellPricesConfig;
import org.hexic.playermines.world.PlayerMine;
import org.hexic.playermines.world.Upgrade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GuiHandler {
    private Inventory inv;
    private GuiConfig config;
    private Player player;
    private ArrayList<Inventory> pages = new ArrayList<Inventory>();

    public GuiHandler(String inventory, Player player){
        this.player = player;
        ItemStack tempItem;
        config = new GuiConfig();
        for(String key : config.getKeys()){
            if(key.toLowerCase().contains(inventory.toLowerCase()) && key.toLowerCase().contains("gui")) {
                if(config.getInt(key +  ".Size") == 5){
                    inv = Bukkit.createInventory(new GuiHolder(), InventoryType.HOPPER,  translate( config.getValue(key + ".Display_Name")));
                } else {
                    inv = Bukkit.createInventory(new GuiHolder(), config.getInt(key +  ".Size"), translate(config.getValue(key + ".Display_Name")));
                }
                for (int i = 0; i < inv.getSize(); i++) {
                    tempItem = trueItemStack(getItemString(i, convertContents(config.getValue(key + ".Contents"))));
                    inv.setItem(i, tempItem);
                }
                return;
            }
        }
    }

    /**
     * Used to do simple tasks with an already created GUI, such as reload the GUI, see if a GUI exists, and get the matching GUI.
     */
    public GuiHandler(){
        config = new GuiConfig();
    }

    public void reloadGui(Player player, Inventory inventory){
        this.player = player;
      if(guiExists(inventory,player)){
          ItemStack[] clone = inventory.getContents().clone();
          player.openInventory(getMatchingGui(clone,player));
      }
    }

    public String getAction(ItemStack item, Player player){
        this.player = player;
        for(String key : config.getKeys()){
            if(!key.toLowerCase().contains("gui")){
                if(getBlock(key).equals(item)){
                    return new GuiConfig().getValue(key + ".Action");
                }
            }
        }
        return "";
    }

    public boolean guiExists(Inventory inventory, Player player){
        this.player = player;
        ItemStack[] gui1 = inventory.getContents().clone();
        for(String key : config.getKeys()){
            if(key.toLowerCase().contains("gui")){
                ItemStack[] gui2 = new GuiHandler(key,player).getGui().getContents().clone();
                if(inventory.getSize() == gui2.length) {
                    for (int i = 0; i < gui2.length; i++) {
                        ItemStack itemStack = gui2[i];
                        if(itemStack != null) {
                            if (itemStack.hasItemMeta()) {
                                itemStack.setItemMeta(gui1[i].getItemMeta());
                            }
                            gui2[i] = itemStack;
                        }
                    }
                    if (Arrays.equals(gui2, gui1) || key.toLowerCase().contains("blocks-gui")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Used to get the display name of an existing GUI.
     * @param name YML name of the GUI.
     * @return The display name of the GUI.
     */
    public String getDisplayName(String name){
        return translate(config.getValue(name + ".Display_Name"));
    }

    public String getYmlName(Inventory inventory){
        if(guiExists(inventory,null)){
            for(String key : config.getKeys()){
                if(key.toLowerCase().contains("gui")){
                    Inventory matchingInv = new GuiHandler(key,null).getGui();
                    if(Arrays.equals(matchingInv.getContents(),inventory.getContents())){
                        return key;
                    }
                }
            }
        }
        return "";
    }

    public Inventory getMatchingGui(ItemStack[] gui1, Player player){
        this.player = player;
        for(String key : config.getKeys()){
            if(key.toLowerCase().contains("gui")){
                ItemStack[] gui2 = new GuiHandler(key,player).getGui().getContents().clone();
                for(int i =0; i < gui2.length; i++) {
                    if (gui1[i].getType() != Material.AIR) {
                        ItemStack itemStack = gui2[i];
                        if (gui1[i].hasItemMeta()) {
                            itemStack.setItemMeta(gui1[i].getItemMeta());
                        }
                        gui2[i] = itemStack;
                    }
                    if (Arrays.equals(gui2, gui1) || key.toLowerCase().contains("blocks-gui")) {
                        return new GuiHandler(key, player).getGui();
                    }
                }
            }
        }
        return null;
    }

    public Inventory getGui(){
        return inv;
    }

    private String translate(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    private String getItemString(int slot, ArrayList<String> contents){// "<x,x;$block>"
        for (String content : contents) {
            String[] temp = content.split(";");// Split location and block.
            if (trueLocationsOfItem(temp[0]).contains(slot)) {//If the array list of all the slots that the item goes in, contains the current slot
                return temp[1];// Return the block that should be there
            }
        }
        return "air";
    }



    private ItemStack trueItemStack(String rawItem){//"$block OR block"
        ItemStack itemString;//Empty placeholder block
        if(rawItem.contains("$")){//"$block"
            itemString = getBlock(rawItem.replace("$",""));
        } else {//"block"
            itemString = new ItemStack(Material.valueOf(rawItem.toUpperCase()));
        }
        return itemString;
    }

    private ItemStack getBlock(String blockSection){
        if(!config.getKeys().contains(blockSection)){
            return new ItemStack(Material.AIR);
        }
        ItemStack item = new ItemStack(Material.valueOf(config.getValue(blockSection + ".Item").toUpperCase()));
        ItemMeta itemMeta = item.getItemMeta();
        String[]  lore = translate(config.getValue( blockSection + ".Lore")).split(";");
        if(player != null) {
            if (config.getValue(blockSection + ".Lore").toLowerCase().contains("upgrade")) {
                Upgrade upgrade = Upgrade.valueOf(new GuiConfig().getValue(blockSection + ".Action").split(";")[1].toUpperCase());
                for (int i = 0; i < lore.length; i++) {
                    if (lore[i].contains("$upgrade_cost")) {
                        lore[i] = lore[i].replace("$upgrade_cost", new PlayerMine(player).getUpgradeCost(upgrade, 1) + "");
                    }
                    if (lore[i].contains("$upgrade_currency")) {
                        lore[i] = lore[i].replace("$upgrade_currency", new PlayerMine(player).balType(upgrade) + "");
                    }
                    if (lore[i].contains("$upgrade_level")) {
                        lore[i] = lore[i].replace("$upgrade_level", new PlayerMine(player).getUpgradeLevel(upgrade) + "");
                    }
                    if (lore[i].contains("$upgrade_max")) {
                        lore[i] = lore[i].replace("$upgrade_max", new PlayerMine(player).getMaxUpgradeLevel(upgrade) + "");
                    }
                }
            }
            if(config.getValue(blockSection + ".Lore").toLowerCase().contains("$mine-contents")){
                lore = addMinePercentages().toArray(new String[0]);
            }
        }
        itemMeta.setDisplayName(translate(config.getValue(blockSection + ".Display_Name")));
        itemMeta.setLore(Arrays.asList(lore));
        item.setItemMeta(itemMeta);
        return item;
    }

    private ArrayList<String> convertContents(String rawContents){
        ArrayList<String> cSections = new ArrayList<>();//Create the content sections in an arraylist
        StringBuilder tempString = new StringBuilder();// Create a new string builder as a temp string builder
        String newContents = rawContents.replace("[", "");// Replace the open bracket to nothing
        newContents = newContents.replace("]", "");// Replace the close bracket to nothing
        for(int i = 0; i < newContents.length(); i ++){
            if(newContents.charAt(i) != ' ') {
                if (newContents.charAt(i) == '}') {
                    cSections.add(tempString.toString());// Add the temp string as an array list
                    tempString = new StringBuilder();// Clear the temp string
                } else if(newContents.charAt(i) != '{')
                    if (i != 0 && newContents.charAt(i - 1) != '}') {
                        tempString.append(newContents.charAt(i));// Add the character to the temp string, as long as the previous character wasn't a '}' and the current character isn't a comma
                    }
            }
        }
        return cSections;
    }

    private ArrayList<Integer> trueLocationsOfItem(String location){
        String row = location.split(",")[0];
        String column = location.split(",")[1];
        ArrayList<Integer> rowCount = new ArrayList<>();
        ArrayList<Integer> columnCount = new ArrayList<>();
        ArrayList<Integer> trueLocations = new ArrayList<>();
        if(row.contains("x")){
            for(int i = 0; i < 9; i++){
                rowCount.add(i + 1);
            }
        } else if (row.contains("-")){
            int fInt = Integer.parseInt(String.valueOf(row.charAt(0)));
            int sInt = Integer.parseInt(String.valueOf(row.charAt(2)));
            for(int i = fInt; i <= sInt; i++){
                rowCount.add(i);
            }
        } else {
            rowCount.add(Integer.parseInt(String.valueOf(row.charAt(0))));
        }
        if(column.contains("x")){
            for(int i = 0; i < 9; i++){
                columnCount.add(i + 1);
            }
        } else if(column.contains("-")){
            int fInt = Integer.parseInt(String.valueOf(column.charAt(0)));
            int sInt = Integer.parseInt(String.valueOf(column.charAt(2)));
            for(int i = fInt; i <= sInt; i++) {
                columnCount.add(i);
            }
        } else {
            columnCount.add(Integer.parseInt(String.valueOf(column.charAt(0))));
        }
        for (Integer integer : rowCount) {
            for (Integer value : columnCount) {
                trueLocations.add((integer * 9) - ( 9 - value + 1));
            }
        }
        return trueLocations;
    }

    public ArrayList<Inventory> fillGuiWithBlocks(){
        Inventory inventory = new GuiHandler("Blocks",player).getGui();
        SellPricesConfig sellPricesConfig = new SellPricesConfig();
        Map<Material,Double> map = sellPricesConfig.getBlocksWithPrices();
        for (Map.Entry<Material, Double> entry : map.entrySet()) {
            Material item = entry.getKey();
            Double price = entry.getValue();
            if (inventory.firstEmpty() == -1) {
                pages.add(inventory);
                inventory = new GuiHandler("Blocks", player).getGui();
            }
            if(sellPricesConfig.getBlocksWithChances().get(entry.getKey()) == 0.0) {
                //Set the display name of the item and the Item meta.
                // Make sure the itemMeta contains the current block chance, and actions to set chances.
                ItemStack itemStack = new ItemStack(item);
                inventory.setItem(inventory.firstEmpty(), itemStack);
            }
        }
        pages.add(inventory);
        return pages;
    }

    public ArrayList<String> addMinePercentages(){
        ArrayList<String> lore = new ArrayList<>();
        PlayerMine playerMine = new PlayerMine(player);
        playerMine.getMineBlocks().forEach(mineBlock ->{
            lore.add(translate("&c" + mineBlock.getItem().getType() + " -> " + playerMine.getMineBlockChance(mineBlock.getItem()) + "%"));
        });
        return  lore;
    }

    public void addNavigationBar(){

    }

}
