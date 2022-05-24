package org.hexic.playermines.commands.player;

import org.bukkit.entity.Player;
import org.hexic.playermines.Main;
import org.hexic.playermines.data.yml.LangConfig;
import org.hexic.playermines.commands.manager.SubCommand;
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
        if(Main.getInitalizer().getSchematicHandler().getQueue().contains(player)){
            player.sendMessage(lang.getPrefixValue("General-Messages","Busy", "&cWe're busy making your mine!"));
            return;
        }
        if (args.length < 2) {
            if(new PlayerMine(player).hasMine()){
                new PlayerMine(player).teleport(player);
                player.sendMessage(lang.getPrefixValue(getName(), "TP_Message", "&cYou've been teleported!"));
                return;
            } else {
                player.sendMessage(lang.getPrefixValue("General-Messages","No_Mine", "&cYou don't own a mine!"));
            }
            return;
        }
        PlayerMine playerMine = new PlayerMine(args[1]);
        if (playerMine.hasMine()) {
            if(playerMine.isPublic() || playerMine.isPlayerAdded(player) ) {
                if(playerMine.playersAtMine().size() <= playerMine.getMemberCap()){
                    playerMine.teleport(player);
                    player.sendMessage(lang.getPrefixValue(getName(), "TP_Message", "&cYou've been teleported!"));
                } else {
                    player.sendMessage(lang.getPrefixValue(getName(),"Max_Members", "&cThis mine has too many people mining at it!"));
                }
            } else {
                    player.sendMessage(lang.getPrefixValue(getName(), "Not_Added", "&cYou're not a member of this players mine!"));
            }
        } else {
            player.sendMessage(lang.getPrefixValue(getName(), "Invalid_Mine", "&cPlayer does not have a mine!"));
        }
    }

    @Override
    public String getPermission() {
        return "PlayerMines.Player.Teleport";
    }
}
