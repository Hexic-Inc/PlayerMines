package org.hexic.playermines.data.manager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.Plugin;

import javax.json.JsonObject;
import java.io.*;

import java.nio.file.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Json {

    String fileName;
    static Plugin pl;
    ObjectMapper mapper;

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .disableHtmlEscaping()
            .create();


    public static void setPlugin(Plugin plugin){
        pl = plugin;
    }


    /**
     * Init the Json file
     * @param fileName Name to fetch or create
     */
    public Json(String fileName){

        this.fileName = fileName;
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        if(!getFile().exists()){
            initFile(new File( pl.getDataFolder(), fileName + ".json"));
        }
    }


    /**
     * Create the Json file if it doesn't exist
     * @param file File to create
     */
    public void initFile(File file){
        try{
            FileWriter writer = new FileWriter(file);
            writer.write("{}");
            writer.flush();
            writer.close();
        } catch(IOException e){
            e.printStackTrace();
        }

    }

    /**
     * Manually save the file
     * @param file File to be manually saved
     */
    public void save(File file){
        try{
            PrintWriter writer = new PrintWriter(file);
            ObjectMapper mapper = new ObjectMapper(); // Convert the existing Json to a map
            writer.write(mapper.writeValueAsString(file));
            writer.flush();
            writer.close();
        } catch(IOException e){
            e.printStackTrace();
        }

    }

    /**
     * Remove a saved array/dataset
     * @param header Name to be removed
     */
    public void remove(String header){
        try{
            FileReader reader = new FileReader(new File( pl.getDataFolder(), fileName + ".json")); // Read the existing Json
            Map<String, ?> map = mapper.readValue(reader,Map.class);
            FileWriter writer = new FileWriter(new File( pl.getDataFolder(), fileName + ".json")); // Create a file writer
            map.remove(header);
            writer.write(mapper.writeValueAsString(map));
            writer.flush(); // Empty out the writer
            writer.close(); // Close out the writer
// IOException is THROWN, not logged
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Save a Json Object to the file
     * @param header Header to save the map under.
     * @param contents New json object to be saved
     */
  /*  public void save(String header, Map<?,?> contents){
        try{
            FileReader reader = new FileReader(new File( pl.getDataFolder(), fileName + ".json")); // Read the existing Json
            PrintWriter writer = new PrintWriter(new File( pl.getDataFolder(), fileName + ".json"));
            Map<String, Object> map = mapper.readValue(reader,Map.class);
            map.put(header,contents);
            writer.print(mapper.writeValueAsString(map));
            System.out.println(mapper.writeValueAsString(header));
            writer.flush();
            writer.close();
// IOException is THROWN, not logged
        } catch(IOException e){
            e.printStackTrace();
        }
    }*/

    public void save(String header, Map<String, Object> contents){
        try{
            Reader reader = Files.newBufferedReader(new File( pl.getDataFolder(), fileName + ".json").toPath()); // Read the existing Json
            Map<String, Map<String,Object>> map = GSON.fromJson(reader,Map.class); // Convert the existing Json to a <ap
            FileWriter writer = new FileWriter(new File( pl.getDataFolder(), fileName + ".json")); // Create a file writer
            map.put(header,contents);
            GSON.toJson(map,writer); // Write the temporary map to the file writer
            writer.flush(); // Empty out the writer
            writer.close(); // Close out the writer
// IOException is THROWN, not logged
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public Map<String,String> getContents(String header){
        try{
            Reader reader = Files.newBufferedReader(new File( pl.getDataFolder(), fileName + ".json").toPath()); // Read the existing Json
            Map<String, Map<String,String>> map = GSON.fromJson(reader,Map.class); // Convert the existing Json to a <ap
            return map.get(header);
// IOException is THROWN, not logged
        } catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public Map<String,Map<String,String>> getContents(){
        try{
            Reader reader = Files.newBufferedReader(new File( pl.getDataFolder(), fileName + ".json").toPath()); // Read the existing Json
            return GSON.fromJson(reader,Map.class);
// IOException is THROWN, not logged
        } catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fetch the Json file
     * @return Json file
     */
    public File getFile(){return new File( pl.getDataFolder(), fileName + ".json");}
}
