package org.hexic.playermines.commands.admin;

import org.bukkit.entity.Player;
import org.hexic.playermines.data.yml.LangConfig;
import org.hexic.playermines.commands.manager.SubCommand;
import org.hexic.playermines.PlayerMine.PlayerMine;

public class TP extends SubCommand {
    @Override
    public String getName() {
        return "TP";
    }

    @Override
    public String getDescription() {
        return "Allows admins to teleport to mine without the surrounding border.";
    }

    @Override
    public void perform(Player player, String[] args) {
        LangConfig lang = new LangConfig();
        if (args.length == 3) {
            if(new PlayerMine(args[2]).hasMine()){
                player.teleport(new PlayerMine(args[2]).getTeleportLocation());
                new PlayerMine(args[2]).removeBorder(player);
                player.sendMessage(lang.getPrefixValue(getName(), "TP_Message", "&cYou've been teleported!"));
                return;
            }
        }
        if (new PlayerMine(player).hasMine()) {
            player.teleport(new PlayerMine(player).getTeleportLocation());
            new PlayerMine(player).removeBorder(player);
            player.sendMessage(lang.getPrefixValue(getName(), "TP_Message", "&cYou've been teleported!"));
        } else {
            player.sendMessage(lang.getPrefixValue(getName(), "Invalid_Mine", "&cPlayer does not have a mine!"));
        }
    }

    @Override
    public String getPermission() {
        return "PlayerMines.Admin.Teleport";
    }
}
