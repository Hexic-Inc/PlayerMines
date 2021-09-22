package org.hexic.playermines.commands;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.hexic.playermines.data.json.PminesJson;
import org.hexic.playermines.data.yml.YmlConfig;
import org.hexic.playermines.managers.commands.SubCommand;
import org.hexic.playermines.world.EmptyChunkGenerator;
import org.hexic.playermines.world.PlayerMine;

public class Create extends SubCommand {
    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Create your first private mine!";
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public void perform(Player player, String[] args) {
        //Check to see if the player has an existing mine in the config. If not
        if(Bukkit.getWorld(new YmlConfig().getWorldName()) == null) {//Check to see if the world exists.
               WorldCreator creator = new WorldCreator(new YmlConfig().getWorldName());//Create the pmine world.
               creator.generator(new EmptyChunkGenerator());
               creator.createWorld();
        }
        PlayerMine pMine = new PlayerMine(player);
        pMine.createMine();
    }
}
