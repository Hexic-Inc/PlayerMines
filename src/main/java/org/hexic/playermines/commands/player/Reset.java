package org.hexic.playermines.commands.player;

import org.bukkit.entity.Player;
import org.hexic.playermines.PlayerMines;
import org.hexic.playermines.data.yml.LangConfig;
import org.hexic.playermines.managers.commands.SubCommand;
import org.hexic.playermines.world.PlayerMine;

public class Reset extends SubCommand {
    @Override
    public String getName() {
        return "reset";
    }

    @Override
    public String getDescription() {
        return new LangConfig().getValue( getName(), "description","&cReset your private mine.");
    }

    @Override
    public void perform(Player player, String[] args) {
        LangConfig lang = new LangConfig(player);
        if(PlayerMines.getInitalizer().getCoolDownHandler().isPlayerInCoolDown(player)){
            player.sendMessage(lang.getPrefixValue(getName(), "Cool_Down", "&cYou must wait $time seconds to reset your mine.").replace("$time", PlayerMines.getInitalizer().getCoolDownHandler().getTimeLeft(player) + ""));
            return;
        }
        if(!new PlayerMine(player).hasMine()){
            player.sendMessage(lang.getPrefixValue(getName(), "No_Mine", "&cYou don't own a mine to reset!"));
        } else {
            new PlayerMine(player).reset();
            PlayerMines.getInitalizer().getCoolDownHandler().addPlayerToMap(player,new PlayerMine(player).getResetTime());
            player.sendMessage(lang.getPrefixValue(getName(), "Reset_Message", "&cMine has been reset!"));
            return;
        }
    }
}
