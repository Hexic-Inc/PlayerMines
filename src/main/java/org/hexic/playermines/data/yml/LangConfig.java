package org.hexic.playermines.data.yml;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hexic.playermines.PlayerMines;
import org.hexic.playermines.managers.data.Config;
import org.hexic.playermines.world.Upgrade;

public class LangConfig {

    private Config config;

    public LangConfig(Player player){
        config = PlayerMines.getInitalizer().getDataManager().getConfig("Lang.yml");
    }

    public LangConfig(Player player, Upgrade upgrade){
        config = PlayerMines.getInitalizer().getDataManager().getConfig("Lang.yml");
    }
    public LangConfig(){
        config = PlayerMines.getInitalizer().getDataManager().getConfig("Lang.yml");
    }


    public String getValue(String section, String key, String defaultValue){
        if(config.get(section) == null){
            config.createSection(section);
            config.setListValue(section, key, defaultValue);
            config.saveConfig();
            return translateToColorCode(defaultValue);
        }
        if(config.getString(section + "." + key) == null){
            config.setListValue(section, key, defaultValue);
            config.saveConfig();
            return translateToColorCode(defaultValue);
        }
        if(config.getString(section + "." + key).contains(defaultValue)){
            return translateToColorCode( defaultValue);
        }
        return translateToColorCode( config.getString(section + "." + key));
    }

    public String getPrefixValue(String section, String key, String defaultValue){
        if(config.get(section) == null){
            config.createSection(section);
            config.setListValue(section, key, defaultValue);
            config.saveConfig();
            return getPrefix() +translateToColorCode( defaultValue);
        }
        if(config.getString(section + "." + key) == null){
            config.setListValue(section, key, defaultValue);
            config.saveConfig();
            return getPrefix() +translateToColorCode( defaultValue);
        }
        if(config.getString(section + "." + key).contains(defaultValue)){
            return getPrefix() +translateToColorCode(defaultValue);
        }
        return getPrefix() + translateToColorCode(config.getString(section + "." + key));
    }

    public String getPrefixValue(String section, String key){
        return translateToColorCode(getPrefix() + config.getString(section + "." + key));
    }

    public String addPrefixValue(String message){
        return translateToColorCode(getPrefix() + message);
    }

    private String getPrefix(){
        if(config.getString("Prefix") == null){
            config.set("Prefix", "&6P&eM&8>> ");
            config.saveConfig();
        }
        return ChatColor.translateAlternateColorCodes('&', config.getString("Prefix"));
    }

    private String translateToColorCode(String toConvert){
        return ChatColor.translateAlternateColorCodes('&', toConvert);
    }

}
