package org.hexic.playermines.commands.admin;

import org.bukkit.entity.Player;
import org.hexic.playermines.commands.manager.SubCommand;
import org.hexic.playermines.PlayerMine.PlayerMine;

public class SpawnMC extends SubCommand {
    @Override
    public String getName() {
        return "SpawnMC";
    }

    @Override
    public String getDescription() {
        return "Spawns a MineCraft at that players mine, on the top level.";
    }

    @Override
    public void perform(Player player, String[] args) {
        if(args.length == 3){
            PlayerMine pMine = new PlayerMine(args[2]);
            pMine.createMineCrateTopLayer();
            player.sendMessage("Spawning the crate at" + args[2] + "'s private mine.");
        } else {
            PlayerMine pMine = new PlayerMine(player);
            pMine.createMineCrateTopLayer();
            player.sendMessage("Spawning crate at your Private Mine");
        }
    }


    @Override
    public String getPermission() {
        return "PlayerMines.Admin.SpawnMC";
    }
}
