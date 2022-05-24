package org.hexic.playermines.PlayerMine.SubClasses;

import org.bukkit.OfflinePlayer;
import org.hexic.playermines.PlayerMine.SubClasses.JsonLocation;
import org.hexic.playermines.data.json.PlayerJson;
import org.hexic.playermines.data.yml.YmlConfig;

import java.util.Map;

public class Owner {

    private String uuid;
    private PlayerJson ownerJson;
    private JsonLocation jsonLocation;
    private OfflinePlayer owner;
    private final YmlConfig config = new YmlConfig();

    /**
     * Create an easily accessible instance of the players' data.
     * @param offlinePlayer Players data to fetch
     */
    public Owner(OfflinePlayer offlinePlayer){
        this.owner = offlinePlayer;
        this.uuid = offlinePlayer.getUniqueId().toString();
        this.ownerJson = new PlayerJson(uuid);
        this.jsonLocation = new JsonLocation(offlinePlayer);
    }


    /**
     * Create the Json Data for a player. Will reset everything if they already own a mine.
     */
    public void createPlayerData(){
        ownerJson.setValue("Location",jsonLocation.getAvailableLocation());
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
        data.put("Member_Cap", "2");
        data.put("Public", "false");
        data.put("Mine-Contents", config.getPmineContents());
        data.put("Mine-Size", config.getMineCords());
        data.put("TP-Location", config.getTPLocation());
        ownerJson.setValue(data);
    }

    public int getMemberCap(){
        return Integer.parseInt(ownerJson.getValue("Member_Cap"));
    }

    /**
     * Set if the mine is public or not
     * @param setPublic Set to true if the mine is public, false if private.
     */
    public void setPublic(Boolean setPublic){
        ownerJson.setValue("Public", setPublic + "");
    }

    /**
     * Check to see if the Owners Mine is public.
     * @return If the mine is public or not
     */
    public boolean isPublic(){
        return Boolean.parseBoolean(ownerJson.getValue("Public"));
    }

    /**
     * Get the JsonLocation of the PlayersMine
     * @return JsonLocation of the owners mine
     */
    public String getJsonLocation(){
        return ownerJson.getValue("Location");
    }

    /**
     * Add a player to the Added Members List
     * @param player Player to add to the list
     */
    public void addPlayer(OfflinePlayer player){
        //Check to see if any members are already exist, if not we're going to add the first member
        if(ownerJson.getValue("Added_Members").equals("") || ownerJson.getValue("Added_Members").isEmpty()){
            //Add the first member
            ownerJson.setValue("Added_Members", player.getUniqueId().toString());
        } else {
            //Add the next member in the list, separating them by commas
            ownerJson.setValue("Added_Members", ownerJson.getValue("Added_Members") + "," + player.getUniqueId());
        }
    }

    /**
     * Get all the members added to the owners mine.
     * @return UUID's of added members
     */
    public String[] getMembers(){
        return ownerJson.getValue("Added_Members").split(",");
    }



}
