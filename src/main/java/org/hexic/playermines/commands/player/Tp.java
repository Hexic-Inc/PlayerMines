package org.hexic.playermines.commands.player;

import org.bukkit.entity.Player;
import org.hexic.playermines.data.yml.LangConfig;
import org.hexic.playermines.managers.commands.SubCommand;
import org.hexic.playermines.PlayerMine.PlayerMine;

public class Tp extends SubCommand {
    @Override
    public String getName() {
        return "Tp";
    }

    @Override
    public String getDescription() {
        return new LangConfig().getValue( getName(), "description","&cTeleport to a players mine.");
    }

    @Override
    public void perform(Player player, String[] args) {
        LangConfig lang = new LangConfig();
        if (args.length < 2) {
            if(new PlayerMine(player).hasMine()){
                new PlayerMine(player).teleport(player);
                player.sendMessage(lang.getPrefixValue(getName(), "TP_Message", "&cYou've been teleported!"));
                return;
            }
        }
        if (new PlayerMine(args[1]).hasMine()) {
            new PlayerMine(args[1]).teleport(player);
            player.sendMessage(lang.getPrefixValue(getName(), "TP_Message", "&cYou've been teleported!"));
        } else {
            player.sendMessage(lang.getPrefixValue(getName(), "Invalid_Mine", "&cPlayer does not have a mine!"));
        }
    }

    @Override
    public String getPermission() {
        return "PlayerMines.Teleport";
    }
}
