package org.hexic.playermines;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.hexic.playermines.data.yml.YmlConfig;
import org.hexic.playermines.managers.commands.CommandManager;
import org.hexic.playermines.managers.data.DataManager;

public class PlayerMines extends JavaPlugin {

    private static DataManager dataManager;

    @Override
    public void onEnable() {
        dataManager = new DataManager(this);
        if(!getDataFolder().exists()){
            new YmlConfig().createConfigs();
        }
        getCommand("PlayerMine").setExecutor(new CommandManager());


    }

    @Override
    public void onDisable() {

    }

    public static DataManager getDataManager(){return dataManager;}

}
