package org.hexic.playermines.managers.commands;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.hexic.playermines.commands.player.Create;
import org.hexic.playermines.commands.player.Reset;
import org.hexic.playermines.commands.player.Tp;
import org.hexic.playermines.data.yml.LangConfig;
import org.hexic.playermines.handlers.GuiHandler;
import org.hexic.playermines.world.PlayerMine;

import java.util.ArrayList;

public class CommandManager implements CommandExecutor {

    private ArrayList<SubCommand> subcommands = new ArrayList<>();

    public CommandManager() {
        subcommands.add(new Create());
        subcommands.add(new Tp());
        subcommands.add(new Reset());
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (args.length > 0) {
                if(args[0].equalsIgnoreCase("help")){
                    p.sendMessage("--------------------------------");
                    for (int i = 0; i < subcommands.size(); i++) {
                        p.sendMessage(subcommands.get(i).getName() + " - " + subcommands.get(i).getDescription());
                    }
                    p.sendMessage("--------------------------------");
                }
                for (int i = 0; i < subcommands.size(); i++) {
                    if (args[0].equalsIgnoreCase(subcommands.get(i).getName())) {
                        subcommands.get(i).perform(p, args);
                    }
                }
            } else {
                if(new PlayerMine(p).hasMine()){
                    p.openInventory(new GuiHandler("Menu",p).getGui());
                } else {
                    p.sendMessage(new LangConfig(p).getPrefixValue("General-Messages","No_Mine", "&cYou don't own a mine!"));
                }
            }

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


}

