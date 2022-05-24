package org.hexic.playermines;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import org.hexic.playermines.init.Initializer;
import org.hexic.playermines.PlayerMine.PlayerMine;

import java.util.Objects;


public class Main extends JavaPlugin {

    private static Initializer initializer;

    @Override
    public void onEnable() {
        initializer = new Initializer(this);
        initializer.initAll();
    }


    @Override
    public void onDisable() {
        if(PlayerMine.getMineWorld() != null){
            Objects.requireNonNull(Bukkit.getWorld(PlayerMine.getMineWorld().getName())).save();
        }
        initializer.getSchematicHandler().cleanUp();

    }

    public static Initializer getInitalizer(){return initializer;}

}
