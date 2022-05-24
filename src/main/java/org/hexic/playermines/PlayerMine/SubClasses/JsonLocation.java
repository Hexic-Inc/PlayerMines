package org.hexic.playermines.PlayerMine.SubClasses;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.hexic.playermines.PlayerMine.PlayerMine;
import org.hexic.playermines.data.json.MinesJson;
import org.hexic.playermines.data.json.PlayerJson;
import org.hexic.playermines.data.yml.YmlConfig;

import java.util.ArrayList;

public class JsonLocation {
    private PlayerJson ownerJson;
    private YmlConfig config = new YmlConfig();

   public JsonLocation(OfflinePlayer owner){
       this.ownerJson = new PlayerJson(owner.getUniqueId().toString());
   }

    /**
     * Get all the free Player Mine Locations
     * @param locations Possible free locations that could be used for a Player Mine.
     * @param size How big the "Ring" around the grid is.
     * @return Total amount of free locations.
     */
    public ArrayList<String> getFreeLocations(String[] locations, String size){
        String border = Integer.parseInt(size) + 1 + "";
        ArrayList<String> freePoints = new ArrayList<>();
        for(String location : locations){
            if (new MinesJson(location).exists()) {
                if(!location.contains(border)){
                    freePoints.add(location);
                }
            }
        }
        return freePoints;
    }

    public void remove(){

    }

    /**
     * Get the total amount of free Locations that could be claimed.
     * @param locations All the possible locations.
     * @param size How big the "Ring" around the grid is.
     * @return Total amount of possible free locations.
     */
    public int getTotalFreePoints(String[] locations, String size){
        int count = 0;
        String border = Integer.parseInt(size) + 1 + "";
        for (String location : locations) {
            if (new MinesJson(location).exists()) {
                if(!location.contains(border)){
                    count++;
                }
            }
        }
        return count;
    }

    public String[] getSurroundingPoints(String location) {
        String[] split = location.split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        int[] newX = {x, x + 1, x, x - 1};
        int[] newY = {y + 1, y, y - 1, y};
        String[] cords = new String[4];
        for(int i = 0; i < 4; i++){
            cords[i] = newX[i] + "," + newY[i];
        }
        return cords;
    }

    public String[] getCorners(String size){
        String[] corners = new String[4];
        int x = Integer.parseInt(size);
        int y = Integer.parseInt(size);
        corners[0] = getNegative(x) + "," + y; // "-x,y"
        corners[1] = x + "," + y; // "x,y"
        corners[2] = x + "," + getNegative(y); // "x,-y"
        corners[3] = getNegative(x) + "," + getNegative(y);
        return corners;
    }

    public int getNegative(int number){
        return number - (number * 2);
    }

    public String getAvailableLocation(){
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


    /**
     * X Coordinate from mine location into a Minecraft X location.
     * @param x X from Player Mine Location in Json.
     * @return Minecraft X Location.
     */
    public int xAsCoord(int x){
        String areaSize = config.getAreaSize();
        return (Integer.parseInt(areaSize) * 3) * x;
    }

    /**
     * Y Coordinate from mine location into a Minecraft Y location.
     * @param y Y from Player Mine Location in Json.
     * @return Minecraft Z Location.
     */
    public int yAsCoord(int y){
        String areaSize = config.getAreaSize();
        return (Integer.parseInt(areaSize) * 3) * y ;
    }

    /**
     * Convert Mine Location string to a MineCraft location.
     * @param cords cords as a string EX. "150,100,150"
     * @return String as a MineCraft Location. EX. "1150,100,850"
     */
    public Location convertcords(String cords){
        String newString = cords.replace(" ", "");
        String pMineCoord =  ownerJson.getValue("Location");
        String[] mineSplit = pMineCoord.split(",");
        int mineX = Integer.parseInt(mineSplit[0]);
        int mineY = Integer.parseInt(mineSplit[1]);
        String[] split = newString.split(",");
        int locX = Integer.parseInt(split[0]);
        int locZ = Integer.parseInt(split[1]);
        int locY = Integer.parseInt(split[2]);
        return new Location(PlayerMine.getMineWorld(),xAsCoord(mineX) + locX, locZ, yAsCoord(mineY) - locY);
    }


    /**
     *
     * Used to split a container
     * @param rawContents EX: "[{string1}; {string2}]" OR "string1; string2"
     * @return
     */
    public ArrayList<String> convertString(String rawContents){
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
     * EX:    900,100,900
     * @return The MineCraft location of the players mine.
     */
    public Location mineLocation(){
        String pMineCord = ownerJson.getValue("Location");
        String[] split = pMineCord.split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        return new Location(Bukkit.getWorld(config.getWorldName()),xAsCoord(x),100,yAsCoord(y) -  (Math.round(Integer.parseInt(config.getAreaSize()))));
    }

    /**
     * Get the Json Mine Location for that players mine.
     * @param location Minecraft Location from inside an existing player mine.
     * @return Mine Location. EX. "0,0"
     */
    public static String jsonLocation(Location location){
        int minLocationX = location.getBlockX();
        int minLocationZ = location.getBlockZ();
        float borderSize = Integer.parseInt(new YmlConfig().getAreaSize());
        float resultX;
        float resultZ;
        if(Math.abs(minLocationX) > borderSize){
            resultX = minLocationX / borderSize;
        } else {
            resultX = 0;
        }
        if(Math.abs(minLocationZ) > borderSize){
            resultZ = minLocationZ / borderSize;
        } else {
            resultZ = 0;
        }
        int mineSpace = 3;
        int totalX = Math.round( resultX / mineSpace);
        int totalY = Math.round( resultZ / mineSpace);
        return totalX + "," + totalY;
    }
}
