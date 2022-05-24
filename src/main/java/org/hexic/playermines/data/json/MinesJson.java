package org.hexic.playermines.data.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hexic.playermines.data.manager.Json;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MinesJson {

    Json mJson;
    File file;
    Map<String,Object> pData = new HashMap<>();
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

    /**
     * Remove the saved location.
     */
    public void remove(){
        mJson.remove(header);
    }

    public void setValue(String key, String value){
        pData.put(key, value);
        mJson.save(header, pData);
    }




    public String getValue(String key){
       return mJson.getContents(header).get(key);
    }

    public boolean exists(){
        return mJson.getContents().containsKey(header);
    }

    public boolean valueExists(String value){
        return mJson.getContents(header).containsKey(value);
    }

}
