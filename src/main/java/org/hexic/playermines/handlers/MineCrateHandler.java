package org.hexic.playermines.handlers;

import dev.drawethree.ultraprisoncore.UltraPrisonCore;
import dev.drawethree.ultraprisoncore.api.enums.ReceiveCause;
import dev.drawethree.ultraprisoncore.gems.api.UltraPrisonGemsAPI;
import dev.drawethree.ultraprisoncore.tokens.api.UltraPrisonTokensAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.hexic.playermines.Main;
import org.hexic.playermines.data.yml.LangConfig;
import org.hexic.playermines.data.yml.MineCrateConfig;

import java.util.HashMap;
import java.util.Map;

public class MineCrateHandler {

    private Material material;
    private MineCrateConfig mineCrateConfig;

    public MineCrateHandler(){
        mineCrateConfig = new MineCrateConfig();
        material = mineCrateConfig.getMaterial();
    }
    public Material getMaterial(){
        return material;
    }

    public Map<String, String> getRewards(){
        Map<String,String> tempMap = new HashMap<>();
        for(String section :mineCrateConfig.getSections()){
            if(section.toLowerCase().contains("reward")){
                tempMap.put(mineCrateConfig.getValue(section,"Type"),mineCrateConfig.getValue(section,"Action"));
            }
        }
        return tempMap;
    }

    public void giveRewards(Player player){
        for(String section :mineCrateConfig.getSections()) {
            if (section.toLowerCase().contains("reward")) {
                if (Math.random() <= Double.parseDouble(mineCrateConfig.getValue(section, "Chance"))) {
                    if (mineCrateConfig.getValue(section, "Type").contains("command")) {
                        String command;
                        if (mineCrateConfig.getValue(section, "Action").contains("$player")) {
                            command = mineCrateConfig.getValue(section, "Action").replace("$player", player.getName());
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                        }
                    } else if (mineCrateConfig.getValue(section, "Type").contains("gems")) {
                        UltraPrisonGemsAPI ultraPrisonGemsAPI = UltraPrisonCore.getInstance().getGems().getApi();
                        ultraPrisonGemsAPI.addGems(player, Integer.parseInt(mineCrateConfig.getValue(section, "Action")), ReceiveCause.GIVE);
                    } else if (mineCrateConfig.getValue(section, "Type").contains("tokens")) {
                        UltraPrisonTokensAPI ultraPrisonTokensAPI = UltraPrisonCore.getInstance().getTokens().getApi();
                        ultraPrisonTokensAPI.addTokens(player, Integer.parseInt(mineCrateConfig.getValue(section, "Action")), ReceiveCause.GIVE);
                    } else if (mineCrateConfig.getValue(section, "Type").contains("dollars")) {
                        Main.getInitalizer().getEcon().depositPlayer(player, Integer.parseInt(mineCrateConfig.getValue(section, "Action")));
                    }
                    if(mineCrateConfig.sectionContainsKey(section, "Message")){
                        player.sendMessage(new LangConfig().addPrefixValue(mineCrateConfig.getValue(section, "Message")));
                    }
                }
            }
        }

    }



}
