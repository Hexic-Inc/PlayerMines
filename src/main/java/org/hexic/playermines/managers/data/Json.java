package org.hexic.playermines.managers.data;

import com.google.gson.*;
import org.bukkit.plugin.Plugin;

import java.io.*;

import java.nio.file.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class Json {

    File file;
    static Plugin pl;

    public static void setPlugin(Plugin plugin){
        pl = plugin;
    }

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .disableHtmlEscaping()
            .create();
    public Json(String fileName){

        file = new File( pl.getDataFolder(), fileName + ".json");

        if(!getFile().exists()){
            initFile(file);
        }
    }

    public void save(String string){
        try{
            Reader reader = Files.newBufferedReader(file.toPath()); // Read the existing Json
            JsonArray json = GSON.fromJson(reader,JsonArray.class); // Convert the existing Json to a <ap
            FileWriter writer = new FileWriter(file); // Create a file writer
            JsonArray newJson = new JsonArray();
            newJson.add(string);
            if(!json.isJsonNull()){ // Check if the Json data has data.
                for (int i = 0; i < json.size(); i++) { // For each dataset in the Map
                    if(!json.get(i).getAsString().equals(string)) { // If the entry that is fetched is not the same as the new data
                        newJson.add(json.get(i)); // Add the new dataset to the temporary map
                    }
                }
            }
            GSON.toJson(newJson,writer); // Write the temporary map to the file writer
            writer.flush(); // Empty out the writer
            writer.close(); // Close out the writer
// IOException is THROWN, not logged
        } catch(IOException e){
            e.printStackTrace();
        }
    }

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


    public void save(File file){
        try{
            FileWriter writer = new FileWriter(file);
            GSON.toJson(writer);
            writer.flush();
            writer.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void save(JsonObject obj){
        try{
            FileWriter writer = new FileWriter(file);
            GSON.toJson(obj ,writer);
            writer.flush();
            writer.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void save(String header, JsonObject contents){
        try{
            Reader reader = Files.newBufferedReader(file.toPath()); // Read the existing Json
            Map<String, JsonObject> map = GSON.fromJson(reader,Map.class); // Convert the existing Json to a <ap
            FileWriter writer = new FileWriter(file); // Create a file writer
            Map<String,JsonObject> newMap = new LinkedHashMap<>(); // Temporary map to move all the data to
            newMap.put(header, contents); // Put the new data into the temporary Map
            if(!map.isEmpty()){ // Check if the Json data has data.
                for (Map.Entry<String, JsonObject> entry : map.entrySet()) { // For each dataset in the Map
                    if(!entry.getKey().equals(header)) { // If the entry that is fetched is not the same as the new data
                        newMap.put(entry.getKey(), entry.getValue()); // Add the new dataset to the temporary map
                    }
                }
            }
            GSON.toJson(newMap,writer); // Write the temporary map to the file writer
            writer.flush(); // Empty out the writer
            writer.close(); // Close out the writer
// IOException is THROWN, not logged
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void remove(String header){
        try{
            Reader reader = Files.newBufferedReader(file.toPath()); // Read the existing Json
            Map<String, JsonObject> map = GSON.fromJson(reader,Map.class); // Convert the existing Json to a <ap
            FileWriter writer = new FileWriter(file); // Create a file writer
            int[] dale = new int[2];
            Map<String,JsonObject> newMap = new LinkedHashMap<>(); // Temporary map to move all the data to
            if(!map.isEmpty()){ // Check if the Json data has data.
                for (Map.Entry<String, JsonObject> entry : map.entrySet()) { // For each dataset in the Map
                    if(!entry.getKey().equals(header)) { // If the entry that is fetched is not the same as the new data
                        newMap.put(entry.getKey(), entry.getValue()); // Add the new dataset to the temporary map
                    }
                }
            }
            GSON.toJson(newMap,writer); // Write the temporary map to the file writer
            writer.flush(); // Empty out the writer
            writer.close(); // Close out the writer
// IOException is THROWN, not logged
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void save(File file, JsonObject obj){
        try{
            FileWriter writer = new FileWriter(file);
            GSON.toJson(obj,writer);
            writer.flush();
            writer.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public File getFile(){return file;
    }
}
