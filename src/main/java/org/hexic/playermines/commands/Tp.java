package org.hexic.playermines.commands;

import com.github.yannicklamprecht.worldborder.api.BorderAPI;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hexic.playermines.data.json.PminesJson;
import org.hexic.playermines.managers.commands.SubCommand;
import org.hexic.playermines.world.PlayerMine;

public class Tp extends SubCommand {
    @Override
    public String getName() {
        return "tp";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public void perform(Player player, String[] args) {
        if(args.length != 2){
            player.sendMessage("Invalid player.");
            return;
        }
        Player player1;
        if(Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[1]))){
            player1 = Bukkit.getPlayer(args[1]);
        } else {
            player1 = Bukkit.getOfflinePlayer(args[1]).getPlayer();
        }
        if(new PminesJson().mineExists(player1)){
            player.teleport(new PlayerMine(player1).mineLocation());
            WorldBorderApi worldBorderApi = BorderAPI.getApi();
            worldBorderApi.setBorder(player,300, new PlayerMine(player1).borderLocation());
            player.sendMessage("You've been teleported");
        } else {
            player.sendMessage("Player does not have a plot");
        }
    }
}
