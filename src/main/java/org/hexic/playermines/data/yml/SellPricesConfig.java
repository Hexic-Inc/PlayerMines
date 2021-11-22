package org.hexic.playermines.data.yml;

import org.bukkit.Material;
import org.hexic.playermines.PlayerMines;
import org.hexic.playermines.managers.data.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SellPricesConfig {

    private Config config;


    /**
     * Load the SellPrices Config
     */
    public SellPricesConfig(){
        config = PlayerMines.getInitalizer().getDataManager().getConfig("SellPrices.yml");
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

    /**
     * Get all the Blocks and their prices.
     * @return Blocks and Prices.
     */
    public Map<Material,Double> getBlocksWithPrices(){
        Set<String> list = config.getKeys();
        Map<Material,Double> hashMap = new HashMap<>();
        list.forEach(header -> {
            if(!header.toLowerCase().contains("default")){
                hashMap.put(Material.valueOf(header.toUpperCase()), Double.parseDouble( config.getString(header + ".Price")));
            }
        });
        return hashMap;
    }
}
