/*     */
package org.hexic.playermines.managers.data;
/*     */
/*     */

import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.bukkit.configuration.ConfigurationSection;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */
/*     */
/*     */


/*     */
/*     */ public class Config
        /*     */ {
    /*     */   private int comments;
    /*     */   private DataManager manager;
    /*     */   private File file;
    /*     */   private FileConfiguration config;

    /*     */
    /*     */
    public Config(File configFile, int comments, JavaPlugin pl) {
        /*  23 */
        this.comments = comments;
        /*  24 */
        this.manager = new DataManager(pl);
        /*     */
        /*  26 */
        this.file = configFile;
        /*  27 */
        this.config = (FileConfiguration) YamlConfiguration.loadConfiguration(configFile);
        /*     */
    }

    /*     */
    /*     */
    public Object get(String path) {
        /*  31 */
        return this.config.get(path);
        /*     */
    }

    /*     */
    /*     */
    public Object get(String path, Object def) {
        /*  35 */
        return this.config.get(path, def);
        /*     */
    }

    /*     */
    /*     */
    public String getString(String path) {
        /*  39 */
        return this.config.getString(path);
        /*     */
    }

    /*     */
    /*     */
    public String getString(String path, String def) {
        /*  43 */
        return this.config.getString(path, def);
        /*     */
    }

    public String getListValue(String path, int def) {
        return this.config.getStringList(path).get(def);
    }

    /*     */
    /*     */
    public int getInt(String path) {
        /*  47 */
        return this.config.getInt(path);
        /*     */
    }

    /*     */
    /*     */
    public int getInt(String path, int def) {
        /*  51 */
        return this.config.getInt(path, def);
        /*     */
    }

    /*     */
    /*     */
    public boolean getBoolean(String path) {
        /*  55 */
        return this.config.getBoolean(path);
        /*     */
    }

    /*     */
    /*     */
    public boolean getBoolean(String path, boolean def) {
        /*  59 */
        return this.config.getBoolean(path, def);
        /*     */
    }

    /*     */
    /*     */
    public void createSection(String path) {
        /*  63 */
        this.config.createSection(path);
        /*     */
    }

    /*     */
    /*     */
    public ConfigurationSection getConfigurationSection(String path) {
        /*  67 */
        return this.config.getConfigurationSection(path);
        /*     */
    }

    /*     */
    /*     */
    public double getDouble(String path) {
        /*  71 */
        return this.config.getDouble(path);
        /*     */
    }

    /*     */
    /*     */
    public double getDouble(String path, double def) {
        /*  75 */
        return this.config.getDouble(path, def);
        /*     */
    }

    /*     */
    /*     */
    public List<?> getList(String path) {
        /*  79 */
        return this.config.getList(path);
        /*     */
    }

    /*     */
    /*     */
    public List<?> getList(String path, List<?> def) {
        /*  83 */
        return this.config.getList(path, def);
        /*     */
    }

    /*     */
    /*     */
    public boolean contains(String path) {
        /*  87 */
        return this.config.contains(path);
        /*     */
    }

    public boolean contains(String path, Boolean ignoreDefault) {
        return this.config.contains(path, ignoreDefault);
    }

    /*     */
    /*     */
    public void removeKey(String path) {
        /*  91 */
        this.config.set(path, null);
        /*     */
    }

    /*     */
    /*     */
    public void set(String path, Object value) {
        /*  95 */
        this.config.set(path, value);
        /*     */
    }

    /*     */
    /*     */
    public void set(String path, Object value, String comment) {
        /*  99 */
        ArrayList<String> newComments = new ArrayList<>();
        /* 100 */
        String s = "";
        /* 101 */
        int count = 0;
        /* 102 */
        int tempcount = 0;
        /* 103 */
        int commentNum = 0;
        /* 104 */
        for (int i = 0; i < comment.length(); i++) {
            /* 105 */
            if (count <= 50) {
                /* 106 */
                s = s + comment.charAt(i);
                /* 107 */
                count++;
                /*     */
            } else {
                /* 109 */
                while (comment.charAt(i) != ' ') {
                    /* 110 */
                    i--;
                    /* 111 */
                    tempcount++;
                    /* 112 */
                }
                if (tempcount != 0) {
                    /* 113 */
                    s = s.substring(0, s.length() - tempcount);
                    /*     */
                }
                /* 115 */
                s = s + comment.charAt(i);
                /* 116 */
                newComments.add(commentNum, s);
                /* 117 */
                count = tempcount;
                /* 118 */
                tempcount = 0;
                /* 119 */
                commentNum++;
                /* 120 */
                s = "";
                /*     */
            }
            /*     */
        }
        /* 123 */
        if (!s.equalsIgnoreCase("")) {
            /* 124 */
            newComments.add(commentNum, s);
            /*     */
        }
        /*     */
        /* 127 */
        String[] test = new String[newComments.size()];
        /* 128 */
        test = newComments.<String>toArray(test);
        /* 129 */
        set(path, value, test);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public void set(String path, Object value, String[] comment) {
        /* 144 */
        for (String comm : comment) {
            /*     */
            /* 146 */
            if (!this.config.contains(path)) {
                /* 147 */
                this.config.set(this.manager.getPluginName() + "_COMMENT_" + this.comments, " " + comm);
                /* 148 */
                this.comments++;
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /* 153 */
        this.config.set(path, value);
        /*     */
    }

    public void addListValue(String path, String def, String value) {
        this.config.getConfigurationSection(path).addDefault(def, value);
    }

    public void setListValue(String path, String def, Object value) {
        this.config.getConfigurationSection(path).set(def, value);
    }

    /*     */
    /*     */
    /*     */
    public void setHeader(String[] header) {
        /* 158 */
        this.manager.setHeader(this.file, header);
        /* 159 */
        this.comments = header.length + 2;
        /* 160 */
        reloadConfig();
        /*     */
    }

    /*     */
    /*     */
    public void reloadConfig() {
        /* 164 */
        this.config = (FileConfiguration) YamlConfiguration.loadConfiguration(this.file);
        /*     */
    }

    /*     */
    /*     */
    public void saveConfig() {
        /* 168 */
        String config = this.config.saveToString();
        /* 169 */
        this.manager.saveConfig(config, this.file);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public Set<String> getKeys() {
        /* 174 */
        return this.config.getKeys(false);
        /*     */
    }
    /*     */
}


/* Location:              /Users/siiant/Desktop/PickaxeCheckRework.jar!/US/Siiant/PickaxeCheck/datamanager/Config.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */