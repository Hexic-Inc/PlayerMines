package org.hexic.playermines.data.yml;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.hexic.playermines.Main;
import org.hexic.playermines.data.manager.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MineCrateConfig {


    private static Config config;

    public MineCrateConfig(){
        config = Main.getInitalizer().getDataManager().getConfig("MineCrate.yml");
        config.getKeys();
    }

    public void createConfig(){
        createMaterial();
        createReward1();
        createReward2();
        createReward3();
        createReward4();
        config.saveConfig();
    }

    public Material getMaterial(){
        return Material.valueOf(config.get("MineCrate" +".Material").toString().toUpperCase());
    }

    public void createMaterial(){
        String section = "MineCrate";
        config.createSection(section);
        config.setListValue(section, "Material", "ENDER_CHEST");
    }

    public void createReward1(){
        String section = "Reward1";
        config.createSection(section);
        config.setListValue(section,"Type", "command");
        config.setListValue(section,"Action", "crate give to $player FoodKey 1");
        config.setListValue(section, "Chance", "0.1");
    }

    public void createReward2(){
        String section = "Reward2";
        config.createSection(section);
        config.setListValue(section,"Type", "gems");
        config.setListValue(section,"Action", "10");
        config.setListValue(section, "Chance", "0.1");
        config.setListValue(section, "Message", "&cYou were given 10 gems!");
    }

    public void createReward3(){
        String section = "Reward3";
        config.createSection(section);
        config.setListValue(section,"Type", "tokens");
        config.setListValue(section,"Action", "1000");
        config.setListValue(section, "Chance", "0.1");
        config.setListValue(section, "Message", "&cYou were given 1000 tokens!");
    }

    public void createReward4(){
        String section = "Reward4";
        config.createSection(section);
        config.setListValue(section,"Type", "dollars");
        config.setListValue(section,"Action", "10000");
        config.setListValue(section, "Chance", "1");
        config.setListValue(section, "Message", "&cYou were given 10000 dollars!");
    }

    public Map<String, String> getSection(String section){
        ConfigurationSection configurationSection = config.getConfigurationSection(section);
        Map<String, String> tempMap = new HashMap<>();
        for(String set : configurationSection.getKeys(false)){
            tempMap.put(set,configurationSection.getString(set));
        }
        return tempMap;
    }

    public Set<String> getSections(){
        return config.getKeys();
    }

    public String getValue(String section, String key){
        return(config.getString(section + "."+ key));
    }

    public boolean sectionContainsKey(String section, String key){
        return config.contains(section + "." + key);
    }


}
