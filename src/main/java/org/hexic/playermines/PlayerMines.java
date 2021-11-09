package org.hexic.playermines;


import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import org.hexic.playermines.data.yml.YmlConfig;
import org.hexic.playermines.init.Initalizer;
import org.hexic.playermines.world.PlayerMine;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.getServer;


public class PlayerMines extends JavaPlugin {

    private static Initalizer initalizer;

    @Override
    public void onEnable() {
        initalizer = new Initalizer(this);
        initalizer.initAll();
    }


    @Override
    public void onDisable() {
        if(PlayerMine.getMineWorld() != null){
            Bukkit.getWorld(PlayerMine.getMineWorld().getName()).save();
        }
    }

    public static Initalizer getInitalizer(){return initalizer;}

}
