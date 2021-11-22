package org.hexic.playermines.handlers;

import org.bukkit.entity.Player;
import org.hexic.playermines.PlayerMines;
import org.hexic.playermines.data.yml.LangConfig;
import org.hexic.playermines.world.Upgrade;
import org.hexic.playermines.world.PlayerMine;

public class ActionHandler {

    enum Action{
        TP,
        RESET;
    }

    private String string;
    private LangConfig config;

    public ActionHandler(String str){
        string = str;
    }

    public String getAction(){
        return  string.split(";")[1];
    }

    public String getType(){
        return string.split(";")[0];
    }

    public void doAction(Player player, int levelToAdd){
        PlayerMine playerMine = new PlayerMine(player);
        this.config = new LangConfig(player);
        if(getType().toLowerCase().contains("gui")){
            player.openInventory(new GuiHandler(getAction(),player).getGui());
        } else if (getType().toLowerCase().contains("command")){
            if(getAction().toUpperCase().contains(Action.TP.toString())){
                player.teleport(player);
                player.sendMessage(config.getPrefixValue(Action.TP.toString().toLowerCase(), "TP_Message", "&cYou've been teleported!"));
            }
            if(getAction().toUpperCase().contains(Action.RESET.toString())){
                if(PlayerMines.getInitalizer().getCoolDownHandler().isPlayerInCoolDown(player)){
                    player.sendMessage(config.getPrefixValue("reset", "Cool_Down", "&cYou must wait $time seconds to reset your mine.").replace("$time", PlayerMines.getInitalizer().getCoolDownHandler().getTimeLeft(player) + ""));
                    return;
                }
                playerMine.reset();
                PlayerMines.getInitalizer().getCoolDownHandler().addPlayerToMap(player,new PlayerMine(player).getResetTime());
                player.sendMessage(config.getPrefixValue(Action.RESET.toString().toLowerCase(), "Reset_Message","&cMine has been reset!"));
            }
        } else if(getType().toLowerCase().contains("upgrade")){
            if(!playerMine.hasEnoughForUpgrade(Upgrade.valueOf(getAction().toUpperCase()),levelToAdd)){
                player.sendMessage(new LangConfig(player,Upgrade.valueOf(getAction().toUpperCase())).getPrefixValue("Upgrade-Messages", "Not-Enough", "&cYou don't have enough $upgrade_currency for that upgrade."));
                return;
            }
            if(playerMine.addPurchasedUpgrade(Upgrade.valueOf(getAction().toUpperCase()),levelToAdd)){
                if(new LangConfig(player, Upgrade.valueOf(getAction().toUpperCase())).getValue("Upgrade-Messages", "General-Message", "&c$upgrade Upgrade cost $upgrade_cost $upgrade_currency for $levels_added level(s).").contains("$levels_added")){
                    player.sendMessage(new LangConfig(player,Upgrade.valueOf(getAction().toUpperCase())).getPrefixValue("Upgrade-Messages", "General-Message").replace("$levels_added", levelToAdd + ""));
                } else {
                    player.sendMessage(new LangConfig(player,Upgrade.valueOf(getAction().toUpperCase())).getPrefixValue("Upgrade-Messages", "General-Message"));
                }
                new GuiHandler().reloadGui(player,player.getOpenInventory().getTopInventory());
            } else {
                player.sendMessage(config.getPrefixValue("Upgrade-Messages", "Max-Level", "&cYou've already maxed out that upgrade!"));
            }


        }
    }
}
