package org.hexic.playermines.commands.admin;

import org.bukkit.entity.Player;
import org.hexic.playermines.PlayerMine.PlayerMine;
import org.hexic.playermines.managers.commands.SubCommand;
import scala.concurrent.impl.FutureConvertersImpl;

public class delete extends SubCommand {
    @Override
    public String getName() {
        return "Delete";
    }

    @Override
    public String getDescription() {
        return "Delete the selected Players Mine and associated data.";
    }

    @Override
    public void perform(Player player, String[] args) {
        if(args.length < 3){
            //Not enough args
            player.sendMessage("Not enough args.");
        } else {
            if(new PlayerMine(args[2]).hasMine()){
                new PlayerMine(args[2]).delete();
                player.sendMessage("Deleted.");
            } else {
                //No mine
                player.sendMessage("No Mine");
            }
        }

    }

    @Override
    public String getPermission() {
        return "PlayerMines.Admin.Delete";
    }
}
