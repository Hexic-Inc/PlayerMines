package org.hexic.playermines.init;

import me.drawethree.ultraprisoncore.UltraPrisonCore;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.hexic.playermines.data.yml.GuiConfig;
import org.hexic.playermines.data.yml.MineCrateConfig;
import org.hexic.playermines.data.yml.SellPricesConfig;
import org.hexic.playermines.data.yml.YmlConfig;
import org.hexic.playermines.handlers.CoolDownHandler;
import org.hexic.playermines.handlers.MenuHandler;
import org.hexic.playermines.listeners.*;
import org.hexic.playermines.managers.commands.CommandManager;
import org.hexic.playermines.managers.data.DataManager;
import org.hexic.playermines.PlayerMine.PlayerMine;


import java.awt.*;
import java.util.Objects;

import static org.bukkit.Bukkit.getServer;

public class Initializer {

    private final JavaPlugin plugin;
    private final DataManager dataManager;
    private static Economy econ = null;
    private static Permission perms = null;
    private CoolDownHandler coolDownHandler;
    private MenuHandler menuHandler;


    public Initializer(JavaPlugin plugin){
        this.plugin = plugin;
        this.dataManager = new DataManager(this.plugin);
        //In order for Autosell regions to work properly, they must be reloaded when the plugin starts
        UltraPrisonCore.getInstance().getAutoSell().reload();
    }

    public DataManager getDataManager(){return dataManager;}

    public Economy getEcon() {return econ;}

    public Permission getPerms() {return perms;}

    public CoolDownHandler getCoolDownHandler(){return coolDownHandler;}

    public MenuHandler getMenuHandler() {return menuHandler;}

    public void initAll(){
        initEcon();
        initPerms();
        initData();
        initMenuHandler();
        initListeners();
        initCommands();
        loadCoolDownHandler();
        initWorld();
    }

    public void loadCoolDownHandler(){
        coolDownHandler = new CoolDownHandler(plugin);
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
        getServer().getPluginManager().registerEvents(new WorldJoin(),plugin);
        //getServer().getPluginManager().registerEvents(new GuiClose(), plugin);
        //getServer().getPluginManager().registerEvents(new GuiOpen(), plugin);
        //getServer().getPluginManager().registerEvents(new PlayerTP(), plugin);
        /*for(Class<?> clazz : new Reflections(plugin.getClass().getPackage().getName() + ".listeners").getSubTypesOf(Listener.class) ){
            try{
                Listener listener = (Listener) clazz.getDeclaredConstructor().newInstance();
                getServer().getPluginManager().registerEvents(listener, plugin);

            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e){
                e.printStackTrace();
            }
        }
        /*for (Class<? extends Listener> aClass : sub) {
            Bukkit.getConsoleSender().sendMessage(aClass.getTypeName());
        }*/
    }

    private void initCommands(){
        plugin.getCommand("playermine").setExecutor(new CommandManager());
    }

    private void initData(){
        if(!plugin.getDataFolder().exists()){
            new YmlConfig().createConfigs();
            new GuiConfig().createConfig();
            new MineCrateConfig().createConfig();
            new SellPricesConfig().createDefaultBlocks();
        }
    }

    private void initMenuHandler(){
        menuHandler = new MenuHandler();
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
