package org.hexic.playermines.init;

import org.hexic.playermines.data.manager.Config;
import org.hexic.playermines.data.manager.DataManager;
import org.hexic.playermines.data.manager.annotations.Comment;
import org.hexic.playermines.data.manager.annotations.Filename;
import org.hexic.playermines.data.manager.annotations.Section;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Configs <T>{

    private final DataManager dataManager;

    /**
     * Init the Config Initializer
     */
    public Configs(DataManager manager){
        this.dataManager = manager;
    }

    public void initConfig(T config){
        Map<String, String> comments = getComments(config);
        Map<String, Map<String,String>> fileContents = getContents(config);
        Config ymlConfig = dataManager.getConfig(getFileName(config));
        fileContents.forEach((section,contents) -> {
            if(comments.containsKey(section)){
                //Get the first Key and Value of the Contents
            }
            contents.forEach((key,value) -> {
                ymlConfig.set(section,key,value,contents.get(section));
            });
        });

    }

    /**
     * Get the comments for each section
     * @param config Config to retrieve the comments for.
     * @return All config comments, in order of Section, Comment
     */
    private Map<String,String> getComments(T config){
        Class<?> clazz = config.getClass();
        Map<String,String> fileComments = new HashMap<>();
        try {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getReturnType().isInstance(new HashMap<String, String>()) && method.isAnnotationPresent(Comment.class) ) {
                    method.setAccessible(true);
                    fileComments.put(method.getAnnotation(Section.class).section(),method.getAnnotation(Comment.class).comment());
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return fileComments;
    }

    /**
     * Get the entire contents of a config.
     * @param config Config to retrieve the contents for.
     * @return Entire files contents.
     */
    private Map<String,Map<String,String>> getContents(T config){
        Class<?> clazz = config.getClass();
        Map<String, Map<String,String>> fileContents = new HashMap<>();
        try {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getReturnType().isInstance(new HashMap<String, String>()) && method.isAnnotationPresent(Section.class) ) {
                    method.setAccessible(true);
                    fileContents.put(method.getAnnotation(Section.class).section(),(Map<String, String>) method.invoke(config));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return fileContents;
    }

    private String getFileName(T config){
        try {
            for (Field field : config.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Filename.class)) {
                    return (String) field.get(config);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
