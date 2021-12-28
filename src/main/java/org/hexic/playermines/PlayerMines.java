package org.hexic.playermines;


import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import org.hexic.playermines.init.Initializer;
import org.hexic.playermines.world.PlayerMine;


public class PlayerMines extends JavaPlugin {

    private static Initializer initializer;

    @Override
    public void onEnable() {
        initializer = new Initializer(this);
        initializer.initAll();
    }


    @Override
    public void onDisable() {
        if(PlayerMine.getMineWorld() != null){
            Bukkit.getWorld(PlayerMine.getMineWorld().getName()).save();
        }

    }

    public static Initializer getInitalizer(){return initializer;}

}
