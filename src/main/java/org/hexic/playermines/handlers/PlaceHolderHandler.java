package org.hexic.playermines.handlers;

import org.bukkit.entity.Player;
import org.hexic.playermines.world.PlayerMine;
import org.hexic.playermines.world.Upgrade;

import java.util.ArrayList;

public class PlaceHolderHandler {

    private Player player;
    private ArrayList<String> placeHolders = new ArrayList<>();
    private Upgrade upgrade;

    public PlaceHolderHandler(Player player, Upgrade upgrade){
        this.player = player;
        this.upgrade = upgrade;
        loadPlaceHolders();
    }

    public PlaceHolderHandler(Player player){
        this.player = player;
        loadPlaceHolders();
    }

   public PlaceHolderHandler(){
        loadPlaceHolders();
   }

    public void loadPlaceHolders(){
        placeHolders.add("$player");
        placeHolders.add("$upgrade_level");
        placeHolders.add("$upgrade_max");
        placeHolders.add("$upgrade_cost");
        placeHolders.add("$upgrade_currency");
        placeHolders.add("$upgrade");
        //"$levels_added" this can't be added into this handler for the time being, so it's considered a manual place holder.
    }

    public String getPlaceHolderValue(String placeHolder){
        if(placeHolder.contains("$player")) {
            return player.getName();
        }
        if(placeHolder.contains("$upgrade_level")){
            return new PlayerMine(player).getUpgradeLevel(upgrade) + "";
        }
        if(placeHolder.contains("$upgrade_max")){
            return new PlayerMine(player).getMaxUpgradeLevel(upgrade) + "";
        }
        if(placeHolder.contains("$upgrade_cost")){
            return new PlayerMine(player).getUpgradeCost(upgrade, 0) + "";
        }
        if(placeHolder.contains("$upgrade_currency")){
            return new PlayerMine(player).balType(upgrade);
        }
          if(placeHolder.contains("$upgrade")){
              return new PlayerMine(player).upgradeAsString(upgrade);
          }
        return "";
    }

    public String replace(String string){
        String replaced = string;
        for(int i = 0; i < placeHolders.size(); i++){
            if(string.contains(placeHolders.get(i))){
                replaced = replaced.replace(placeHolders.get(i),getPlaceHolderValue(placeHolders.get(i)));
            }
        }
        return replaced;
    }
}
