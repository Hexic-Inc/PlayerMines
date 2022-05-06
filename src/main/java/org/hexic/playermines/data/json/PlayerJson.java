package org.hexic.playermines.data.json;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hexic.playermines.managers.data.Json;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayerJson {

    Json pJson;
    File file;
    JsonObject pData = new JsonObject();
    String header;

    /**
     * Owner of the data that will be assigned to it.
     * @param header Title of the owner, typically a UUID as a string.
     */
    public PlayerJson(String header){
        pJson = new Json("Players");
        this.file = pJson.getFile();
        this.header = header;
    }

    /**
     * Remove the players saved data
     */
    public void remove(){
        pJson.remove(header);
    }

    public void setValue(String key, Boolean value){
        if(this.exists()) {
            for (Map.Entry<String, String> entry : getData().entrySet()) {
                if (entry.getKey().contains(key)) {
                    pData.addProperty(key, value);
                } else {
                    pData.addProperty(entry.getKey(), entry.getValue());
                }
            }
        } else {
            pData.addProperty(key, value);
        }
        pJson.save(header, pData);
    }


    public void setValue(String key, String value){
        if(this.exists()) {
            for (Map.Entry<String, String> entry : getData().entrySet()) {
                if (entry.getKey().contains(key)) {
                    pData.addProperty(key, value);
                } else {
                    pData.addProperty(entry.getKey(), entry.getValue());
                }
            }
        } else {
            pData.addProperty(key, value);
        }
        pJson.save(header, pData);
    }

    public void setValue(String key, int value){
        if(this.exists()) {
            for (Map.Entry<String, String> entry : getData().entrySet()) {
                if (entry.getKey().contains(key)) {
                    pData.addProperty(key, value);
                } else {
                    pData.addProperty(entry.getKey(), entry.getValue());
                }
            }
        } else {
            pData.addProperty(key, value);
        }
        pJson.save(header, pData);
    }

    public void setValue(Map<String,String> data){
        for (Map.Entry<String, String> entry : data.entrySet()) {
            pData.addProperty(entry.getKey(),entry.getValue());
        }
        pJson.save(header,pData);
    }

    public Map<String, String> getData(){
        try{
            FileReader reader = new FileReader(file);
            Map<String, ?> map = Json.GSON.fromJson(reader, Map.class);
            reader.close();
            return toMap(map.get(header).toString());
        } catch(IOException e){
            e.printStackTrace();
        }
        return new HashMap<>();
    }


    public String getValue(String key){
        try{
            FileReader reader = new FileReader(file);
            Map<String, ?> map = Json.GSON.fromJson(reader, Map.class);
            reader.close();
            return toMap(map.get(header).toString()).get(key);
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
        String s = convert.replaceFirst("\\{", "");
        Map<String,String> map = new HashMap<>();
        String key = "";
        StringBuilder temp = new StringBuilder();
        boolean blocker = false;
        for(int i = 0; i < s.length(); i++){
            if(s.charAt(i) == '['){
                blocker = true;
            }
            if (s.charAt(i)== ']'){
                blocker = false;
            }
            if (s.charAt(i) == ',' && s.charAt( i + 1) == ' ' && !blocker || s.charAt(i) == '}') {
                map.put(key.replace(" ", ""), temp.toString());
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

