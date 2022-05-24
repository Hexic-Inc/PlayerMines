package org.hexic.playermines.commands.player;

import org.bukkit.entity.Player;
import org.hexic.playermines.data.yml.LangConfig;
import org.hexic.playermines.commands.manager.SubCommand;
import org.hexic.playermines.PlayerMine.PlayerMine;

public class Create extends SubCommand {
    @Override
    public String getName() {
        return "Create";
    }

    @Override
    public String getDescription() {
        return new LangConfig().getValue(getName(), "description","&cCreate your private mine.");
    }

    @Override
    public void perform(Player player, String[] args) {
        PlayerMine pMine = new PlayerMine(player);
        LangConfig lang = new LangConfig(player);
        if(pMine.hasMine()){
            player.sendMessage(lang.getPrefixValue(getName(), "Mine_Exists", "&cYou already own a mine!"));
            return;
        }
        if(!pMine.createMine()){
            player.sendMessage(lang.getPrefixValue(getName(), "Mine_Queue", "&cPlease wait while we make your mine."));
        }
    }

    @Override
    public String getPermission() {
        return "PlayerMines.Player.Create";
    }
}
