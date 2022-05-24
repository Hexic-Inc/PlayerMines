package org.hexic.playermines.data.yml;

import org.bukkit.Material;
import org.hexic.playermines.Main;
import org.hexic.playermines.data.manager.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SellPricesConfig {

    private Config config;


    /**
     * Load the SellPrices Config
     */
    public SellPricesConfig(){
        config = Main.getInitalizer().getDataManager().getConfig("SellPrices.yml");
    }

    /**
     * Create the Default Block and Price.
     */
    public void createDefault(){
        String section = "Default";
        config.createSection(section);
        config.setListValue(section, "Block", Material.STONE.toString());
        config.setListValue(section,"Price", 1000);
        config.saveConfig();
    }

    /**
     * Creates all the Default Blocks and Values for the Config.
     */
    public void createDefaultBlocks(){
        createDefault();
        createBlocksAndPrice(Material.STONE, 100, 0);
        createBlocksAndPrice(Material.COBBLESTONE, 100, 0);
        createBlocksAndPrice(Material.COAL_BLOCK, 100, 0);
        createBlocksAndPrice(Material.COAL_ORE, 100, 0);
        createBlocksAndPrice(Material.IRON_ORE, 100, 0);
        createBlocksAndPrice(Material.IRON_BLOCK, 100, 0);
        createBlocksAndPrice(Material.ANDESITE, 100, 0);
        createBlocksAndPrice(Material.POLISHED_ANDESITE, 100, 0);
        createBlocksAndPrice(Material.DIORITE, 100, 0);
        createBlocksAndPrice(Material.POLISHED_DIORITE, 100, 0);
        createBlocksAndPrice(Material.GRANITE, 100, 0);
        createBlocksAndPrice(Material.POLISHED_GRANITE, 100, 0);
        createBlocksAndPrice(Material.END_STONE, 10000, 1);

    }

    /**
     * Gets the Default price.
     * @return Default Block Price.
     */
    public double getDefaultPrice(){
        return Double.parseDouble( config.getString("Default" + ".Price"));
    }

    /**
     * Create the specified Block with the price and is its locked.
     * @param block Material Type of the block.
     * @param price Price for block, if 0, will be Default price.
     * @param lockedChance If greater than 0, player will not be able to change the block percent or type.
     */
    public void createBlocksAndPrice(Material block, double price, double lockedChance){
        String section = block.toString();
        config.createSection(section);
        if(lockedChance > 0){
            config.setListValue(section, "Locked", lockedChance);
        }
        if(price > 0) {
            config.setListValue(section, "Price", price);
        } else {
            config.setListValue(section, "Price", getDefaultPrice());
        }
        config.saveConfig();
    }

    public ArrayList<Material> getLockedBlocks(){
        Set<String> list = config.getKeys();
        ArrayList<Material> blocks = new ArrayList<>();
        list.forEach(header -> {
            if(!header.toLowerCase().contains("default")){
                if(config.contains(header + ".Locked")){
                    blocks.add(Material.valueOf(header.toUpperCase()));
                }
            }
        });
        return blocks;
    }

    public Double getTotalLockedChance(){
        Set<String> list = config.getKeys();
        final Double[] locked = {0.0};
        list.forEach(header -> {
            if (!header.toLowerCase().contains("default")) {
                if (config.contains(header + ".Locked")) {
                   locked[0] += Double.parseDouble(config.getString(header + ".Locked"));
                }
            }
        });
        return locked[0];
    }

    /**
     * Get all the Blocks and their prices.
     * @return Blocks and Prices.
     */
    public Map<Material,Double> getBlocksWithPrices(){
        Set<String> list = config.getKeys();
        Map<Material,Double> hashMap = new HashMap<>();
        list.forEach(header -> {
            if(!header.toLowerCase().contains("default")){
                if(config.getString(header + ".Price").toLowerCase().contains("default")){
                    hashMap.put(Material.valueOf(header.toUpperCase()), Double.parseDouble(config.getString("Default" + ".Price")));
                }else {
                    hashMap.put(Material.valueOf(header.toUpperCase()), Double.parseDouble(config.getString(header + ".Price")));
                }
            }
        });
        return hashMap;
    }

    /**
     * Get all the blocks with their chances.
     * @return Map of the blocks and the prices.
     */
    public Map<Material,Double> getBlocksWithChances(){
        Set<String> list = config.getKeys();
        Map<Material,Double> hashMap = new HashMap<>();
        list.forEach(header -> {
            if(!header.toLowerCase().contains("default")){
                if(config.contains(header + ".Locked")){
                    hashMap.put(Material.valueOf(header.toUpperCase()), Double.parseDouble(config.getString(header + ".Locked")));
                } else {
                    hashMap.put(Material.valueOf(header.toUpperCase()), 0.0);
                }
            }
        });
        return hashMap;
    }
}
