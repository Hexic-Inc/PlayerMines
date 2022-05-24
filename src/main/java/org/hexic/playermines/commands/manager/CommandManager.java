package org.hexic.playermines.commands.manager;


import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.hexic.playermines.Main;
import org.hexic.playermines.commands.admin.SpawnMC;
import org.hexic.playermines.commands.admin.Delete;
import org.hexic.playermines.commands.admin.TP;
import org.hexic.playermines.commands.player.Create;
import org.hexic.playermines.commands.player.Reset;
import org.hexic.playermines.commands.player.Tp;
import org.hexic.playermines.data.yml.LangConfig;
import org.hexic.playermines.handlers.GuiHandler;
import org.hexic.playermines.PlayerMine.PlayerMine;
import org.hexic.playermines.init.Commands;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements TabExecutor {

    private final ArrayList<SubCommand> subcommands ;
    private final ArrayList<SubCommand> adminCommands ;

    public CommandManager() {
        Commands commands = new Commands();
        commands.initAll();
        //Player Commands
        subcommands = new Commands().getPlayerCommands();
        //Admin commands
        adminCommands = new Commands().getAdminCommands();
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if(args.length > 1 && args[0].toLowerCase().contains("admin")){
                for(SubCommand subCommand : adminCommands){
                    if(args[1].equalsIgnoreCase(subCommand.getName())){
                        if(Main.getInitalizer().getPerms().playerHas(p, subCommand.getPermission()) || p.isOp() || Main.getInitalizer().getPerms().playerHas(p,"PlayerMines.Admin.*")){
                            subCommand.perform(p,args);
                        } else {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to perform that command."));
                        }
                    }
                }
            }
            if (args.length > 0) {
                //If the player issues the "/pmine help" command
                if(args[0].equalsIgnoreCase("help")){
                    p.sendMessage("--------------------------------");
                    for (SubCommand subcommand : subcommands) {
                        //Only get the commands that the player can run with their equipped permission nodes
                        if (Main.getInitalizer().getPerms().playerHas(p, subcommand.getPermission()) || p.isOp() || Main.getInitalizer().getPerms().playerHas(p,"PlayerMines.Player.*")) {
                            p.sendMessage( "-" + subcommand.getName() + " - " + subcommand.getDescription());
                        }
                    }
                    p.sendMessage("--------------------------------");
                    for(SubCommand subCommand : adminCommands){
                        if(Main.getInitalizer().getPerms().playerHas(p, subCommand.getPermission()) || p.isOp() ||  Main.getInitalizer().getPerms().playerHas(p,"PlayerMines.Admin.*")){
                            p.sendMessage( "-" + subCommand.getName() + " - " + subCommand.getDescription());
                        }
                    }
                }
                //Permission check to make sure the player can run that command
                for (SubCommand subcommand : subcommands) {
                    if (args[0].equalsIgnoreCase(subcommand.getName())) {
                        if (Main.getInitalizer().getPerms().playerHas(p, subcommand.getPermission()) || p.isOp() || Main.getInitalizer().getPerms().playerHas(p,"PlayerMines.Player.*")) {
                            subcommand.perform(p, args);
                        } else {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to perform that command."));
                        }
                    }
                }
            } else {
                // If the player just runs the command "/pmine" with no args
                if(new PlayerMine(p).hasMine()){
                    //Open the pmine GUI
                    p.openInventory(new GuiHandler("Menu",p).getGui());
                } else {
                    //Tell the player that they don't own a mine.
                    p.sendMessage(new LangConfig(p).getPrefixValue("General-Messages","No_Mine", "&cYou don't own a mine!"));
                }
            }
            //Console sender
        } else if (sender instanceof ConsoleCommandSender) {
            Player p = (Player) sender;

            if (args.length > 0) {
                for (int i = 0; i < subcommands.size(); i++) {
                    if (args[0].equalsIgnoreCase(subcommands.get(i).getName())) {
                        subcommands.get(i).perform(p, args);
                    }
                }
            } else {
                p.sendMessage("--------------------------------");
                for (int i = 0; i < subcommands.size(); i++) {
                    p.sendMessage(subcommands.get(i).getName() + " - " + subcommands.get(i).getDescription());
                }
                p.sendMessage("--------------------------------");
            }
        }


        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if(args.length == 1){
            List<String> commands = new ArrayList<>();
            subcommands.forEach(cmd -> {
                Player player = (Player) sender;
                if(Main.getInitalizer().getPerms().playerHas(player, cmd.getPermission()) || player.isOp()){
                    commands.add(cmd.getName());
                }
            });
            // We loop through all the admin commands because we don't know if that player only has one Admin command they're allowed to do
            adminCommands.forEach(cmd -> {
                Player player = (Player) sender;
                if((Main.getInitalizer().getPerms().playerHas(player, cmd.getPermission()) || player.isOp()) && !commands.contains("Admin")){
                    commands.add("Admin");
                }
            });
            return commands;
        }
        if(args.length == 2){
            List<String> commands = new ArrayList<>();
            adminCommands.forEach(cmd -> {
                Player player = (Player) sender;
                if(player.isOp() || Main.getInitalizer().getPerms().playerHas(player, cmd.getPermission())){
                    commands.add(cmd.getName());
                }
            });
            return commands;
        }
        return null;
    }
}

