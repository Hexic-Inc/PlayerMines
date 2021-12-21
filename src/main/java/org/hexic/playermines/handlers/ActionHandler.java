package org.hexic.playermines.handlers;

import me.drawethree.ultraprisoncore.UltraPrisonCore;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.hexic.playermines.PlayerMines;
import org.hexic.playermines.data.yml.LangConfig;
import org.hexic.playermines.data.yml.SellPricesConfig;
import org.hexic.playermines.world.Upgrade;
import org.hexic.playermines.world.PlayerMine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ActionHandler {

    enum Action{
        TP,
        RESET;
    }

    private String string;
    private LangConfig config;
    private Inventory prevInv;

    public ActionHandler(String str){
        string = str;
    }

    public String getAction(){
        return  string.split(";")[1];
    }

    public String getType(){
        return string.split(";")[0];
    }

    public void doAction(InventoryClickEvent event){
        this.prevInv = event.getClickedInventory();
        ClickType clickType = event.getClick();
        Player player = (Player) event.getWhoClicked();
        PlayerMine playerMine = new PlayerMine(player);


        if(Arrays.equals(player.getOpenInventory().getTopInventory().getContents(), new GuiHandler("Blocks-Gui", player).fillGuiWithBlocks().get(0).getContents())){
           boolean remove = clickType.isShiftClick();
           int click = calculatePercentClick(clickType);
           ItemStack clickedBlock = event.getCurrentItem();
            Map<ItemStack, Float> contents = new HashMap<>();
            if(remove){
                if(clickType.isCreativeAction()) {
                   contents.put(clickedBlock, 0f);
               } else {
                   contents.put(clickedBlock, playerMine.getMineBlockChance(clickedBlock) - click);
               }
                playerMine.setMineBlocks(contents);
                player.sendMessage("Set " + clickedBlock.getItemMeta().getDisplayName() + "to " + (playerMine.getMineBlockChance(clickedBlock) - click));
            } else {
                contents.put(clickedBlock, playerMine.getMineBlockChance(clickedBlock) + click);
                player.sendMessage("Set " + clickedBlock.getItemMeta().getDisplayName() + "to " + (playerMine.getMineBlockChance(clickedBlock) + click));
                playerMine.setMineBlocks(contents);
            }
            return;

        }




        this.config = new LangConfig(player);
        if(getType().toLowerCase().contains("gui")){
            Inventory inventory;
            if (getAction().contains("blocks")){
                inventory =  new GuiHandler(getAction(),player).fillGuiWithBlocks().get(0);
            } else {
               inventory =  new GuiHandler(getAction(),player).getGui();
            }
            player.openInventory(inventory);

        } else if (getType().toLowerCase().contains("command")){
            if(getAction().toUpperCase().contains(Action.TP.toString())){
                playerMine.teleport(player);
                player.sendMessage(config.getPrefixValue(Action.TP.toString().toLowerCase(), "TP_Message", "&cYou've been teleported!"));
            }

            if(getAction().toUpperCase().contains(Action.RESET.toString())){
                if(PlayerMines.getInitalizer().getCoolDownHandler().isPlayerInCoolDown(player)){
                    player.sendMessage(config.getPrefixValue("reset", "Cool_Down", "&cYou must wait $time seconds to reset your mine.").replace("$time", PlayerMines.getInitalizer().getCoolDownHandler().getTimeLeft(player) + ""));
                    return;
                }
                playerMine.reset();
                PlayerMines.getInitalizer().getCoolDownHandler().addPlayerToMap(player,new PlayerMine(player).getResetTime());
                player.sendMessage(config.getPrefixValue(Action.RESET.toString().toLowerCase(), "Reset_Message","&cMine has been reset!"));
            }

        } else if(getType().toLowerCase().contains("upgrade")){
            int levelToAdd = calculateClick(clickType, player, (Upgrade.valueOf(getAction().toUpperCase())));
            if(!playerMine.hasEnoughForUpgrade(Upgrade.valueOf(getAction().toUpperCase()),levelToAdd)){
                player.sendMessage(new LangConfig(player,Upgrade.valueOf(getAction().toUpperCase())).getPrefixValue("Upgrade-Messages", "Not-Enough", "&cYou don't have enough $upgrade_currency for that upgrade."));
                return;
            }

            if(playerMine.addPurchasedUpgrade(Upgrade.valueOf(getAction().toUpperCase()),levelToAdd)){
                if(new LangConfig(player, Upgrade.valueOf(getAction().toUpperCase())).getValue("Upgrade-Messages", "General-Message", "&c$upgrade Upgrade cost $upgrade_cost $upgrade_currency for $levels_added level(s).").contains("$levels_added")){
                    player.sendMessage(new LangConfig(player,Upgrade.valueOf(getAction().toUpperCase())).getPrefixValue("Upgrade-Messages", "General-Message").replace("$levels_added", levelToAdd + ""));
                } else {
                    player.sendMessage(new LangConfig(player,Upgrade.valueOf(getAction().toUpperCase())).getPrefixValue("Upgrade-Messages", "General-Message"));
                }
                new GuiHandler().reloadGui(player,player.getOpenInventory().getTopInventory());
            } else {
                player.sendMessage(config.getPrefixValue("Upgrade-Messages", "Max-Level", "&cYou've already maxed out that upgrade!"));
            }


        }
    }


    public int calculatePercentClick(ClickType clickType){
        int count = 0;
        if(clickType.isCreativeAction()){
            SellPricesConfig sellPricesConfig = new SellPricesConfig();
            Map<Material,Double> map = sellPricesConfig.getBlocksWithPrices();
            for (Map.Entry<Material, Double> entry : map.entrySet()) {
                if(sellPricesConfig.getBlocksWithChances().get(entry.getKey()) == 0.0) {
                    count++;
                }
            }
        }else if(clickType.isLeftClick()){
            count = 10;
        } else {
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
            bal = (long) PlayerMines.getInitalizer().getEcon().getBalance(player);
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
