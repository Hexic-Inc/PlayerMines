package org.hexic.playermines.data.yml;

import org.bukkit.configuration.ConfigurationSection;
import org.hexic.playermines.PlayerMines;
import org.hexic.playermines.managers.data.Config;

import java.util.HashMap;
import java.util.Map;

public class YmlConfig {

    private static Config config;

    public YmlConfig(){
        config = PlayerMines.getInitalizer().getDataManager().getConfig("Config.yml");
    }

    public void createConfigs(){
        createGeneralSettings();
        createEnchantChances();
        createEnchantCaps();
        createEnchantTriggered();
        createCost();
        config.saveConfig();
    }


    private void createGeneralSettings(){
        String section = "general-settings";
        config.createSection(section);
        config.setListValue(section,"World-Name", "pmine");
        config.setListValue(section,"Area-Size","300");
        config.setListValue(section,"Default-Schem", "default");
        config.setListValue(section,"Mine-Cords", "125,100,125; 175,50,175");
        config.setListValue(section, "TP-Location", "149,101,114");
        config.setListValue(section,"Mine-Contents","[99;stone, 1;end_stone]");
        config.setListValue(section,"Mine-Flags", "{block-break;allow}, {block-place;deny}");
        config.setListValue(section,"Global-Flags", "{block-break;deny}, {chorus-fruit-teleport;deny}, {mob-spawning;deny}, {enderpearl;deny}, {block-place;deny}, {natural-hunger-drain;deny}");
    }

    private void createCost(){
        String section = "Enchant_Costs";
        config.createSection(section);
        config.setListValue(section, "Berserk", "$50000 * level");
        config.setListValue(section, "Gem_Drops", "T30 * level");
        config.setListValue(section, "Upgrade_Finder", "G30 * level");
        config.setListValue(section, "MineCrate_Finder", "T30 * level");
        config.setListValue(section, "EToken_Finder", "T20 * level");
        config.setListValue(section, "Regen_Time", "G20 * level");
        config.setListValue(section, "Mine_Multiplier", "$10000 * level");
        config.setListValue(section, "Rent_Price", "T50 * level");
        config.setListValue(section, "Tax_Price", "T50 * level");
        config.setListValue(section, "Size", "G50 * level");
    }

    private void createEnchantChances(){
        String section = "Enchant_Chances";
        config.createSection(section);
        config.setListValue(section, "Berserk", "0.01 * level");
        config.setListValue(section, "Gem_Drops", "0.01 * level");
        config.setListValue(section, "Upgrade_Finder", "0.01 * level");
        config.setListValue(section, "MineCrate_Finder", "0.01 * level");
        config.setListValue(section, "EToken_Finder", "0.01 * level");
        config.setListValue(section, "Mine_Multiplier", "1");
        config.setListValue(section, "Rent_Price", "1");
        config.setListValue(section, "Tax_Price", "1");
    }

    private void createEnchantCaps(){
        String section = "Enchant_Caps";
        config.createSection(section);
        config.setListValue(section, "Berserk", "30");
        config.setListValue(section, "Gem_Drops", "30");
        config.setListValue(section, "Upgrade_Finder", "30");
        config.setListValue(section, "MineCrate_Finder", "30");
        config.setListValue(section, "EToken_Finder", "30");
        config.setListValue(section, "Regen_Time", "60");
        config.setListValue(section, "Mine_Multiplier", "30");
        config.setListValue(section, "Rent_Price", "30");
        config.setListValue(section, "Tax_Price", "30");
        config.setListValue(section, "Size", "50");
    }

    private void createEnchantTriggered(){
        String section = "Enchant_Triggered";
        config.createSection(section);
        config.setListValue(section, "Gem_Drops", "10 * level");
        config.setListValue(section, "EToken_Finder", "300 * level");
        config.setListValue(section, "Regen_Time", "120 - level");
        config.setListValue(section, "Rent_Price", "10000 * level");
        config.setListValue(section, "Tax_Price", "10000 * level");
        config.setListValue(section, "Size", "1");
        config.setListValue(section, "Berserk", "1.68 * level");
        config.setListValue(section, "Berserk_Count", "5");
        config.setListValue(section, "Mine_Multiplier", "0.1 * level");
    }

    public Map<String, String> getSection(String section){
        ConfigurationSection configurationSection = config.getConfigurationSection(section);
        Map<String, String> tempMap = new HashMap<>();
        for(String set : configurationSection.getKeys(false)){
            tempMap.put(set,configurationSection.getString(set));
        }
        return tempMap;
    }

    public String getTPLocation(){return config.getString("general-settings" + ".TP-Location");}

    public String getSectionValue(String section, String key){
        return config.getString(section + "." + key);
    }

    public String getWorldName(){return config.getString("general-settings" + ".World-Name");}

    public String getPmineContents(){return config.getString("general-settings" + ".Mine-Contents");}

    public String getAreaSize(){return config.getString("general-settings" + ".Area-Size");}

    public String getDefaultSchem(){return config.getString("general-settings" + ".Default-Schem");}

    public String getMineCords(){return config.getString("general-settings" + ".Mine-Cords");}

    public Map<String, Boolean> getGlobalRegionFlags(){
       String flags = config.getString("general-settings.Global-Flags");
       String temp = flags;
       if(flags.contains(" ")){
           temp = flags.replace(" ", "");
       }
        String[] split = new String[1];
        if(flags.contains(",")){
            split = temp.split(",");
        } else {
            split[0] = flags;
        }
       Map<String, Boolean> flagMap = new HashMap<>();
       String temp1;
       boolean bool;
       String[] split2;
        for (String s : split) {
            temp1 = s;
            if (temp1.contains("{")) {
                temp1 = temp1.replace("{", "");
            }
            if (temp1.contains("}")) {
                temp1 = temp1.replace("}", "");
            }
            split2 = temp1.split(";");
            bool = split2[1].toLowerCase().contains("allow");
            flagMap.put(split2[0], bool);
        }
       return flagMap;
    }

    public Map<String, Boolean> getMineRegionFlags(){
        String flags = config.getString("general-settings.Mine-Flags");
        String temp = flags;
        if(flags.contains(" ")){
            temp = flags.replace(" ", "");
        }
        String[] split = new String[1];
        if(flags.contains(",")){
            split = temp.split(",");
        } else {
            split[0] = flags;
        }
        Map<String, Boolean> flagMap = new HashMap<>();
        String temp1;
        boolean bool;
        String[] split2;
        for (String s : split) {
            temp1 = s;
            if (temp1.contains("{")) {
                temp1 = temp1.replace("{", "");
            }
            if (temp1.contains("}")) {
                temp1 = temp1.replace("}", "");
            }
            split2 = temp1.split(";");
            bool = split2[1].toLowerCase().contains("allow");
            flagMap.put(split2[0], bool);
        }
        return flagMap;
    }


}
