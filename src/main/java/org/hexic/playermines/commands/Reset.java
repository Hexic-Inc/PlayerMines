package org.hexic.playermines.commands;

import org.bukkit.entity.Player;
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
        if(new PlayerMine(player).hasMine()){
            new PlayerMine(player).reset();
            player.sendMessage(lang.getPrefixValue(getName(), "Reset_Message", "&cMine has been reset!"));
            return;
        } else {
            player.sendMessage(lang.getPrefixValue(getName(), "No_Mine", "&cYou don't own a mine to reset!"));
        }
    }
}
