package org.hexic.playermines.data.json;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.hexic.playermines.managers.data.Json;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MinesJson {

    Json mJson;
    File file;
    JsonObject pData = new JsonObject();
    String header;

    /**
     * Owner of the data that will be assigned to it.
     * @param header Title of the owner, typically a UUID as a string.
     */
    public MinesJson(String header){
        mJson = new Json("Mines");
        this.file = mJson.getFile();
        this.header = header;
    }

    public void setValue(String key, String value){
        pData.addProperty(key, value);
        mJson.save(header, pData);
    }


    public String getValue(String key){
        try{
            FileReader reader = new FileReader(file);
            Map<String, ?> map = Json.GSON.fromJson(reader, Map.class);
            reader.close();
            String string = map.get(header).toString();
            Map<String,String> newMap = toMap(string);
            return newMap.get(key);
        } catch(IOException e){
            e.printStackTrace();
        }
        return "";
    }

    public boolean exists(){
        try{
            FileReader reader = new FileReader(file);
            Map<String,?> map = Json.GSON.fromJson(reader, Map.class);
            reader.close();
            return map.containsKey(header);
        } catch(IOException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean valueExists(String value){
        try{
            FileReader reader = new FileReader(file);
            Map<String,?> map = Json.GSON.fromJson(reader, Map.class);
            reader.close();
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                if(entry.getValue().toString().contains(value)) {
                    return true;
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        return false;
    }

    private Map<String,String> toMap(String convert){
        String s = convert.replace("{", "");
        Map<String,String> map = new HashMap<>();
        String key = "";
        StringBuilder temp = new StringBuilder();
        for(int i = 0; i < s.length(); i++){
            if (s.charAt(i) == ',' && s.charAt( i + 1) == ' ' || s.charAt(i) == '}') {
                map.put(key, temp.toString());
                temp = new StringBuilder();
            } else if (s.charAt(i) == '=') {
                key = temp.toString();
                temp = new StringBuilder();
            }
            else {
                temp.append(s.charAt(i));
            }
        }
        return map;
    }
}
