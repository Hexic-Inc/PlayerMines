package org.hexic.playermines.commands.admin;

import org.bukkit.entity.Player;
import org.hexic.playermines.commands.manager.SubCommand;

public class Config extends SubCommand {
    @Override
    public String getName() {
        return "Config";
    }

    @Override
    public String getDescription() {
        return "Manipulate configs in game.";
    }

    @Override
    public void perform(Player player, String[] args) {

    }

    @Override
    public String getPermission() {
        return "PlayerMines.Admin.Config";
    }
}
