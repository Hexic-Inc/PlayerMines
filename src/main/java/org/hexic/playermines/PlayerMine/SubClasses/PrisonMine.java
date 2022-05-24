package org.hexic.playermines.PlayerMine.SubClasses;

import dev.drawethree.ultraprisoncore.UltraPrisonCore;
import dev.drawethree.ultraprisoncore.autosell.UltraPrisonAutoSell;
import me.jet315.prisonmines.JetsPrisonMinesAPI;
import me.jet315.prisonmines.mine.Mine;
import me.jet315.prisonmines.mine.blocks.MineBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.hexic.playermines.PlayerMine.PlayerMine;
import org.hexic.playermines.data.json.PlayerJson;
import org.hexic.playermines.data.yml.SellPricesConfig;
import org.hexic.playermines.data.yml.YmlConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PrisonMine {

    private final JetsPrisonMinesAPI jetsPrisonMinesAPI = new JetsPrisonMinesAPI();
    private OfflinePlayer owner;
    private Regions regions;
    private String mineName;
    private PlayerJson ownerJson;
    private YmlConfig config;
    private JsonLocation jsonLoc;

    /**
     * Create instance of JetsPrisonMines mine.
     * @param owner Player that will "own" the mine.
     * @param regions Regions Object
     */
    public PrisonMine(OfflinePlayer owner, Regions regions){
        this.owner = owner;
        this.regions = regions;
        String[] split = owner.getUniqueId().toString().split("-");
        mineName = "mine" + "-" + split[split.length-1];
        this.ownerJson = new PlayerJson(owner.getUniqueId().toString());
        this.config = new YmlConfig();
        this.jsonLoc = new JsonLocation(owner);
    }

    /**
     * Get all the blocks assigned to that mine that have a chance of spawning.
     * @return All blocks that can possibly spawn in that mine and that are added to that mine.
     */
    public ArrayList<MineBlock> getMineBlocks(){
        return jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getBlockManager().getMineBlocks();
    }

    /**
     * Get the chance for a block to spawn at that mine.
     * @param item Block to get the chance for in the form of an ItemStack.
     * @return Chance for that block to spawn in the mine.
     */
    public float getMineBlockChance(ItemStack item){
        return jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getBlockManager().getChanceOfBlock(item);
    }

    /**
     * Get the total free percentage of blocks in the mine.
     * @return Total free percentage of blocks.
     */
    public double getMineBlocksFreeChance(){
        final double[] free = {100};
        getMineBlocks().forEach(mineBlock ->
                free[0] -= getMineBlockChance(mineBlock.getItem()));
        return free[0];
    }


    /**
     * Create the Auto Sell region for the players mine.
     */
    public void createAutoSellRegion(){
        UltraPrisonAutoSell ultraPrisonAutoSell = UltraPrisonCore.getInstance().getAutoSell();
        Map<Material, Double> map = new SellPricesConfig().getBlocksWithPrices();
        Location regionLocation = new Location(PlayerMine.getMineWorld(), regions.getMineRegion().getMinimumPoint().getX(),regions.getMineRegion().getMinimumPoint().getY(),regions.getMineRegion().getMinimumPoint().getZ());
        map.forEach((item, price) -> {
            ultraPrisonAutoSell.addBlockToRegion(regionLocation,item,price,PlayerMine.getMineWorld());
        });
        UltraPrisonCore.getInstance().getAutoSell().reload();
    }


    /**
     * Get the Players Prison Mine.
     * @return Mine of a JetsPrisonMines type.
     */
    public Mine getMine(){
        if(jetsPrisonMinesAPI.getMineManager().getMineByName(mineName) == null) {
            new PlayerMine(owner).createMine();
        }
        return jetsPrisonMinesAPI.getMineByName(mineName);
    }



    /**
     * Get the middle location for the player mine.
     * @return The center point inside the players mine as a Location.
     */
    public Location middleLocation(){
        String pMineCoord = ownerJson.getValue("Location");
        String[] split = pMineCoord.split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        return new Location(PlayerMine.getMineWorld(),jsonLoc.xAsCoord(x) + Math.round(getBorderLength() / 2.0)  ,100,jsonLoc.yAsCoord(y) - (Math.round(getBorderLength() / 2.0)));
    }

    /**
     * Get the border length for each Player Mine.
     * @return Count of the proper border length.
     */
    public static int getBorderLength(){
        return Integer.parseInt(new YmlConfig().getAreaSize());
    }


    /**
     * Set the blocks of a mine.
     * @param contents Map of ItemStack and Float representing blocks for the mine.
     */
    public void setMineBlocks(Map<ItemStack, Float> contents){
        Mine mine = jetsPrisonMinesAPI.getMineManager().getMineByName(mineName);
        contents.forEach((block, chance) -> {
            if(mine.getBlockManager().getMineBlocks().contains(block)){
                mine.getBlockManager().modifyBlockChanceInRegion(block,chance);
            } else {
                jetsPrisonMinesAPI.addBlockToMine(mine, block, chance);
            }
        });

    }


    /**
     * Create the JetsPrisonMines mine for the player.
     */
    public void createPrisonMine(Location teleportLoc) {
        String[] cords = config.getMineCords().split(";");
        Location first = jsonLoc.convertcords(cords[0]);
        Location second = jsonLoc.convertcords(cords[1]);
        if (jetsPrisonMinesAPI.createMine(mineName, first, second)) { // Create a mine if the player doesn't have a mine already.
            Map<String,String> data = ownerJson.getData();
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).setCustomName(mineName);
            setMineBlocks(convertBlocks(data.get("Mine-Contents")));
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getResetManager().setUseMessages(false);
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getResetManager().setUsePercentage(true);
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).setSpawnLocation(teleportLoc);
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getResetManager().setPercentageReset(50);
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getResetManager().setUseTimer(true);
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getResetManager().setMineResetTime(10000);
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).save();
        } else if (jetsPrisonMinesAPI.getMineManager().deleteMine(mineName) && jetsPrisonMinesAPI.createMine(mineName, first, second)){ // If for whatever reason the player has a mine already, delete the old one and make a new one.
            Map<String,String> data = ownerJson.getData();
            setMineBlocks(convertBlocks(data.get("Mine-Contents")));
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).setCustomName(mineName);
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getResetManager().setUseMessages(false);
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getResetManager().setUsePercentage(true);
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).setSpawnLocation(teleportLoc);
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getResetManager().setPercentageReset(50);
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getResetManager().setUseTimer(true);
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getResetManager().setMineResetTime(10000);
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).save();
        } else {
            Bukkit.getConsoleSender().sendMessage("Error creating " + mineName + "'s mine. Make sure it has valid blocks and a valid location.");
            Bukkit.getConsoleSender().sendMessage("Contact the dev if this issue persists.");
            return;
        }
        jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).reset(true);
    }


    /**
     * Convert blocks from json into a map
     * @param rawContents Raw string from json or YML
     * @return Map with Block, Chance
     */
    public Map<ItemStack, Float> convertBlocks(String rawContents){
        String newContents = rawContents.replace("[", "").replace("]","").replace(" ", "");
        String[] blocks = newContents.split(",");
        Map<ItemStack, Float> map = new HashMap<>();
        for (String block : blocks) {
            String[] split = block.split(";");
            ItemStack item = new ItemStack(Material.valueOf(split[1].toUpperCase()));
            float flo = Float.parseFloat(split[0]);
            map.put(item, flo);
        }
        return map;
    }


}
