package org.hexic.playermines.commands.admin;

import org.bukkit.entity.Player;
import org.hexic.playermines.managers.commands.SubCommand;

public class delete extends SubCommand {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void perform(Player player, String[] args) {

    }

    @Override
    public String getPermission() {
        return "PlayerMines.Admin.Delete";
    }
}
