package org.hexic.playermines.commands.player;

import org.bukkit.entity.Player;
import org.hexic.playermines.data.yml.LangConfig;
import org.hexic.playermines.managers.commands.SubCommand;
import org.hexic.playermines.world.PlayerMine;

public class Create extends SubCommand {
    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return new LangConfig().getValue(getName(), "description","&cCreate your private mine.");
    }

    @Override
    public void perform(Player player, String[] args) {
        PlayerMine pMine = new PlayerMine(player);
        LangConfig lang = new LangConfig(player);
        if(pMine.hasMine()){
            player.sendMessage(lang.getPrefixValue(getName(), "Mine_Exists", "&cYou already own a mine!"));
            return;
        }
        pMine.createMine();
        player.sendMessage(lang.getPrefixValue(getName(), "Mine_Created", "&cYour mine has been created!"));
    }

    @Override
    public String getPermission() {
        return "PlayerMines.Create";
    }
}
