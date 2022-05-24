package org.hexic.playermines.data.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hexic.playermines.data.manager.Json;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PlayerJson {

    Json pJson;
    File file;
    Map<String, Object> pData = new HashMap<>();
    String owner;

    /**
     * Owner of the data that will be assigned to it.
     * @param owner Title of the owner, typically a UUID as a string.
     */
    public PlayerJson(String owner){
        pJson = new Json("Players");
        this.file = pJson.getFile();
        this.owner = owner;
    }

    /**
     * Remove the players saved data
     */
    public void remove(){
        pJson.remove(owner);
    }

    public void setValue(String key, Boolean value){
        if(this.exists()) {
            for (Map.Entry<String, String> entry : getData().entrySet()) {
                if (entry.getKey().contains(key)) {
                    pData.put(key, value);
                } else {
                    pData.put(entry.getKey(), entry.getValue());
                }
            }
        } else {
            pData.put(key, value);
        }
        pJson.save(owner, pData);
    }


    public void setValue(String key, String value){
        if(this.exists()) {
            for (Map.Entry<String, String> entry : getData().entrySet()) {
                if (entry.getKey().contains(key)) {
                    pData.put(key, value);
                } else {
                    pData.put(entry.getKey(), entry.getValue());
                }
            }
        } else {
            pData.put(key, value);
        }
        pJson.save(owner, pData);
    }

    public void setValue(String key, int value){
        if(this.exists()) {
            for (Map.Entry<String, String> entry : getData().entrySet()) {
                if (entry.getKey().contains(key)) {
                    pData.put(key, value);
                } else {
                    pData.put(entry.getKey(), entry.getValue());
                }
            }
        } else {
            pData.put(key, value);
        }
        pJson.save(owner, pData);
    }

    public void setValue(Map<String,String> data){
        pData.putAll(data);
        pJson.save(owner,pData);
    }

    public Map<String,String> getData(){
      return pJson.getContents(owner);
    }


    public String getValue(String key){
            return getData().get(key);
    }

    public boolean exists(){
        try{
            FileReader reader = new FileReader(file); // Read the existing Json
            ObjectMapper mapper = new ObjectMapper();
            Map<String,String> map = mapper.readValue(reader,Map.class);
            reader.close();
            return map.containsKey(owner);
        } catch(IOException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean valueExists(String value){
        try{
            FileReader reader = new FileReader(file); // Read the existing Json
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = mapper.readValue(reader,Map.class);
            reader.close();
            return map.containsValue(value);

        } catch(IOException e){
            e.printStackTrace();
        }
        return false;
    }

    private Map<String,String> toMap(String convert){
        ObjectMapper mapper = new ObjectMapper();
        Map<String,String> map = null;
        try {
            map = mapper.readValue(convert, Map.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return map;
    }

}

