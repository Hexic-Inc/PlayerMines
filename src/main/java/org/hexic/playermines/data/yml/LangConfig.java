package org.hexic.playermines.data.yml;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;
import org.hexic.playermines.Main;
import org.hexic.playermines.PlayerMine.PlayerMine;
import org.hexic.playermines.PlayerMine.Upgrade;
import org.hexic.playermines.data.PlaceHolder;
import org.hexic.playermines.managers.data.Config;

import java.util.ArrayList;

public class LangConfig {

    private final Config config;
    private Player player;
    private Upgrade upgrade;

    public LangConfig(Player player){
        this.player = player;
        config = Main.getInitalizer().getDataManager().getConfig("Lang.yml");
    }

    public LangConfig(Player player, Upgrade upgrade){
        this.player = player;
        this.upgrade = upgrade;
        config = Main.getInitalizer().getDataManager().getConfig("Lang.yml");
    }
    public LangConfig(){
        config = Main.getInitalizer().getDataManager().getConfig("Lang.yml");
    }


    /**
     * Manually Replace an Arraylist of PlaceHolder's, to the replacement ArrayList.
     * @param rawMessage Message that contains the placeholder.
     * @param placeHolders PlaceHolders that will be replaced in rawMessage.
     * @param replacements Replacements that will take the same spot as the placeholder. Defaults to normal placeholder replacements if null/none provided.
     * @return Replaced string with all replaced placeholders.
     */
    public String manualPlHolReplace(String rawMessage, ArrayList<PlaceHolder> placeHolders, ArrayList<String> replacements){
        String newMessage = rawMessage;
        for(int i = 0; i < placeHolders.size(); i++){
            if(rawMessage.contains(placeHolders.get(i).toString().toLowerCase())){
                if(replacements.size() < i || replacements.get(i).equals("") || replacements.get(i).isEmpty()){
                    newMessage = replacePlaceHolder(newMessage);
                } else {
                    newMessage = newMessage.replace(placeHolders.get(i).toString().toLowerCase(), replacements.get(i));
                }
            }
        }
        return newMessage;
    }

    /**
     * Manually Replace an Arraylist of placeholders as strings, to the replacement arraylist.
     * @param rawMessage Message that contains the placeholder.
     * @param placeHolders Placeholders that will be replaced in rawMessage.
     * @param replacements Replacements that will take the same spot as the placeholder. Defaults to normal placeholder replacements if null/none provided.
     * @return Replaced string with all replaced placeholders.
     */
    public String manualStringReplace(String rawMessage, ArrayList<String> placeHolders, ArrayList<String> replacements){
        String newMessage = rawMessage;
        for(int i = 0; i < placeHolders.size(); i++){
            if(rawMessage.contains(placeHolders.get(i).toLowerCase())){
                if(replacements.size() < i || replacements.get(i).equals("") || replacements.get(i).isEmpty()){
                    newMessage = replacePlaceHolder(newMessage);
                } else {
                    newMessage = newMessage.replace(placeHolders.get(i).toLowerCase(), replacements.get(i));
                }
            }
        }
        return newMessage;
    }

    private String replacePlaceHolder(String rawString){
        String newString = rawString;
        for (PlaceHolder holder : PlaceHolder.values()) {
            String placeHolder = holder.toString().toLowerCase();
            if (newString.toLowerCase().contains(placeHolder)) {
                newString = newString.replace(placeHolder, placeHolderData(placeHolder));
            }
        }
        return newString;
    }

    private String placeHolderData(String placeHolder){
        if(placeHolder.toLowerCase().contentEquals("$upgrade$")){
            if(new PlayerMine(player).getUpgradeString(upgrade).contains("_")){
                return new PlayerMine(player).getUpgradeString(upgrade).replace("_", " ");
            } else {
                return new PlayerMine(player).getUpgradeString(upgrade);
            }
        }
        if(placeHolder.toLowerCase().contains("$upgrade_cost$")){
            return new PlayerMine(player).getUpgradeCost(upgrade,1) + "";
        }
        if(placeHolder.toLowerCase().contains("$upgrade_currency$")){
            return new PlayerMine(player).getUpgradeType(upgrade);
        }
        return placeHolder;
    }

    public String getValue(String section, String key, String defaultValue){
        if(config.get(section) == null){
            config.createSection(section);
            config.setListValue(section, key, defaultValue);
            config.saveConfig();
        }
        if(config.getString(section + "." + key) == null){
            config.setListValue(section, key, defaultValue);
            config.saveConfig();
        }
        return translateToColorCode(replacePlaceHolder( config.getString(section + "." + key)));
    }

    public String getPrefixValue(String section, String key, String defaultValue){
        return getPrefix() + getValue(section,key,defaultValue);
    }

    public String getPrefixValue(String section, String key){
        return translateToColorCode(getPrefix() + getValue(section,key,"test"));
    }

    public String addPrefixValue(String message){
        return translateToColorCode(getPrefix() + message);
    }

    private String getPrefix(){
        if(config.getString("Prefix") == null){
            config.set("Prefix", "&6P&eM&8>> ");
            config.saveConfig();
        }
        return translateToColorCode(config.getString("Prefix"));
    }

    private String translateToColorCode(String toConvert){
        return ChatColor.translateAlternateColorCodes('&', toConvert);
    }

}
