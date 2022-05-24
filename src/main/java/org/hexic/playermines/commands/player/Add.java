package org.hexic.playermines.commands.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hexic.playermines.PlayerMine.PlayerMine;
import org.hexic.playermines.data.yml.LangConfig;
import org.hexic.playermines.commands.manager.SubCommand;

public class Add extends SubCommand {
    @Override
    public String getName() {
        return "Add";
    }

    @Override
    public String getDescription() {
        return "Add a player to your private mine.";
    }

    @Override
    public void perform(Player player, String[] args) {
        LangConfig langConfig = new LangConfig(player);
        PlayerMine playerMine = new PlayerMine(player);
        if(!playerMine.hasMine()){
            player.sendMessage(langConfig.getPrefixValue("General-Messages","No_Mine", "&cYou don't own a mine!"));
        } else {
            if(playerMine.getMembers().length + 1 > playerMine.getMemberCap()){
                player.sendMessage(langConfig.getPrefixValue(getName(), "Max_Members", "&cYou already have the maximum amount of members!"));
            } else {
                playerMine.addPlayer(Bukkit.getOfflinePlayer(args[1]));
                player.sendMessage(langConfig.manualStringReplacePrefix(getName(), "Added_Member", "&cSuccessfully added $player$ to your mine!", "$player$", args[1]));
            }
        }
    }

    @Override
    public String getPermission() {
        return "PlayerMines.Player.Add";
    }
}
