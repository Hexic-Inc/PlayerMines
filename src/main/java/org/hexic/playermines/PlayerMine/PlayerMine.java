package org.hexic.playermines.PlayerMine;

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


import me.jet315.prisonmines.JetsPrisonMinesAPI;

import me.jet315.prisonmines.mine.Mine;
import me.jet315.prisonmines.mine.blocks.MineBlock;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.hexic.playermines.Main;
import org.hexic.playermines.PlayerMine.SubClasses.*;
import org.hexic.playermines.data.json.MinesJson;
import org.hexic.playermines.data.json.PlayerJson;
import org.hexic.playermines.data.yml.YmlConfig;
import org.hexic.playermines.world.EmptyChunkGenerator;

import javax.xml.crypto.dsig.Transform;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;



public class PlayerMine {

    private String uuid;
    private final JetsPrisonMinesAPI jetsPrisonMinesAPI = new JetsPrisonMinesAPI();
    private final YmlConfig config = new YmlConfig();
    private final OfflinePlayer ownerPlayer;
    private final PlayerJson ownerJson;
    private final String mineName;

    //SubClasses
    private final JsonLocation jsonLocation;
    private final MineCrate mineCrate;
    private final Regions regions;
    private final PrisonMine prisonMine;
    private final Upgrades upgrades;
    private final Owner owner;



    /**
     * Create the instance of the players mine. Some features may not work if the player is offline.
     * @param playerName Players In-Game Name that would own the mine.
     */
    public PlayerMine(String playerName){
        if(Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(playerName))){
            this.uuid = Bukkit.getPlayer(playerName).getUniqueId().toString();
        } else {
            if(Bukkit.getOfflinePlayer(Bukkit.getOfflinePlayer(playerName).getUniqueId()).hasPlayedBefore()) {
                this.uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId().toString();
            }
        }
        this.ownerPlayer = Bukkit.getOfflinePlayer(playerName);
        this.ownerJson =  new PlayerJson(uuid);
        String[] split = uuid.split("-");
        this.mineName = "mine" + "-" + split[split.length-1];
        //Load SubClasses
        this.jsonLocation = new JsonLocation(ownerPlayer);
        this.mineCrate = new MineCrate(mineName);
        this.regions = new Regions(mineName);
        this.prisonMine = new PrisonMine(ownerPlayer, regions);
        this.upgrades = new Upgrades(ownerPlayer);
        this.owner = new Owner(ownerPlayer);
    }

    /**
     * Create the instance of the players mine for an offline player.
     * @param player Player that would own the mine.
     */
    public PlayerMine(OfflinePlayer player){
        this.uuid = Bukkit.getOfflinePlayer(player.getName()).getUniqueId().toString();
        this.ownerJson = new PlayerJson(uuid);
        this.ownerPlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
        String[] split = uuid.split("-");
        this.mineName = "mine" + "-" + split[split.length-1];
        //Load SubClasses
        this.jsonLocation = new JsonLocation(ownerPlayer);
        this.mineCrate = new MineCrate(mineName);
        this.regions = new Regions(mineName);
        this.prisonMine = new PrisonMine(ownerPlayer, regions);
        this.upgrades = new Upgrades(ownerPlayer);
        this.owner = new Owner(ownerPlayer);
    }

    /**
     * Create a PlayerMine instance using a players UUID.
     * @param uuid Player's Minecraft UUID
     */
    public PlayerMine(UUID uuid){
        this.uuid = uuid.toString();
        this.ownerJson = new PlayerJson(this.uuid);
        this.ownerPlayer = Bukkit.getOfflinePlayer(UUID.fromString(this.uuid));
        String[] split = uuid.toString().split("-");
        this.mineName = "mine" + "-" + split[split.length-1];
        //Load SubClasses
        this.jsonLocation = new JsonLocation(ownerPlayer);
        this.mineCrate = new MineCrate(mineName);
        this.regions = new Regions(mineName);
        this.prisonMine = new PrisonMine(ownerPlayer, regions);
        this.upgrades = new Upgrades(ownerPlayer);
        this.owner = new Owner(ownerPlayer);
    }

    /**
     * Create the instance of the players mine.
     * @param player Player that would own the mine.
     */
    public PlayerMine(Player player){
        this.uuid = player.getUniqueId().toString();
        this.ownerJson = new PlayerJson(uuid);
        this.ownerPlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
        String[] split = uuid.split("-");
        this.mineName = "mine" + "-" + split[split.length-1];
        //Load SubClasses
        this.jsonLocation = new JsonLocation(ownerPlayer);
        this.mineCrate = new MineCrate(mineName);
        this.regions = new Regions(mineName);
        this.prisonMine = new PrisonMine(ownerPlayer, regions);
        this.upgrades = new Upgrades(ownerPlayer);
        this.owner = new Owner(ownerPlayer);

    }

    /**
     * Delete the selected players saved Location and Upgrades. Allowing a new mine to be created.
     */
    public void delete(){
        new MinesJson(JsonLocation.jsonLocation(getTeleportLocation())).remove();
        ownerJson.remove();
    }

    /**
     * Check to see if a player is a member of the mine.
     * @param player Player to check
     * @return If the player is a member of the mine
     */
    public boolean isPlayerAdded(OfflinePlayer player){
        for(int i = 0; i < owner.getMembers().length; i++){
            if(owner.getMembers()[i].contains(player.getUniqueId().toString())){
                return true;
            }
        }
        return false;
    }

    /**
     * Get the added members of the PlayerMine
     * @return Added members
     */
    public String[] getMembers(){return owner.getMembers();}

    /**
     * Add a player to the Members list
     * @param player Player to be added
     */
    public void addPlayer(OfflinePlayer player){
        owner.addPlayer(player);
    }

    /**
     * Get the max amount of members allowed at that mine.
     * @return Max members allowed.
     */
    public int getMemberCap(){
        return owner.getMemberCap();
    }

    /**
     * Check to see if the Players mine is public
     * @return If the mine is public or not
     */
    public boolean isPublic(){
        return owner.isPublic();
    }

    public void setPublic(Boolean setPublic){

    }

    /**
     * Create the mine and all the associated data needed for that player instance.
     * @return Returns true if created instantly, false if there's a queue.
     */
    public boolean createMine(){
        owner.createPlayerData();
        new MinesJson(ownerJson.getValue("Location")).setValue("Owner",uuid);
        prisonMine.createPrisonMine(getTeleportLocation());
        regions.createMineRegion();
        prisonMine.createAutoSellRegion();
        return Main.getInitalizer().getSchematicHandler().pastePlayersSchematic(ownerPlayer);
    }


    // Upgrades Section

    /**
     * Run the upgrades for that specific player.
     * @param player Player who will recieve the rewards.
     */
    public void runUpgrades(Player player){
        upgrades.runUpgrades(player);
    }

    /**
     * Get the Upgrades type of currency
     * @param upgrade Upgrade to get the required type of currency.
     * @return Needed type of currency to level up that Upgrade.
     */
    public String getUpgradeType(Upgrade upgrade){
        return upgrades.getUpgradeType(upgrade);
    }

    /**
     * Add the level for a specific Upgrade on the mine. Takes the supported currencies out of their account.
     * @param upgrade Type of upgrade.
     * @param levelToAdd Increase the upgrade by that amount.
     * @return If the upgrade got added.
     */
    public boolean addPurchasedUpgrade(Upgrade upgrade, int levelToAdd){
        return upgrades.addPurchasedUpgrade(upgrade,levelToAdd);
    }

    /**
     * Check if the player has enough of the required currency
     * @param upgrade Upgrade type that will be leveled up
     * @param levelToAdd Level that will be added to the Upgrade type
     * @return If the player has enough to go to that level for that Upgrade type.
     */
    public boolean hasEnoughForUpgrade(Upgrade upgrade, int levelToAdd){
        return upgrades.hasEnoughForUpgrade(upgrade,levelToAdd);
    }

    /**
     * Get the time left for the mine to be reset.
     * @return Total time left.
     */
    public int getResetTime(){
        return upgrades.getResetTime();
    }

    /**
     * Max supported Upgrade level for that type.
     * @param upgrade Upgrade type.
     * @return Level that the Upgrade can be brought to.
     */
    public int getMaxUpgradeLevel(Upgrade upgrade){
        return upgrades.getMaxUpgradeLevel(upgrade);
    }

    /**
     * Get the level that the Upgrade is currently at.
     * @param upgrade Upgrade to fetch the level for.
     * @return Fetched level of the upgrade.
     */
    public int getUpgradeLevel(Upgrade upgrade){
        return upgrades.getUpgradeLevel(upgrade);
    }

    /**
     * The currency that is needed for that upgrade.
     * @param upgrade Upgrade that we're fetching the currency type for.
     * @return Type of currency needed.
     */
    public String getBalType(Upgrade upgrade){
        return upgrades.balType(upgrade);
    }

    /**
     * Get the cost required to upgrade the level by.
     * @param upgrade Upgrade to fetch the cost for.
     * @param level Level that will be added to the upgrade level.
     * @return Total amount that it would cost.
     */
    public long getUpgradeCost(Upgrade upgrade, int level){
        return upgrades.getUpgradeCost(upgrade,level);
    }

    /**
     * Get the Upgrade as it's seen in the YML
     * @param upgrade Upgrade to get as seen in YML
     * @return YML formatted Upgrade
     */
    public String getUpgradeString(Upgrade upgrade){return upgrades.upgradeAsString(upgrade);}

    // PrisonMine Section \\

    /**
     * Total available percentage left in the mine
     * @return Percent that there is no chance for blocks to spawn.
     */
    public double getMineBlocksFreeChance(){
        return prisonMine.getMineBlocksFreeChance();
    }

    /**
     * Set the Mines contents
     * @param contents Block and Chance to set the mine to
     */
    public void setMineBlocks(Map<ItemStack, Float> contents){
        prisonMine.setMineBlocks(contents);
    }

    /**
     * Get the Players JetsPrisonMine
     * @return The players Mine
     */
    public Mine getPrisonMine(){
        return prisonMine.getMine();
    }

    /**
     * All the blocks that have a chance of spawning at the time at that mine.
     * @return All the blocks that are added to the mine.
     */
    public ArrayList<MineBlock> getMineBlocks(){
        return prisonMine.getMineBlocks();
    }

    /**
     * Chance that the item may spawn at the mine.
     * @param item ItemStack of the block
     * @return Chance that the block may spawn.
     */
    public float getMineBlockChance(ItemStack item){
        return prisonMine.getMineBlockChance(item);
    }

    // MineCrate Section

    /**
     * Remove a hologram from within a box radius of the specified location.
     * @param location Near location of the hologram.
     * @param radius Radius around the location to remove the hologram
     */
    public void removeHologram(Location location, int radius){
        mineCrate.removeHologram(location, radius);
    }

    /**
     * Spawn a MineCrate on the top layer of the players Mine
     */
    public void createMineCrateTopLayer(){
        mineCrate.createMineCrateTopLayer();
    }








    //End of SubClasses

    /**
     * Get the owner of the Mine the player is standing at.
     * @param location Minecraft location of where the player currently is.
     * @return Owner of the Private Mine that they're standing at.
     */
    public static OfflinePlayer mineOwner(Location location){
        String mineLocation = JsonLocation.jsonLocation(location);
        MinesJson minesJson = new MinesJson(mineLocation);
        return Bukkit.getOfflinePlayer(UUID.fromString(minesJson.getValue("Owner")));
    }

    /**
     * Check to see if the player has a pre-existing mine.
     * @return Returns true if a player has a mine
     */
    public boolean hasMine(){
        return ownerJson.exists();
    }


    /**
     * Actually teleport the player and set a border for the player around the mine.
     * @param player Player to teleport.
     */
    public void teleport(Player player) {
        Location mineLocation = jsonLocation.mineLocation();
        Map<String, String> data = ownerJson.getData();
        String[] split = data.get("TP-Location").split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        int z = Integer.parseInt(split[2]);
        Location tpLocation = new Location(mineLocation.getWorld(), mineLocation.getX() + x, y, mineLocation.getZ() + z);
        player.teleport(tpLocation);
        setBorder(player);
    }

    /**
     * Get the Location that PlayerMines get by default
     * @return Minecraft Location of bottom right corner of mine.
     */
    public Location getRawMineLocation(){
        return jsonLocation.mineLocation();
    }


    /**
     * Get the location that players should be teleported to.
     * @return Location to get teleported at.
     */
    public Location getTeleportLocation() {
        Location mineLocation = jsonLocation.mineLocation();
        Map<String, String> data = ownerJson.getData();
        String[] split = data.get("TP-Location").split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        int z = Integer.parseInt(split[2]);
        return new Location(mineLocation.getWorld(), mineLocation.getX() + x, y, mineLocation.getZ() + z);
    }

    /**
     * Get the list of players at the mine.
     * @return All the players that are currently mining at the mine.
     */
    public List<Player> playersAtMine(){
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
        worldBorderApi.setBorder(player,getBorderLength(), prisonMine.middleLocation());
    }


    /**
     * Remove a border from the player.
     * @param player Player to remove the border from.
     */
    public void removeBorder(Player player){
        WorldBorderApi worldBorderApi = BorderAPI.getApi();
        worldBorderApi.resetWorldBorderToGlobal(player);
    }


    /**
     * Get the world that the player mines are in.
     * @return Bukkit world representation of the world.
     */
    public static World getMineWorld(){
        if(new YmlConfig().getWorldName() != null) {
            return Bukkit.getWorld(new YmlConfig().getWorldName());
        }
        return null;
    }



    /**
     * Reset the players mine manually.
     */
    public void reset(){
        teleport(Objects.requireNonNull(ownerPlayer.getPlayer()));
        jetsPrisonMinesAPI.getMineByName(mineName).reset(true);
    }


    /**
     * Create the Bukkit World for the Player Mines.
     */
    public static void createWorld(){
        WorldCreator creator = new WorldCreator(new YmlConfig().getWorldName());//Create the pmine world.
        creator.generator(new EmptyChunkGenerator());
        creator.createWorld();
        Regions.createGlobalRegion();
    }



    /**
     * Get the border length for each Player Mine.
     * @return Count of the proper border length.
     */
    private static int getBorderLength(){
        return Integer.parseInt(new YmlConfig().getAreaSize());
    }


}
