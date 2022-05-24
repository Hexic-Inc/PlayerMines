package org.hexic.playermines.commands.player;

import org.bukkit.entity.Player;
import org.hexic.playermines.commands.manager.SubCommand;

public class Upgrade extends SubCommand {
    @Override
    public String getName() {
        return "Upgrade";
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
        return "PlayerMines.Player.Upgrade";
    }
}
