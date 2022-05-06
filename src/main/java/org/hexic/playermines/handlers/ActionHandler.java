package org.hexic.playermines.handlers;

import me.drawethree.ultraprisoncore.UltraPrisonCore;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.hexic.playermines.Main;
import org.hexic.playermines.PlayerMine.Upgrade;
import org.hexic.playermines.data.yml.LangConfig;
import org.hexic.playermines.data.yml.SellPricesConfig;
import org.hexic.playermines.PlayerMine.PlayerMine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ActionHandler {

    enum Action{
        TP,
        RESET;
    }

    private String string;
    private LangConfig config;
    private MenuHandler menuHandler = Main.getInitalizer().getMenuHandler();

    public ActionHandler(String str){
        string = str;
    }

    public String getAction(){
        return  string.split(";")[1];
    }

    public String getType(){
        return string.split(";")[0];
    }

    private void mineBlockHandler(InventoryClickEvent event){
        double lockedPercent = new SellPricesConfig().getTotalLockedChance();
        double click = calculatePercentClick(event);
        ItemStack clickedItem = event.getCurrentItem();
        Map<ItemStack, Float> contents = new HashMap<>();

        Player player = (Player) event.getWhoClicked();
        PlayerMine playerMine = new PlayerMine(player);
        ClickType clickType = event.getClick();

            /*
                            Mine-Block Gui Handler

                 Handles when a player is inside the "Block-Gui" and changes the mines blocks accordingly.
             */
        //I tried to update this code block, but it seemed to break the block, might try to come back to this to simplify the way this works.
        if (clickType.isShiftClick()) { // Only works for left and right-click, does not apply to middle-click.
            if ((playerMine.getMineBlockChance(clickedItem) - click) < 0) {
                player.sendMessage("Set " + "to " + 0);
                contents.put(clickedItem, 0f);
                playerMine.setMineBlocks(contents);
                menuHandler.setRecentlyClosed(player,true);
                menuHandler.removeInventory(player, event.getView().getTopInventory());
                player.openInventory(new GuiHandler("blocks-gui",player).fillGuiWithBlocks().get(0));
                return;
            }
            player.sendMessage("Set " + "to " + (playerMine.getMineBlockChance(clickedItem) - click));
            contents.put(clickedItem, (float) (playerMine.getMineBlockChance(clickedItem) - click));
            playerMine.setMineBlocks(contents);
            menuHandler.setRecentlyClosed(player,true);
            menuHandler.removeInventory(player, event.getView().getTopInventory());
            player.openInventory(new GuiHandler("blocks-gui",player).fillGuiWithBlocks().get(0));
            return;
        }

        if (click == 0) {//Applies if the player middle-clicks a block.
            click = (100.0 - lockedPercent);
            playerMine.getMineBlocks().forEach(mineBlock -> {
                if (!new SellPricesConfig().getLockedBlocks().contains(mineBlock.getItem().getType())) {
                    contents.put(mineBlock.getItem(), 0f);
                }
            });
            playerMine.setMineBlocks(contents);
            contents.clear();
            contents.put(clickedItem, (float) click);
            playerMine.setMineBlocks(contents);
            if(new LangConfig(player).getPrefixValue("Block-Messages", "Replace-All", "&cReplaced all blocks with $block$").contains("$block$")){
                assert clickedItem != null;
                player.sendMessage(new LangConfig(player).getPrefixValue("Block-Messages", "Replace-All", "&cReplaced all blocks with $block$").replace("$block$",clickedItem.getType().toString()));
            } else {
                player.sendMessage(new LangConfig(player).getPrefixValue("Block-Messages", "Replace-All", "&cReplaced all blocks with $block$"));
            }
            menuHandler.setRecentlyClosed(player,true);
            menuHandler.removeInventory(player, event.getView().getTopInventory());
            player.openInventory(new GuiHandler("blocks-gui",player).fillGuiWithBlocks().get(0));
            return;
        }

        if (click < (playerMine.getMineBlocksFreeChance())) {//Applies if the click is a left or right-click, without pressing shift.
            contents.put(clickedItem, (float) (playerMine.getMineBlockChance(clickedItem) + click));
            playerMine.setMineBlocks(contents);
            String message = new LangConfig(player).getPrefixValue("Block-Messages", "Replace-Some", "&cReplaced $replaced$% of blocks with $block$");
            if(message.contains("$block$")){
                assert clickedItem != null;
                message = message.replace("$block$",clickedItem.getType().toString());
            }
            if(message.contains("$replaced$")){
                message = message.replace("$replaced$", click + "");
            }
            player.sendMessage(message);
            menuHandler.setRecentlyClosed(player,true);
            menuHandler.removeInventory(player, event.getView().getTopInventory());
            player.openInventory(new GuiHandler("blocks-gui",player).fillGuiWithBlocks().get(0));
            return;
        } else {
            player.sendMessage(new LangConfig(player).getPrefixValue("Block-Messages","Too-Much","&cThat's too many blocks to have in your mine!"));
        }
        //              End of Block-Gui function.

    }


    /**
     * This method handles when a player clicks on an item that belongs to anything related to this plugin.
     * This method does various things from upgrading features in a mine, to changing the blocks in a mine.
     * @param event InventoryClickEvent from the InventoryClickEvent listener.
     */
    public void doAction(InventoryClickEvent event){


        Player player = (Player) event.getWhoClicked();
        PlayerMine playerMine = new PlayerMine(player);
        ClickType clickType = event.getClick();


        if(event.getView().getTitle().equals(new GuiHandler().getDisplayName("Blocks-Gui")) &&  new SellPricesConfig().getBlocksWithChances().containsKey(Objects.requireNonNull(event.getCurrentItem()).getType())) {
            mineBlockHandler(event);
        }

        /*
                        Various Actions
         */
        this.config = new LangConfig();
        if(getType().toLowerCase().contains("gui")){
            Inventory inventory;
            if (getAction().contains("blocks")){
                inventory =  new GuiHandler(getAction(),player).fillGuiWithBlocks().get(0);
            } else {
               inventory =  new GuiHandler(getAction(),player).getGui();
            }
            menuHandler.setRecentlyClosed(player,true);
            player.openInventory(inventory);

        } else if (getType().toLowerCase().contains("command")){
            if(getAction().toUpperCase().contains(Action.TP.toString())){
                playerMine.teleport(player);
                player.sendMessage(config.getPrefixValue(Action.TP.toString().toLowerCase(), "TP_Message", "&cYou've been teleported!"));
            }

            if(getAction().toUpperCase().contains(Action.RESET.toString())){
                if(Main.getInitalizer().getCoolDownHandler().isPlayerInCoolDown(player)){
                    player.sendMessage(config.getPrefixValue("reset", "Cool_Down", "&cYou must wait $time$ seconds to reset your mine.").replace("$time$", Main.getInitalizer().getCoolDownHandler().getTimeLeft(player) + ""));
                    return;
                }
                playerMine.reset();
                Main.getInitalizer().getCoolDownHandler().addPlayerToMap(player,new PlayerMine(player).getResetTime());
                player.sendMessage(config.getPrefixValue(Action.RESET.toString().toLowerCase(), "Reset_Message","&cMine has been reset!"));
            }

        } else if(getType().toLowerCase().contains("upgrade")){
            int levelToAdd = calculateClick(clickType, player, (Upgrade.valueOf(getAction().toUpperCase())));
            if(!playerMine.hasEnoughForUpgrade(Upgrade.valueOf(getAction().toUpperCase()),levelToAdd)){
                player.sendMessage(new LangConfig(player,Upgrade.valueOf(getAction().toUpperCase())).getPrefixValue("Upgrade-Messages", "Not-Enough", "&cYou don't have enough $upgrade_currency for that upgrade."));
                return;
            }

            if(playerMine.addPurchasedUpgrade(Upgrade.valueOf(getAction().toUpperCase()),levelToAdd)){
                if(new LangConfig(player, Upgrade.valueOf(getAction().toUpperCase())).getValue("Upgrade-Messages", "General-Message", "&c$upgrade$ Upgrade cost $upgrade_cost$ $upgrade_currency$ for $levels_added$ level(s).").contains("$levels_added$")){
                    player.sendMessage(new LangConfig(player,Upgrade.valueOf(getAction().toUpperCase())).getPrefixValue("Upgrade-Messages", "General-Message").replace("$levels_added$", levelToAdd + ""));
                } else {
                    player.sendMessage(new LangConfig(player,Upgrade.valueOf(getAction().toUpperCase())).getPrefixValue("Upgrade-Messages", "General-Message"));
                }
                menuHandler.setRecentlyClosed(player,true);
                new GuiHandler().reloadGui(player,player.getOpenInventory().getTopInventory());
            } else {
                player.sendMessage(config.getPrefixValue("Upgrade-Messages", "Max-Level", "&cYou've already maxed out that upgrade!"));
            }


        }
    }


    public int calculatePercentClick(InventoryClickEvent event){
        int count = 0;
        ClickType clickType = event.getClick();
        Player player = (Player) event.getWhoClicked();
        if(clickType.isCreativeAction()){
            PlayerMine playerMine = new PlayerMine(player);
            count = (int) playerMine.getMineBlockChance(event.getCurrentItem());
        }else if(clickType.isLeftClick()){
            count = 10;
        } else if (clickType.isRightClick()){
            count = 1;
        }
       return count;
    }

    public int calculateClick(ClickType clickType, Player player, Upgrade upgrade){
        int total = 0;
        PlayerMine pMine = new PlayerMine(player);
        long bal = 0;
        if(pMine.getUpgradeType(upgrade).toLowerCase().contains("gems")){
            bal = UltraPrisonCore.getInstance().getGems().getApi().getPlayerGems(player);
        } else if (pMine.getUpgradeType(upgrade).toLowerCase().contains("tokens")){
            bal = UltraPrisonCore.getInstance().getTokens().getApi().getPlayerTokens(player);
        } else if (pMine.getUpgradeType(upgrade).toLowerCase().contains("money")) {
            bal = (long) Main.getInitalizer().getEcon().getBalance(player);
        }
        //Max Amount
        if(clickType.isShiftClick() && clickType.isLeftClick() || clickType.isShiftClick() && clickType.isRightClick()){
            for(int i = pMine.getUpgradeLevel(upgrade); i < pMine.getMaxUpgradeLevel(upgrade); i ++){
                if(bal > pMine.getUpgradeCost(upgrade, i)){
                    bal -= pMine.getUpgradeCost(upgrade, i);
                    total ++;
                }
            }
            // 10
        } else if (clickType.isRightClick()){
            for(int i = 0; i < 10; i ++) {
                if(pMine.getMaxUpgradeLevel(upgrade) > pMine.getUpgradeLevel(upgrade) + i) {
                    if (bal > pMine.getUpgradeCost(upgrade, i)) {
                        bal -= pMine.getUpgradeCost(upgrade, i);
                        total ++;
                    }
                }
            }
            // 1
        }else if (clickType.isLeftClick()) {
            if(pMine.getMaxUpgradeLevel(upgrade) > pMine.getUpgradeLevel(upgrade) + 1) {
                if (bal > pMine.getUpgradeCost(upgrade, 1)) {
                    total ++;
                }
            }
        }

        return total;
    }
}
