package org.hexic.playermines.init;

import org.hexic.playermines.commands.admin.Config;
import org.hexic.playermines.commands.admin.Delete;
import org.hexic.playermines.commands.admin.SpawnMC;
import org.hexic.playermines.commands.admin.TP;
import org.hexic.playermines.commands.manager.SubCommand;
import org.hexic.playermines.commands.player.*;

import java.util.ArrayList;

public class Commands {

    private ArrayList<SubCommand> playerCommands = new ArrayList<>();
    private ArrayList<SubCommand> adminCommands = new ArrayList<>();

    public void initAll(){
        initAdminCommands();
        initPlayerCommands();
    }

    /**
     * Get all admin commands.
     * @return All admin commands that are currently setup
     */
    public ArrayList<SubCommand> getAdminCommands(){
        initAdminCommands();
        return adminCommands;}

    /**
     * Get all player commands
     * @return All player commands that are currently setup
     */
    public ArrayList<SubCommand> getPlayerCommands(){ initPlayerCommands(); return playerCommands;}

    public void initAdminCommands(){
        initConfig();
        initAdminTP();
        initDelete();
        initSpawnMC();
    }

    /**
     * Init all Player Commands
     */
    public void initPlayerCommands(){
        initAdd();
        initContents();
        initCreate();
        initPrivate();
        initRemove();
        initReset();
        initTP();
        initUpgrade();
    }
    //Player Commands

    /**
     * Init Add Command
     */
    private void initAdd(){
        playerCommands.add(new Add());
    }
    /**
     * Init Contents Command
     */
    private void initContents(){
        playerCommands.add(new Contents());
    }
    /**
     * Init Create Command
     */
    private void initCreate(){
        playerCommands.add(new Create());
    }
    /**
     * Init Private Command
     */
    private void initPrivate(){
        playerCommands.add(new Private());
    }
    /**
     * Init Remove Command
     */
    private void initRemove(){
        playerCommands.add(new Remove());
    }
    /**
     * Init Reset Command
     */
    private void initReset(){
        playerCommands.add(new Reset());
    }
    /**
     * Init TP Command
     */
    private void initTP(){
        playerCommands.add(new Tp());
    }
    /**
     * Init Upgrade Command
     */
    private void initUpgrade(){
        playerCommands.add(new Upgrade());
    }

    //Admin Commands

    /**
     * Init Admin Delete Command
     */
    private void initDelete(){adminCommands.add(new Delete());}
    /**
     * Init Admin SpawnMC Command
     */
    private void initSpawnMC(){adminCommands.add(new SpawnMC());}
    /**
     * Init Admin Config Command
     */
    private void initConfig(){adminCommands.add(new Config());}
    /**
     * Init Admin TP Command
     */
    private void initAdminTP(){adminCommands.add(new TP());}
}
