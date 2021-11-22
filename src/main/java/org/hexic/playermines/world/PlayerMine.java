package org.hexic.playermines.world;

import com.github.yannicklamprecht.worldborder.api.BorderAPI;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.drawethree.ultraprisoncore.UltraPrisonCore;
import me.drawethree.ultraprisoncore.api.enums.LostCause;
import me.drawethree.ultraprisoncore.api.enums.ReceiveCause;
import me.drawethree.ultraprisoncore.autosell.UltraPrisonAutoSell;
import me.drawethree.ultraprisoncore.gems.api.UltraPrisonGemsAPI;
import me.drawethree.ultraprisoncore.tokens.api.UltraPrisonTokensAPI;
import me.jet315.prisonmines.JetsPrisonMines;
import me.jet315.prisonmines.JetsPrisonMinesAPI;

import me.jet315.prisonmines.mine.Mine;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.hexic.playermines.PlayerMines;
import org.hexic.playermines.data.json.MinesJson;
import org.hexic.playermines.data.json.PlayerJson;
import org.hexic.playermines.data.yml.LangConfig;
import org.hexic.playermines.data.yml.SellPricesConfig;
import org.hexic.playermines.data.yml.YmlConfig;
import org.hexic.playermines.handlers.MineCrateHandler;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class PlayerMine {

    private String uuid;
    private final JetsPrisonMinesAPI jetsPrisonMinesAPI = UltraPrisonCore.getInstance().getJetsPrisonMinesAPI();
    private final YmlConfig config;
    private OfflinePlayer ownerPlayer;
    private PlayerJson ownerJson;
    private Economy econ;
    private String mineName;

    /**
     * Create the instance of the players mine. Some features may not work if the player is offline.
     * @param player Players name that would own the mine.
     */
    public PlayerMine(String player){
        if(Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(player))){
            this.uuid = Bukkit.getPlayer(player).getUniqueId().toString();
        } else {
            if(Bukkit.getOfflinePlayer(Bukkit.getOfflinePlayer(player).getUniqueId()).hasPlayedBefore()) {
                this.uuid = Bukkit.getOfflinePlayer(player).getUniqueId().toString();
            }
        }
        this.ownerPlayer = Bukkit.getOfflinePlayer(player);
        this.config = new YmlConfig();
        this.ownerJson =  new PlayerJson(uuid);
        this.econ = PlayerMines.getInitalizer().getEcon();
        String[] split = uuid.split("-");
        this.mineName = "mine" + "-" + split[split.length-1];
    }


    /**
     * Create a blank instance of a players mine. You will need to manually specify the UUID of the mine using setUuid(uuid).
     */
    public PlayerMine(){
        this.config = new YmlConfig();
        this.econ = PlayerMines.getInitalizer().getEcon();
    }

    /**
     * Create the instance of the players mine.
     * @param player Player that would own the mine.
     */
    public PlayerMine(@NotNull Player player){
        this.uuid = player.getUniqueId().toString();
        this.econ = PlayerMines.getInitalizer().getEcon();
        this.config = new YmlConfig();
        this.ownerJson = new PlayerJson(uuid);
        String[] split = uuid.split("-");
        this.mineName = "mine" + "-" + split[split.length-1];
    }

    /**
     * Manually set the owner of the mine.
     * @param uuid The players or mines UUID;
     */
    public PlayerMine setUuid(String uuid){
        this.uuid = uuid;
        this.ownerJson = new PlayerJson(this.uuid);
        this.ownerPlayer = Bukkit.getOfflinePlayer(UUID.fromString(this.uuid));
        String[] split = uuid.split("-");
        this.mineName = "mine" + "-" + split[split.length-1];
        return this;
    }

    public static String mineOwner(Location location){
        String mineLocation = mineLocation(location);
        MinesJson minesJson = new MinesJson(mineLocation);
        return minesJson.getValue("Owner");
    }

    public static String mineLocation(Location location){
        JetsPrisonMinesAPI jetsPrisonMinesAPI = ((JetsPrisonMines) Bukkit.getPluginManager().getPlugin("JetsPrisonMines")).getAPI();
        ArrayList<Mine> mines = jetsPrisonMinesAPI.getMinesByLocation(location);
        int minLocationX = mines.get(0).getMineRegion().getMinPoint().getBlockX();
        int minLocationY = mines.get(0).getMineRegion().getMinPoint().getBlockY();
        int borderSize = getBorderLength();
        int resultX = minLocationX / borderSize;
        int resultY = minLocationY / borderSize;
        int mineSpace = 3;
        int totalX = resultX / mineSpace;
        int totalY = resultY / mineSpace;
        return totalX + "," + totalY;
    }

    /**
     * Get the Players Prison Mine.
     * @return Mine of a JetsPrisonMines type.
     */

    public Mine getPrisonMine(){
        if(jetsPrisonMinesAPI.getMineManager().getMineByName(mineName) == null) {
           createMine();
        }
        return jetsPrisonMinesAPI.getMineByName(mineName);
    }


    /**
     * Check if the player has enough of the supported currency needed.
     * @param upgrade Upgrade to get the currency for.
     * @param level Level that would be added to the upgrade.
     * @return if the player has enough.
     */
    public boolean hasEnoughForUpgrade(Upgrade upgrade, int level){
        String string = config.getSectionValue("Enchant_Costs", upgradeAsString(upgrade));
        long bal = 0;
        if(string.contains("G")){
            UltraPrisonGemsAPI ultraPrisonGemsAPI = UltraPrisonCore.getInstance().getGems().getApi();
            string = string.replace("G", "");
            bal =  ultraPrisonGemsAPI.getPlayerGems(ownerPlayer);
        } else if(string.contains("T")){
            UltraPrisonTokensAPI ultraPrisonTokensAPI = UltraPrisonCore.getInstance().getTokens().getApi();
            string = string.replace("T", "");
            bal =  ultraPrisonTokensAPI.getPlayerTokens(ownerPlayer);
        } else if(string.contains("$")){
            bal = (long) econ.getBalance(ownerPlayer);
            string = string.replace("$", "");
        }
        int newLevel = Integer.parseInt(ownerJson.getData().get(upgradeAsString(upgrade))) + level;
        return bal > (long) runMath(string, newLevel);
    }

    /**
     * Get the Upgrade level for that mine.
     * @param upgrade Upgrade to get the level for.
     * @return Level of the upgrade.
     */
    public int getUpgradeLevel(Upgrade upgrade){
        return Integer.parseInt(ownerJson.getValue(upgradeAsString(upgrade)));
    }

    /**
     * Get the Max Level for the Upgrade.
     * @param upgrade Upgrade to get the max level for.
     * @return Max level for the upgrade.
     */
    public int getMaxUpgradeLevel(Upgrade upgrade){
        return Integer.parseInt(config.getSectionValue("Enchant_Caps",upgradeAsString(upgrade)));
    }

    public int getResetTime(){
        return (int) runMath(config.getSectionValue("Enchant_Triggered", "Regen_Time"), getUpgradeLevel(Upgrade.REGEN_TIME));
    }

    /**
     * Get the upgrade as a YML supported string.
     * @param upgrade Upgrade to convert to string.
     * @return YML supported string.
     */
    public String upgradeAsString(Upgrade upgrade){
        String string = "";
        switch (upgrade){
            case SIZE:
                string = "Size";
                break;
            case BERSERK:
                string = "Berserk";
                break;
            case GEM_DROPS:
                string = "Gem_Drops";
                break;
            case TAX_PRICE:
                string = "Tax_Price";
                break;
            case REGEN_TIME:
                string = "Regen_Time";
                break;
            case RENT_PRICE:
                string = "Rent_Price";
                break;
            case ETOKEN_FINDER:
                string = "EToken_Finder";
                break;
            case UPGRADE_FINDER:
                string ="Upgrade_Finder";
                break;
            case MINE_MULTIPLIER:
                string = "Mine_Multiplier";
                break;
            case MINECRATE_FINDER:
                string = "MineCrate_Finder";
                break;
        }
        return string;
    }


    /**
     * The kind of currency that the upgrade requires.
     * @param upgrade Upgrade to get the currency for.
     * @return "Gems", "Tokens", "Dollars", OR "" if it doesn't contain a supported currency.
     */
    public String balType(Upgrade upgrade){
        String string = config.getSectionValue("Enchant_Costs", upgradeAsString(upgrade));
        if(string.contains("G")){
          return "Gems";
        } else if(string.contains("T")){
           return "Tokens";
        } else if(string.contains("$")){
           return "Dollars";
        }
        return "";
    }

    /**
     * Get the price needed to add the level to the upgrade.
     * @param upgrade Upgrade to get the supported currency.
     * @param level Level that would be added to the upgrade.
     * @return Price it would cost to add the level to the selected upgrade.
     */
    public long getUpgradeCost(Upgrade upgrade, int level){
        String string = config.getSectionValue("Enchant_Costs", upgradeAsString(upgrade));
        int newLevel = Integer.parseInt(ownerJson.getData().get(upgradeAsString(upgrade))) + level;
        long price = 0;
        if(string.contains("G")){
            price = (long) runMath(string.replace("G", ""), newLevel);
        } else if(string.contains("T")){
            price = (long) runMath(string.replace("T", ""), newLevel);
        } else if(string.contains("$")){
            price = (long) runMath(string.replace("$", ""), newLevel);
        }
        return price;
    }

    /**
     * Remove the supported currency from the players account.
     * @param upgrade Upgrade that is being selected.
     * @param level Amount to add to the upgrade leve.
     */
    private void runUpgradeCost(Upgrade upgrade, int level){
        String string = config.getSectionValue("Enchant_Costs", upgradeAsString(upgrade));
        int newLevel = Integer.parseInt(ownerJson.getData().get(upgradeAsString(upgrade))) + level;
        long price;
        if(string.contains("G")){
            UltraPrisonGemsAPI ultraPrisonGemsAPI = UltraPrisonCore.getInstance().getGems().getApi();
            price = (long) runMath(string.replace("G", ""), newLevel);
            ultraPrisonGemsAPI.removeGems(ownerPlayer, price);
        } else if(string.contains("T")){
            UltraPrisonTokensAPI ultraPrisonTokensAPI = UltraPrisonCore.getInstance().getTokens().getApi();
            price = (long) runMath(string.replace("T", ""), newLevel);
            ultraPrisonTokensAPI.removeTokens(ownerPlayer, price, LostCause.WITHDRAW);
        } else if(string.contains("$")){
            price = (long) runMath(string.replace("$", ""), newLevel);
            econ.withdrawPlayer(ownerPlayer, price);
        }

    }

    /**
     * Add the level for a specific Upgrade on the mine. Takes the supported currencies out of their account.
     * @param upgrade Type of upgrade.
     * @param level Increase the upgrade by that amount.
     * @return If the upgrade got added.
     */
    public boolean addPurchasedUpgrade(Upgrade upgrade, int level){
        Map<String, String> data = ownerJson.getData();
        String string = upgradeAsString(upgrade);
        if(Integer.parseInt(ownerJson.getData().get(string)) + level <= Integer.parseInt(config.getSectionValue("Enchant_Caps", string))){
            data.put(string, (Integer.parseInt(ownerJson.getData().get(string)) + level) + "");
            ownerJson.setValue(data);
            runUpgradeCost(upgrade, level);
            if(upgrade == Upgrade.SIZE){
               // setMineSize(level);
            }
            return true;
        }
        return false;
    }

    /**
     * Add the level for a specific Upgrade on the mine.
     * @param upgrade Type of upgrade.
     * @param level Increase the upgrade by that amount.
     * @return If the upgrade got added.
     */
    public boolean addUpgrade(Upgrade upgrade, int level){
        Map<String, String> data = ownerJson.getData();
        String string = upgradeAsString(upgrade);
        if(Integer.parseInt(ownerJson.getData().get(string)) + level < Integer.parseInt(config.getSectionValue("Enchant_Caps", string))){
            data.put(string, (Integer.parseInt(ownerJson.getData().get(string)) + level) + "");
            ownerJson.setValue(data);
            return true;
        }
        return false;
    }


    /**
     * Create a MineCrate Randomly in the mine. This gets ran 5 seconds after the mine gets created to make sure the crate gets created.
     *
     */
    public void createMineCrate(){
        Bukkit.getScheduler().scheduleSyncDelayedTask(PlayerMines.getInitalizer().getPlugin(), () ->{
            Mine mine = jetsPrisonMinesAPI.getMineManager().getMineByName(mineName);
            Location min = mine.getMineRegion().getMinPoint();
            Location max = mine.getMineRegion().getMaxPoint();
            int minX = min.getBlockX();
            int minY = min.getBlockY();
            int minZ = min.getBlockZ();
            int maxX = max.getBlockX();
            int maxY = max.getBlockY();
            int maxZ = max.getBlockZ();
            Random random = new Random();
            int randomX = random.nextInt(maxX - minX);
            int randomY = random.nextInt(maxY - minY);
            int randomZ = random.nextInt(maxZ - minZ);
            Location randomLoc = new Location(getMineWorld(), minX +randomX, minY + randomY, minZ + randomZ);
            Location holoLoc = new Location(getMineWorld(), minX + randomX, minY + randomY, minZ + randomZ);
            randomLoc.getBlock().setType(Material.AIR);
            randomLoc.getBlock().setType(new MineCrateHandler().getMaterial());
            ArmorStand hologram = (ArmorStand) getMineWorld().spawnEntity(holoLoc, EntityType.ARMOR_STAND);
            hologram.setVisible(false);
            hologram.setCustomNameVisible(true);
            hologram.setCustomName(ChatColor.RED + "MineCrate");
            hologram.setGravity(false);
        }, 100);
    }


    /**
     * Create the Auto Sell region for the players mine.
     */
    private void createAutoSellRegion(){
        UltraPrisonAutoSell ultraPrisonAutoSell = UltraPrisonCore.getInstance().getAutoSell();
        Map<Material, Double> map = new SellPricesConfig().getBlocksWithPrices();
        Location regionLocation = new Location(getMineWorld(), getMineRegion().getMinimumPoint().getX(),getMineRegion().getMinimumPoint().getY(),getMineRegion().getMinimumPoint().getZ());
        map.forEach((item, price) -> {
            ultraPrisonAutoSell.addBlockToRegion(regionLocation,item,price,getMineWorld());
        });
    }

    /**
     * Remove holograms at the specified location.
     * @param location Approximate location of where the hologram is.
     */
    public void removeHologram(Location location, int boxRadius){
        Collection<Entity> nearbyEntites = Objects.requireNonNull(location.getWorld()).getNearbyEntities(location, boxRadius, boxRadius, boxRadius);
        nearbyEntites.forEach(entity -> {
            if(entity.getType().equals(EntityType.ARMOR_STAND)){
                entity.remove();
            }
        });
    }


    /**
     * Run the upgrades for the mine, for that specific player.
     * @param player Player to run the upgrade for.
     */
    public void runUpgrades(Player player){
        config.getSection("Enchant_Chances").forEach((key, value) -> {
            if (Math.random() <= runMath(value, Double.parseDouble(ownerJson.getValue(key)))) {
                if (Integer.parseInt(ownerJson.getValue(key)) > 0) {
                    String string = new LangConfig(player, Upgrade.valueOf(key.toUpperCase())).getPrefixValue("Triggered-Messages", "Upgrade-Triggered", "&c$upgrade got triggered.");
                    String string2 = string.replace("$upgrade", Upgrade.valueOf(key.toUpperCase()).toString());
                    int level;
                    if (Integer.parseInt(ownerJson.getValue("Berserk_Count")) > 0) {
                        level = (int) runMath(config.getSectionValue("Enchant_Triggered", "Berserk"), Integer.parseInt(ownerJson.getValue("Berserk")));
                    } else {
                        level = Integer.parseInt(ownerJson.getValue(key));
                    }
                    if (Integer.parseInt(ownerJson.getValue("Berserk_Count")) - 1 >= 0) {
                        Map<String, String> data = ownerJson.getData();
                        data.put("Berserk_Count", (Integer.parseInt(ownerJson.getValue("Berserk_Count")) - 1) + "");
                        ownerJson.setValue(data);
                    }
                    switch (Upgrade.valueOf(key.toUpperCase())) {
                        case BERSERK:
                            runBerserk();
                        case GEM_DROPS:
                            runGemDrops(player, level);
                        case MINECRATE_FINDER:
                            createMineCrate();
                        case UPGRADE_FINDER:
                            Upgrade[] upgrades = Upgrade.values();
                            addUpgrade(upgrades[new Random().nextInt(upgrades.length)], 1);
                        case ETOKEN_FINDER:
                            runETokenFinder(player, level);
                        case RENT_PRICE:
                            if (!player.getUniqueId().toString().equalsIgnoreCase(uuid)) {
                                runRentPrice(player, level);
                            }
                        case TAX_PRICE:
                            if (!player.getUniqueId().toString().equalsIgnoreCase(uuid)) {
                                runTaxPrice(player, level);
                            }
                    }
                    if (Upgrade.valueOf(key.toUpperCase()) == Upgrade.TAX_PRICE) {
                        if (!player.getUniqueId().toString().equalsIgnoreCase(uuid)) {
                            runRentPrice(player, level);
                            player.sendMessage(string2.replace(Upgrade.valueOf(key.toUpperCase()).toString(), prettyString(Upgrade.valueOf(key.toUpperCase()))));
                        }
                    } else if (Upgrade.valueOf(key.toUpperCase()) == Upgrade.RENT_PRICE) {
                        if (!player.getUniqueId().toString().equalsIgnoreCase(uuid)) {
                            runTaxPrice(player, level);
                            player.sendMessage(string2.replace(Upgrade.valueOf(key.toUpperCase()).toString(), prettyString(Upgrade.valueOf(key.toUpperCase()))));
                        }
                    } else {
                        player.sendMessage(string2.replace(Upgrade.valueOf(key.toUpperCase()).toString(), prettyString(Upgrade.valueOf(key.toUpperCase()))));
                    }
                }
            }
        });
    }

    /**
     * Format an Upgrade to a nice string.
     * @param upgrade EX. "MINECRATE_FINDER"
     * @return EX. "Minecrate Finder"
     */
    private String prettyString(Upgrade upgrade){
        String temp = upgradeAsString(upgrade);
        if(temp.contains("_")) {
            String[] split = upgrade.toString().toLowerCase().split("_");
            String temp1 = split[0].replaceFirst(String.valueOf(split[0].charAt(0)), String.valueOf(split[0].charAt(0)).toUpperCase());
            String temp2 = split[1].replaceFirst(String.valueOf(split[1].charAt(0)), String.valueOf(split[1].charAt(0)).toUpperCase());
            temp = temp1 + " " + temp2;
        } else {
            String s = String.valueOf(upgrade.toString().charAt(0));
            temp = upgrade.toString().replaceFirst(s, s.toUpperCase());
        }
        return temp;
    }

    /**
     * Run the tax calculation for the players mine.
     * @param player Player to take the balance from.
     * @param level Upgrade Level
     */
    private void runTaxPrice(Player player, int level){
            econ.depositPlayer(ownerPlayer, runMath(config.getSectionValue("Enchant_Triggered", "Tax_Price"), level));
            econ.withdrawPlayer(player, runMath(config.getSectionValue("Enchant_Triggered", "Tax_Price"), level));
    }


    /**
     * Run the rent calculation for the players mine.
     * @param player Player to take the balance from.
     * @param level Upgrade level
     */
    private void runRentPrice(Player player, int level){
            econ.depositPlayer(ownerPlayer, runMath(config.getSectionValue("Enchant_Triggered", "Rent_Price"), level));
            econ.withdrawPlayer(player, runMath(config.getSectionValue("Enchant_Triggered", "Rent_Price"), level));
    }

    /**
     * Run the EToken Finder Upgrade.
     * @param player Player to give the rewards to.
     * @param level Upgrade level.
     */
    private void runETokenFinder(Player player, int level){
        UltraPrisonTokensAPI ultraPrisonTokensAPI = UltraPrisonCore.getInstance().getTokens().getApi();
        ultraPrisonTokensAPI.addTokens(player, (long) runMath(config.getSectionValue("Enchant_Triggered", "EToken_Finder"), level),ReceiveCause.GIVE);

    }

    /**
     * Run the Gem Drop Upgrade.
     * @param player Player to give the rewards to.
     * @param level Upgrade level.
     */
    private void runGemDrops(Player player, int level){
       UltraPrisonGemsAPI ultraPrisonGemsAPI = UltraPrisonCore.getInstance().getGems().getApi();
       ultraPrisonGemsAPI.addGems(player, (long) runMath(config.getSectionValue("Enchant_Triggered", "Gem_Drops"),level), ReceiveCause.GIVE);

    }

    /**
     * Run the Berserk Upgrade.
     */
    private void runBerserk(){
        Map<String, String> data = ownerJson.getData();
        data.put("Berserk_Count", new YmlConfig().getSectionValue("Enchant_Triggered","Berserk_Count"));
        ownerJson.setValue(data);
    }

    /**
     * Increase the size of the mine in every direction.
     * @param addLevel Size to increase the mine by.
     */
    private void setMineSize(int addLevel){
        ArrayList<String> coords = convertString(ownerJson.getValue("Mine-Size"));
        String[] first = coords.get(0).split(",");
        String[] second = coords.get(1).split(",");
        String newCoord = ("{" + ((Integer.parseInt(first[0]) - addLevel) +",") + (first[1] + ",") + (Integer.parseInt(first[2]) - addLevel)+"}" + ", " + "{" + ((Integer.parseInt(second[0]) + addLevel) +",") + (second[1] + ",") + (Integer.parseInt(second[2]) + addLevel)+ "}");
        ownerJson.setValue("Size", newCoord);
        createPrisonMine();
    }

    /**
     * Reset the players mine manually.
     */
    public void reset(){
        jetsPrisonMinesAPI.getMineByName(mineName).reset(true);
    }

    /**
     * Check to see if the player has a pre-existing mine.
     * @return Returns true if a player has a mine
     */
    public boolean hasMine(){
        return ownerJson.exists();
    }

    /**
     * EX:    900,100,900
     * @return The MineCraft location of the players mine.
     */
    public Location mineLocation(){
        String pMineCoord = ownerJson.getValue("Location");
        String[] split = pMineCoord.split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        return new Location(Bukkit.getWorld(config.getWorldName()),xAsCoord(x),100,yAsCoord(y) -  (Math.round(getBorderLength())));
    }

    /**
     * Teleport the player and set a border for the player around the mine.
     * @param player Player to teleport.
     */
    public void teleport(Player player) {
        player.teleport(mineLocation());
        setBorder(player);
    }

    /**
     * Get the list of players at the mine.
     * @param mineName Name of the mine. Usually the UUID of the owner of the mine.
     * @return All the players that are currently mining at the mine.
     */
    public List<Player> playersAtMine(String mineName){
            List<Player> playersAtMine = new ArrayList<>();
            List<Player> players = Objects.requireNonNull(Bukkit.getWorld(config.getWorldName())).getPlayers();
            for (Player player : players) {
                if (jetsPrisonMinesAPI.getMineManager().getMineByNameIgnoreCase(mineName).isLocationInRegion(player.getLocation())) {
                    playersAtMine.add(player);
                }
            }

            return playersAtMine;
    }



    /**
     * Set a border for the player the size of the mine.
     * @param player Player to set the border to.
     */
    public void setBorder(Player player){
        WorldBorderApi worldBorderApi = BorderAPI.getApi();
        worldBorderApi.setBorder(player,getBorderLength(), middleLocation());
    }

    /**
     * Create the mine and all the associated data needed for that player instance.
     */
    public void createMine(){
        if(Bukkit.getWorld(config.getWorldName()) == null) {//Check to see if the world exists.
            createWorld();
            createGlobalRegion();
        }
        createPlayerData();
        createMineData();
        createPrisonMine();
        createMineRegion();
        createAutoSellRegion();
        loadAndPaste();
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
        return new Location(getMineWorld(),xAsCoord(x) + Math.round(getBorderLength() / 2.0)  ,100,yAsCoord(y) - (Math.round(getBorderLength() / 2.0)));
    }

    /**
     * Set the blocks of a mine.
     * @param contents Map of ItemStack and Float representing blocks for the mine.
     */
    public void setMineBlocks(Map<ItemStack, Float> contents){
        Mine mine = jetsPrisonMinesAPI.getMineManager().getMineByName(mineName);
        contents.forEach((block, chance) -> {
            jetsPrisonMinesAPI.addBlockToMine(mine, block, chance);
        });
    }

    /**
     * Get the world that the player mines are in.
     * @return Bukkit world representation of the world.
     */
    public static World getMineWorld(){
        return Bukkit.getWorld(new YmlConfig().getWorldName());
    }

    /**
     * Create the JetsPrisonMines mine for the player.
     */
    private void createPrisonMine() {
        String[] coords = config.getMineCoords().split(";");
        Location first = convertCoords(coords[0]);
        Location second = convertCoords(coords[1]);
        if (jetsPrisonMinesAPI.createMine(mineName, first, second)) { // Create a mine if the player doesn't have a mine already.
            Map<String,String> data = ownerJson.getData();
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).setCustomName(mineName);
            setMineBlocks(convertBlocks(data.get("Mine-Contents")));
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getResetManager().setUseMessages(false);
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getResetManager().setUsePercentage(true);
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).setSpawnLocation(mineLocation());
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getResetManager().setPercentageReset(50);
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getResetManager().setUseTimer(false);
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).save();
        } else if (jetsPrisonMinesAPI.getMineManager().deleteMine(mineName) && jetsPrisonMinesAPI.createMine(mineName, first, second)){ // If for whatever reason the player has a mine already, delete the old one and make a new one.
            Map<String,String> data = ownerJson.getData();
            setMineBlocks(convertBlocks(data.get("Mine-Contents")));
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).setCustomName(mineName);
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getResetManager().setUseMessages(false);
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getResetManager().setUsePercentage(true);
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).setSpawnLocation(mineLocation());
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getResetManager().setPercentageReset(50);
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getResetManager().setUseTimer(false);
            jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).save();
        } else {
            Bukkit.getConsoleSender().sendMessage("Error creating " + mineName + "'s mine. Make sure it has valid blocks and a valid location.");
            Bukkit.getConsoleSender().sendMessage("Contact the dev if this issue persists.");
            return;
        }
        jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).reset(true);
    }

    /**
     * Create the Json data for the player.
     */
    private void createPlayerData(){
        ownerJson.setValue("Location",getAvailableLocation());

        Map<String,String> data = ownerJson.getData();
        data.put("Size", "0");
        data.put("Regen_Time", "0");
        data.put("Mine_Multiplier", "0");
        data.put("EToken_Finder",  "0");
        data.put("Tax_Price", "0");
        data.put("Rent_Price",  "0");
        data.put("MineCrate_Finder", "0");
        data.put("Upgrade_Finder", "0");
        data.put("Berserk",  "0");
        data.put("Gem_Drops", "0");
        data.put("Added_Members","");
        data.put("Berserk_Count", "0");
        data.put("Public", "false");
        data.put("Mine-Contents", config.getPmineContents());
        data.put("Mine-Size", config.getMineCoords());
        ownerJson.setValue(data);
    }

    /**
     * Convert blocks from json into a map
     * @param rawContents Raw string from json or YML
     * @return Map with Block, Chance
     */
    private Map<ItemStack, Float> convertBlocks(String rawContents){
        //Change this to get the players blocks once we create the player in the json.
        // For now we're just going to use the default blocks from Config.yml
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

    /**
     * Convert Mine Location string to a MineCraft location.
     * @param coords Coords as a string EX. "150,100,150"
     * @return String as a MineCraft Location. EX. "1150,100,850"
     */
    private Location convertCoords(String coords){
        String newString = coords.replace(" ", "");
        String pMineCoord =  ownerJson.getValue("Location");
        String[] mineSplit = pMineCoord.split(",");
        int mineX = Integer.parseInt(mineSplit[0]);
        int mineY = Integer.parseInt(mineSplit[1]);
        String[] split = newString.split(",");
        int locX = Integer.parseInt(split[0]);
        int locZ = Integer.parseInt(split[1]);
        int locY = Integer.parseInt(split[2]);
        return new Location(getMineWorld(),xAsCoord(mineX) + locX, locZ, yAsCoord(mineY) - locY);
    }


    /**
     *
     * Used to split a container
     * @param rawContents EX: "[{string1}; {string2}]" OR "string1; string2"
     * @return
     */
    private ArrayList<String> convertString(String rawContents){
        String newString = rawContents.replace(" ", "").trim();
        ArrayList<String> cSections = new ArrayList<>();//Create the content sections in an arraylist
        StringBuilder tempString = new StringBuilder();// Create a new string builder as a temp string builder
        String newContents = newString.replace("[", "");// Replace the open bracket to nothing
        newContents = newContents.replace("]", "");// Replace the close bracket to nothing
        for(int i = 0; i < newContents.length(); i ++){
            if(newContents.charAt(i) != ' ') {
                if (newContents.charAt(i) == '}' || newContents.charAt(i) == ';' || newContents.length() -1 == i) {
                    cSections.add(tempString.toString());// Add the temp string as an array list
                    tempString = new StringBuilder();// Clear the temp string
                } else if(newContents.charAt(i) != '{') {
                    tempString.append(newContents.charAt(i));// Add the character to the temp string, as long as the previous character wasn't a '}' and the current character isn't a comma
                }
            }
        }
        return cSections;
    }

    /**
     * Create the Bukkit World for the Player Mines.
     */
    public static void createWorld(){
        //Create the world on another thread.
        new BukkitRunnable(){
            @Override
            public void run() {
                WorldCreator creator = new WorldCreator(new YmlConfig().getWorldName());//Create the pmine world.
                creator.generator(new EmptyChunkGenerator());
                creator.createWorld();
            }
        }.runTask(PlayerMines.getInitalizer().getPlugin());
    }

    /**
     * Create the Region for that Players mine.
     */
    private void createMineRegion(){
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(getMineWorld()));
        if(!regions.hasRegion(mineName)) {
            Location first = jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getMineRegion().getMinPoint();
            Location second = jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getMineRegion().getMaxPoint();
            BlockVector3 min = BlockVector3.at(first.getX(), first.getY(), first.getZ());
            BlockVector3 max = BlockVector3.at(second.getX(), second.getY(), second.getZ());
            ProtectedRegion region = new ProtectedCuboidRegion(mineName, min, max);
            region.setPriority(1);
            region.setFlag(Flags.BLOCK_BREAK, StateFlag.State.ALLOW);
            regions.addRegion(region);
        }
    }

    /**
     * Get the Region for the mine.
     * @return Mine Region.
     */
    public ProtectedRegion getMineRegion(){
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(getMineWorld()));
        assert regions != null;
        if(!regions.hasRegion(mineName)) {
            createMineRegion();
        }
        return regions.getRegion(mineName);
    }

    /**
     * Create the Global Region for the Player Mine world.
     */
    private void createGlobalRegion(){
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(getMineWorld()));
        if(!regions.hasRegion("__global__")) {
            GlobalProtectedRegion region = new GlobalProtectedRegion("__global__");
            region.setFlag(Flags.BLOCK_BREAK, StateFlag.State.DENY);
            region.setFlag(Flags.CHORUS_TELEPORT, StateFlag.State.DENY);
            region.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
            region.setFlag(Flags.ENDERPEARL, StateFlag.State.DENY);
            regions.addRegion(region);
        }
    }

    /**
     * Load and paste the Player Mine schematic. Opens a new thread to do this.
     */
    private void loadAndPaste(){
        //Load and paste the schematic on a separate thread.
        new BukkitRunnable(){
            @Override
            public void run() {
                Location areaLocation = mineLocation();
                Clipboard clipboard;
                File file = new File(PlayerMines.getInitalizer().getDataManager().getFolder(), "/" + config.getDefaultSchem() + ".schem");
                ClipboardFormat format = ClipboardFormats.findByFile(file);
                try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                    clipboard = reader.read();
                    pasteSchematic(areaLocation,clipboard);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }.runTask(PlayerMines.getInitalizer().getPlugin());
    }

    /**
     * Paste the loaded schematic.
     * @param location Location to paste the schematic.
     * @param clipboard Schematic copied to the clipboard.
     */
    private void pasteSchematic(Location location, Clipboard clipboard){
        World world = getMineWorld();
        com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(world);
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(adaptedWorld)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    /**
     * X Coordinate from mine location into a Minecraft X location.
     * @param x X from Player Mine Location in Json.
     * @return Minecraft X Location.
     */
    private int xAsCoord(int x){
        String areaSize = config.getAreaSize();
        String[] split = areaSize.split(",");
        return (Integer.parseInt(split[0]) * 3) * x;
    }

    /**
     * Y Coordinate from mine location into a Minecraft Y location.
     * @param y Y from Player Mine Location in Json.
     * @return Minecraft Y Location.
     */
    private int yAsCoord(int y){
        String areaSize = config.getAreaSize();
        String[] split = areaSize.split(",");
        return (Integer.parseInt(split[0]) * 3) * y + getBorderLength();
    }

    /**
     * Get the border length for each Player Mine.
     * @return Count of the proper border length.
     */
    private static int getBorderLength(){
        return Integer.parseInt(new YmlConfig().getAreaSize());
    }

    /**
     * Create Mine Data to track each player's Mine Location.
     */
    private void createMineData(){
        MinesJson mData = new MinesJson(ownerJson.getValue("Location"));
        mData.setValue("Owner",uuid);
    }


    private ArrayList<String> getFreeLocations(String[] locations, String size){
        String border = Integer.parseInt(size) + 1 + "";
        ArrayList<String> freePoints = new ArrayList<>();
        for(String location : locations){
            if (!new MinesJson(location).exists()) {
                if(!location.contains(border)){
                    freePoints.add(location);
                }
            }
        }
        return freePoints;
    }

    private int getTotalFreePoints(String[] locations, String size){
        int count = 0;
        String border = Integer.parseInt(size) + 1 + "";
        for (String location : locations) {
            if (!new MinesJson(location).exists()) {
                if(!location.contains(border)){
                    count++;
                }
            }
        }
        return count;
    }

    private String[] getSurroundingPoints(String location) {
        String[] split = location.split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        int[] newX = {x, x + 1, x, x - 1};
        int[] newY = {y + 1, y, y - 1, y};
        String[] coords = new String[4];
        for(int i = 0; i < 4; i++){
            coords[i] = newX[i] + "," + newY[i];
        }
        return coords;
    }

    private String[] getCorners(String size){
        String[] corners = new String[4];
        int x = Integer.parseInt(size);
        int y = Integer.parseInt(size);
        corners[0] = getNegative(x) + "," + y; // "-x,y"
        corners[1] = x + "," + y; // "x,y"
        corners[2] = x + "," + getNegative(y); // "x,-y"
        corners[3] = getNegative(x) + "," + getNegative(y);
        return corners;
    }

    private int getNegative(int number){
        return number - (number * 2);
    }

    private String getAvailableLocation(){
        String location;
        String size;
        if(!new MinesJson("0,0").exists()){
            location = "0,0";
            size = "1";
        } else {
            location = new MinesJson("recent").getValue("Last");
            size = new MinesJson("size").getValue("Size");
            int freeCount = getTotalFreePoints(getSurroundingPoints(location), size);
            if (freeCount == 4) {
                location = getCorners(size)[0];
            } else if (freeCount == 2 || freeCount == 1) {
                location = getFreeLocations(getSurroundingPoints(location), size).get(0);
            } else if (freeCount == 0) {
                size = Integer.parseInt(size) + 1 + "";
                location = getCorners(size)[0];
            }
        }
        new MinesJson("recent").setValue("Last", location);
        new MinesJson("size").setValue("Size", size);
        return location;
    }

    private double runMath(String problem, double UpgradesLevel){
        String temp = problem.replace(" ", "");
        if(temp.contains("level")){
            temp = temp.replace("level", UpgradesLevel + "");
        }
        if(temp.contains("*")){
            String[] split = temp.split("\\*");
            return Double.parseDouble(split[0]) * Double.parseDouble(split[1]);
        }
        if(problem.contains("-")){
            String[] split = temp.split("-");
            return Double.parseDouble(split[0]) - Double.parseDouble(split[1]);
        }
        if(temp.contains("+")){
            String[] split = temp.split("\\+");
            return Double.parseDouble(split[0]) + Double.parseDouble(split[1]);
        }
        if(temp.contains("/")){
            String[] split = temp.split("/");
            return Double.parseDouble(split[0]) / Double.parseDouble(split[1]);
        }

        return Double.parseDouble(problem);
    }


}
