
package org.hexic.playermines.data.manager;



import java.io.File;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Set;
 import org.bukkit.configuration.ConfigurationSection;
 import org.bukkit.configuration.file.FileConfiguration;
 import org.bukkit.configuration.file.YamlConfiguration;
 import org.bukkit.plugin.java.JavaPlugin;






 public class Config
         {
       private int comments;
       private DataManager manager;
       private File file;
       private FileConfiguration config;



    public Config(File configFile, int comments, JavaPlugin pl) {

        this.comments = comments;

        this.manager = new DataManager(pl);


        this.file = configFile;

        this.config = (FileConfiguration) YamlConfiguration.loadConfiguration(configFile);

    }



    public Object get(String path) {

        return this.config.get(path);

    }



    public Object get(String path, Object def) {

        return this.config.get(path, def);

    }



    public String getString(String path) {

        return this.config.getString(path);

    }



    public String getString(String path, String def) {

        return this.config.getString(path, def);

    }

    public String getListValue(String path, int def) {
        return this.config.getStringList(path).get(def);
    }



    public int getInt(String path) {

        return this.config.getInt(path);

    }



    public int getInt(String path, int def) {

        return this.config.getInt(path, def);

    }



    public boolean getBoolean(String path) {

        return this.config.getBoolean(path);

    }



    public boolean getBoolean(String path, boolean def) {

        return this.config.getBoolean(path, def);

    }



    public void createSection(String path) {

        this.config.createSection(path);

    }



    public ConfigurationSection getConfigurationSection(String path) {

        return this.config.getConfigurationSection(path);

    }



    public double getDouble(String path) {

        return this.config.getDouble(path);

    }



    public double getDouble(String path, double def) {

        return this.config.getDouble(path, def);

    }



    public List<?> getList(String path) {

        return this.config.getList(path);

    }



    public List<?> getList(String path, List<?> def) {

        return this.config.getList(path, def);

    }



    public boolean contains(String path) {

        return this.config.contains(path);

    }

    public boolean contains(String path, Boolean ignoreDefault) {
        return this.config.contains(path, ignoreDefault);
    }



    public void removeKey(String path) {

        this.config.set(path, null);

    }



    public void set(String path, Object value) {

        this.config.set(path, value);

    }



    public void set(String section, String key, Object value, String comment) {
        ArrayList<String> newComments = new ArrayList<>();
        StringBuilder s = new StringBuilder();
        int count = 0;
        int tempCount = 0;
        int commentNum = 0;
        for (int i = 0; i < comment.length(); i++) {
            if (count <= 50) {
                s.append(comment.charAt(i));
                count++;
            } else {
                while (comment.charAt(i) != ' ') {
                    i--;
                    tempCount++;
                }
                if (tempCount != 0) {
                    s = new StringBuilder(s.substring(0, s.length() - tempCount));
                }
                s.append(comment.charAt(i));
                newComments.add(commentNum, s.toString());
                count = tempCount;
                tempCount = 0;
                commentNum++;
                s = new StringBuilder();
            }
        }
        if (!s.toString().equalsIgnoreCase("")) {
            newComments.add(commentNum, s.toString());
        }
        set(section,key, value, newComments.toArray(new String[0]));

    }

    private void set(String section, String key, Object value, String[] comment) {
        for (String comm : comment) {
            if (!this.config.contains(section + "." + key)) {
                this.config.set(this.manager.getPluginName() + "_COMMENT_" + this.comments, " " + comm);
                this.comments++;
            }
        }
        this.config.set(section + "." + key, value);

    }

    public void addListValue(String path, String def, String value) {
        this.config.getConfigurationSection(path).addDefault(def, value);
    }

    public void setListValue(String path, String def, Object value) {
        this.config.getConfigurationSection(path).set(def, value);
    }




    public void setHeader(String[] header) {

        this.manager.setHeader(this.file, header);

        this.comments = header.length + 2;

        reloadConfig();

    }



    public void reloadConfig() {

        this.config = (FileConfiguration) YamlConfiguration.loadConfiguration(this.file);

    }



    public void saveConfig() {

        String config = this.config.saveToString();

        this.manager.saveConfig(config, this.file);

    }




    public Set<String> getKeys() {

        return this.config.getKeys(false);

    }

}