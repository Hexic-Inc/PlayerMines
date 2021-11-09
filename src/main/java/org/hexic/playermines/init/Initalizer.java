package org.hexic.playermines.init;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.hexic.playermines.data.yml.GuiConfig;
import org.hexic.playermines.data.yml.MineCrateConfig;
import org.hexic.playermines.data.yml.YmlConfig;
import org.hexic.playermines.listeners.GuiClick;
import org.hexic.playermines.listeners.MineCrate;
import org.hexic.playermines.listeners.MineReset;
import org.hexic.playermines.managers.commands.CommandManager;
import org.hexic.playermines.managers.data.DataManager;
import org.hexic.playermines.world.PlayerMine;


import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import static org.bukkit.Bukkit.getServer;

public class Initalizer {

    private final JavaPlugin plugin;
    private final DataManager dataManager;
    private static Economy econ = null;
    private static Permission perms = null;


    public Initalizer(JavaPlugin plugin){
        this.plugin = plugin;
        this.dataManager = new DataManager(this.plugin);
    }

    public DataManager getDataManager(){return dataManager;}

    public Economy getEcon() {return econ;}

    public Permission getPerms() {return perms;}

    public void initAll(){
        initEcon();
        initPerms();
        initData();
        initListeners();
        initCommands();
        initWorld();
    }

    private void initWorld(){
        if(PlayerMine.getMineWorld() == null){
            PlayerMine.createWorld();
        }
    }

    public JavaPlugin getPlugin(){
        return plugin;
    }

    private void initListeners(){
        getServer().getPluginManager().registerEvents(new MineReset(),plugin);
        getServer().getPluginManager().registerEvents(new GuiClick(), plugin);
        getServer().getPluginManager().registerEvents(new MineCrate(), plugin);

    }

    private void initCommands(){
        plugin.getCommand("playermine").setExecutor(new CommandManager());
    }

    private void initData(){
        //if(!getDataFolder().exists()){
        new YmlConfig().createConfigs();
        new GuiConfig().createConfig();
        new MineCrateConfig().createConfig();
        // }
    }

    private boolean initPerms(){
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    private void initEcon(){
        if (!setupEconomy() ) {
            Bukkit.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", plugin.getDescription().getName()));
            getServer().getPluginManager().disablePlugin(plugin);
            return;
        }
    }


    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

}
