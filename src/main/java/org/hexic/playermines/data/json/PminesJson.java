package org.hexic.playermines.data.json;

import com.google.gson.JsonObject;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hexic.playermines.managers.data.Json;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
public class PminesJson {

    Json mJson;
    File file;

    /**
     * Load the json before querying data.
     */
    public PminesJson(){
        mJson = new Json("Mines");
        this.file = mJson.getFile();
    }

    /**
     * Create the specified players mine.
     * @param player Player to create the mine for.
     */
    public void createPMine(Player player){
        String location;
        String size;
        if(!mineExists("0,0")){
            location = "0,0";
            size = "1";
        } else {
            location = getRecent();
            size = getSize();
            if(getTotalFreePoints(getSurroundingPoints(location), size) == 4 ){
                location = getCorners(size)[0];
            } else if (getTotalFreePoints(getSurroundingPoints(location), size) == 2 || getTotalFreePoints(getSurroundingPoints(location), size) == 1){
                location = getFreeLocations(getSurroundingPoints(location),size).get(0);
            } else if (getTotalFreePoints(getSurroundingPoints(location), size) == 0){
                size = increaseSize(size);
                location = getCorners(size)[0];
            }
        }
        addLocation(location, player.getUniqueId());
        updateRecent(location);
        updateSize(size);
    }

    /**
     * Get the mines location
     * @param player Player to fetch the mine location for.
     * @return Mine location in a two dimensional plot. EX// X,Y
     */
    public String getMineCoord(Player player){
        String key1 = "";
        try{
            FileReader reader = new FileReader(file);
            Map<String,?> map = Json.GSON.fromJson(reader, Map.class);
            reader.close();
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                String key = entry.getKey();
               if(entry.getValue().toString().contains(player.getUniqueId().toString())){
                   key1 = key;
               }
            }

        } catch(IOException e) {
            e.printStackTrace();
        }
        return key1;
    }

    /**
     * See if the player already own a mine.
     * @param player Potential owner of the mine.
     * @return Boolean if the player owns a mine.
     */
    public boolean mineExists(Player player){
        try{
            FileReader reader = new FileReader(file);
            Map<String,?> map = Json.GSON.fromJson(reader, Map.class);
            reader.close();
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                if(entry.getValue().toString().contains(player.getUniqueId().toString())){
                    return true;
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        return false;
    }

    private void addLocation(String location,UUID uuid){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Owner", uuid.toString());
        mJson.save(location, jsonObject);
    }

    private void updateRecent(String location){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Last", location);
        mJson.save("recent", jsonObject);
    }

    private void updateSize(String size){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Size", size);
        mJson.save("size",jsonObject);
    }

    @NotNull
    private String increaseSize(String size){
        return Integer.parseInt(size) + 1 + "";
    }

    @NotNull
    private ArrayList<String> getFreeLocations(String[] locations, String size){
        String border = increaseSize(size);
        ArrayList<String> freePoints = new ArrayList<>();
        for(String location : locations){
            if(!location.contains(border) || !mineExists(location)){
                freePoints.add(location);
            }
        }
       return freePoints;
    }

    private int getTotalFreePoints(String[] locations, String size){
        int count = 0;
        for (String location : locations) {
            if (!mineExists(location) || !location.contains(increaseSize(size))) {
                count++;
            }
        }
        return count;
    }
    @NotNull
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
    @NotNull
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



    // Get the amount of times the border wraps around the center point 0,0
    @NotNull
    private String getSize(){
        try{
            FileReader reader = new FileReader(file);
            Map<?, ?> map = Json.GSON.fromJson(reader, Map.class);
            reader.close();
            String string = map.get("size").toString();
            return string.replace("Size=", "").replace("{","").replace("}","");
        } catch(IOException e){
            e.printStackTrace();
        }
        return "";
    }

    @NotNull
    private String getRecent(){
        try{
            FileReader reader = new FileReader(file);
            Map<?, ?> map = Json.GSON.fromJson(reader, Map.class);
            reader.close();
            String string = map.get("recent").toString();
            return string.replace("Last=", "").replace("{","").replace("}","");
        } catch(IOException e){
            e.printStackTrace();
        }
        return "";
    }

    private boolean mineExists(String location){
        try{
            FileReader reader = new FileReader(file);
            Map<String,?> map = Json.GSON.fromJson(reader, Map.class);
            reader.close();
            return map.containsKey(location);
        } catch(IOException e){
            e.printStackTrace();
        }
        return false;
    }

}
