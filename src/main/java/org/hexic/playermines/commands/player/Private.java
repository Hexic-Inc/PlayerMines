package org.hexic.playermines.commands.player;

import org.bukkit.entity.Player;
import org.hexic.playermines.PlayerMine.PlayerMine;
import org.hexic.playermines.data.yml.LangConfig;
import org.hexic.playermines.commands.manager.SubCommand;

public class Private extends SubCommand {
    @Override
    public String getName() {
        return "Private";
    }

    @Override
    public String getDescription() {
        return "Set your mine to public or private";
    }

    @Override
    public void perform(Player player, String[] args) {
        PlayerMine playerMine = new PlayerMine(player);
        LangConfig langConfig = new LangConfig(player);
        if(!playerMine.hasMine()){
            player.sendMessage(langConfig.getPrefixValue("General-Messages","No_Mine", "&cYou don't own a mine!"));
        } else {
            if(playerMine.isPublic()){
                playerMine.setPublic(false);
                player.sendMessage(langConfig.getPrefixValue(getName(), "Now_Private", "&cYou made your PlayerMine private."));
            } else {
                playerMine.setPublic(true);
                player.sendMessage(langConfig.getPrefixValue(getName(), "Now_Private", "&cYou made your PlayerMine public."));
            }
        }
    }

    @Override
    public String getPermission() {
        return "PlayerMines.Player.Private";
    }
}
