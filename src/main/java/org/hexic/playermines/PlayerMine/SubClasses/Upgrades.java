package org.hexic.playermines.PlayerMine.SubClasses;

import dev.drawethree.ultraprisoncore.UltraPrisonCore;
import dev.drawethree.ultraprisoncore.api.enums.LostCause;
import dev.drawethree.ultraprisoncore.api.enums.ReceiveCause;
import dev.drawethree.ultraprisoncore.gems.api.UltraPrisonGemsAPI;
import dev.drawethree.ultraprisoncore.tokens.api.UltraPrisonTokensAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.hexic.playermines.Main;
import org.hexic.playermines.PlayerMine.PlayerMine;
import org.hexic.playermines.PlayerMine.SubClasses.*;
import org.hexic.playermines.data.json.PlayerJson;
import org.hexic.playermines.data.yml.LangConfig;
import org.hexic.playermines.data.yml.YmlConfig;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Upgrades {

    private UUID uuid;
    private final YmlConfig config = new YmlConfig();
    private OfflinePlayer ownerPlayer;
    private PlayerJson ownerJson;
    private final Economy econ = Main.getInitalizer().getEcon();
    private String mineName;

    /**
     * Upgrade interface for PlayerMine
     * @param owner Owner of the PlayerMine/Upgrades
     */
    public Upgrades (OfflinePlayer owner){
        uuid = owner.getUniqueId();
        ownerPlayer = owner;
        ownerJson = new PlayerJson(uuid.toString());
        String[] split = uuid.toString().split("-");
        mineName = "mine" + "-" + split[split.length-1];
    }


    /**
     * Check if the player has enough of the supported currency needed.
     * @param upgrade Upgrade to get the currency for.
     * @param level Level that would be added to the upgrade.
     * @return if the player has enough.
     */
    public boolean hasEnoughForUpgrade(Upgrade upgrade, int level){
        String string = config.getSectionValue("Enchant_Costs", upgradeAsString(upgrade));
        long bal = 0;
        if(string.contains("G")){
            UltraPrisonGemsAPI ultraPrisonGemsAPI = UltraPrisonCore.getInstance().getGems().getApi();
            string = string.replace("G", "");
            bal =  ultraPrisonGemsAPI.getPlayerGems(ownerPlayer);
        } else if(string.contains("T")){
            UltraPrisonTokensAPI ultraPrisonTokensAPI = UltraPrisonCore.getInstance().getTokens().getApi();
            string = string.replace("T", "");
            bal =  ultraPrisonTokensAPI.getPlayerTokens(ownerPlayer);
        } else if(string.contains("$")){
            bal = (long) econ.getBalance(ownerPlayer);
            string = string.replace("$", "");
        }
        int newLevel = Integer.parseInt(ownerJson.getData().get(upgradeAsString(upgrade))) + level;
        return bal > (long) runMath(string, newLevel);
    }

    /**
     * Get the amount of time left to reset the players mine.
     * @return Time left.
     */
    public int getResetTime(){
        return (int) runMath(config.getSectionValue("Enchant_Triggered", "Regen_Time"), getUpgradeLevel(Upgrade.REGEN_TIME));
    }


    /**
     * Get the Upgrade level for that mine.
     * @param upgrade Upgrade to get the level for.
     * @return Level of the upgrade.
     */
    public int getUpgradeLevel(Upgrade upgrade){
        return Integer.parseInt(ownerJson.getValue(upgradeAsString(upgrade)));
    }

    /**
     * Get the Max Level for the Upgrade.
     * @param upgrade Upgrade to get the max level for.
     * @return Max level for the upgrade.
     */
    public int getMaxUpgradeLevel(Upgrade upgrade){
        return Integer.parseInt(config.getSectionValue("Enchant_Caps",upgradeAsString(upgrade)));
    }


    /**
     * Get the upgrade as a YML supported string.
     * @param upgrade Upgrade to convert to string.
     * @return YML supported string.
     */
    public String upgradeAsString(Upgrade upgrade){
        String string = "";
        switch (upgrade){
            case SIZE:
                string = "Size";
                break;
            case BERSERK:
                string = "Berserk";
                break;
            case GEM_DROPS:
                string = "Gem_Drops";
                break;
            case TAX_PRICE:
                string = "Tax_Price";
                break;
            case REGEN_TIME:
                string = "Regen_Time";
                break;
            case RENT_PRICE:
                string = "Rent_Price";
                break;
            case ETOKEN_FINDER:
                string = "EToken_Finder";
                break;
            case UPGRADE_FINDER:
                string ="Upgrade_Finder";
                break;
            case MINE_MULTIPLIER:
                string = "Mine_Multiplier";
                break;
            case MINECRATE_FINDER:
                string = "MineCrate_Finder";
                break;
        }
        return string;
    }


    /**
     * The kind of currency that the upgrade requires.
     * @param upgrade Upgrade to get the currency for.
     * @return "Gems", "Tokens", "Dollars", OR "" if it doesn't contain a supported currency.
     */
    public String balType(Upgrade upgrade){
        String string = config.getSectionValue("Enchant_Costs", upgradeAsString(upgrade));
        if(string.contains("G")){
            return "Gems";
        } else if(string.contains("T")){
            return "Tokens";
        } else if(string.contains("$")){
            return "Dollars";
        }
        return "";
    }

    /**
     * Get the price needed to add the level to the upgrade.
     * @param upgrade Upgrade to get the supported currency.
     * @param level Level that would be added to the upgrade.
     * @return Price it would cost to add the level to the selected upgrade.
     */
    public long getUpgradeCost(Upgrade upgrade, int level){
        String string = config.getSectionValue("Enchant_Costs", upgradeAsString(upgrade));
        int newLevel = Integer.parseInt(ownerJson.getData().get(upgradeAsString(upgrade))) + level;
        long price = 0;
        if(string.contains("G")){
            price = (long) runMath(string.replace("G", ""), newLevel);
        } else if(string.contains("T")){
            price = (long) runMath(string.replace("T", ""), newLevel);
        } else if(string.contains("$")){
            price = (long) runMath(string.replace("$", ""), newLevel);
        }
        return price;
    }

    /**
     * Get the Upgrades type of currency
     * @param upgrade Upgrade to get the required type of currency.
     * @return Needed type of currency to level up that Upgrade.
     */
    public String getUpgradeType(Upgrade upgrade){
        String string = config.getSectionValue("Enchant_Costs", upgradeAsString(upgrade));
        if(string.contains("G")){
            return "Gems";
        } else if(string.contains("T")){
            return "Tokens";
        } else if(string.contains("$")){
            return "Money";
        }
        return "";



    }

    /**
     * Remove the supported currency from the players account.
     * @param upgrade Upgrade that is being selected.
     * @param level Amount to add to the upgrade level.
     */
    private void runUpgradeCost(Upgrade upgrade, int level){
        String string = config.getSectionValue("Enchant_Costs", upgradeAsString(upgrade));
        int newLevel = Integer.parseInt(ownerJson.getData().get(upgradeAsString(upgrade))) + level;
        long price;
        if(string.contains("G")){
            UltraPrisonGemsAPI ultraPrisonGemsAPI = UltraPrisonCore.getInstance().getGems().getApi();
            price = (long) runMath(string.replace("G", ""), newLevel);
            ultraPrisonGemsAPI.removeGems(ownerPlayer, price);
        } else if(string.contains("T")){
            UltraPrisonTokensAPI ultraPrisonTokensAPI = UltraPrisonCore.getInstance().getTokens().getApi();
            price = (long) runMath(string.replace("T", ""), newLevel);
            ultraPrisonTokensAPI.removeTokens(ownerPlayer, price, LostCause.WITHDRAW);
        } else if(string.contains("$")){
            price = (long) runMath(string.replace("$", ""), newLevel);
            econ.withdrawPlayer(ownerPlayer, price);
        }

    }

    /**
     * Add the level for a specific Upgrade on the mine. Takes the supported currencies out of their account.
     * @param upgrade Type of upgrade.
     * @param level Increase the upgrade by that amount.
     * @return If the upgrade got added.
     */
    public boolean addPurchasedUpgrade(Upgrade upgrade, int level){
        Map<String, String> data = ownerJson.getData();
        String string = upgradeAsString(upgrade);
        if(Integer.parseInt(ownerJson.getData().get(string)) + level <= Integer.parseInt(config.getSectionValue("Enchant_Caps", string))){
            data.put(string, (Integer.parseInt(ownerJson.getData().get(string)) + level) + "");
            ownerJson.setValue(data);
            runUpgradeCost(upgrade, level);
            if(upgrade == Upgrade.SIZE){
                // setMineSize(level);
            }
            if(upgrade == Upgrade.MINE_MULTIPLIER){
                addMultiplier(runMath(config.getSectionValue("Enchant_Triggered", "Mine_Multiplier"), Double.parseDouble(ownerJson.getValue("Mine_Multiplier"))));
            }
            return true;
        }
        return false;
    }

    /**
     * Add the level for a specific Upgrade on the mine.
     * @param upgrade Type of upgrade.
     * @param level Increase the upgrade by that amount.
     * @return If the upgrade got added.
     */
    private boolean addUpgrade(Upgrade upgrade, int level){
        Map<String, String> data = ownerJson.getData();
        String string = upgradeAsString(upgrade);
        if(Integer.parseInt(ownerJson.getData().get(string)) + level < Integer.parseInt(config.getSectionValue("Enchant_Caps", string))){
            data.put(string, (Integer.parseInt(ownerJson.getData().get(string)) + level) + "");
            ownerJson.setValue(data);
            return true;
        }
        return false;
    }


    /**
     * Run the upgrades for the mine, for that specific player.
     * @param player Player to run the upgrade for.
     */
    public void runUpgrades(Player player){
        config.getSection("Enchant_Chances").forEach((upgrade, sLvl) -> {
            if (Math.random() <= runMath(sLvl, Double.parseDouble(ownerJson.getValue(upgrade)))) {
                if (Integer.parseInt(ownerJson.getValue(upgrade)) > 0) {
                    String string = new LangConfig(player, Upgrade.valueOf(upgrade.toUpperCase())).getPrefixValue("Triggered-Messages", "Upgrade-Triggered", "&c$upgrade got triggered.");
                    String string2 = string.replace("$upgrade", Upgrade.valueOf(upgrade.toUpperCase()).toString());
                    int level;
                    if (Integer.parseInt(ownerJson.getValue("Berserk_Count")) > 0) {
                        level = (int) runMath(config.getSectionValue("Enchant_Triggered", "Berserk"), Integer.parseInt(ownerJson.getValue("Berserk")));
                    } else {
                        level = Integer.parseInt(ownerJson.getValue(sLvl));
                    }
                    if (Integer.parseInt(ownerJson.getValue("Berserk_Count")) - 1 >= 0) {
                        Map<String, String> data = ownerJson.getData();
                        data.put("Berserk_Count", (Integer.parseInt(ownerJson.getValue("Berserk_Count")) - 1) + "");
                        ownerJson.setValue(data);
                    }
                    switch (Upgrade.valueOf(upgrade.toUpperCase())) {
                        case BERSERK:
                            runBerserk();
                        case GEM_DROPS:
                            runGemDrops(player, level);
                        case MINECRATE_FINDER:
                            new MineCrate(mineName).createMineCrate();
                        case UPGRADE_FINDER:
                            Upgrade[] upgrades = Upgrade.values();
                            addUpgrade(upgrades[new Random().nextInt(upgrades.length)], 1);
                        case ETOKEN_FINDER:
                            runETokenFinder(player, level);
                        case RENT_PRICE:
                            if (!player.getUniqueId().equals(uuid)) {
                                runRentPrice(player, level);
                            }
                        case TAX_PRICE:
                            if (!player.getUniqueId().equals(uuid)) {
                                runTaxPrice(player, level);
                            }
                    }
                    if (Upgrade.valueOf(upgrade.toUpperCase()) == Upgrade.TAX_PRICE) {
                        if (!player.getUniqueId().equals(uuid)) {
                            runRentPrice(player, level);
                            player.sendMessage(string2.replace(Upgrade.valueOf(upgrade.toUpperCase()).toString(), prettyString(Upgrade.valueOf(upgrade.toUpperCase()))));
                        }
                    } else if (Upgrade.valueOf(upgrade.toUpperCase()) == Upgrade.RENT_PRICE) {
                        if (!player.getUniqueId().equals(uuid)) {
                            runTaxPrice(player, level);
                            player.sendMessage(string2.replace(Upgrade.valueOf(upgrade.toUpperCase()).toString(), prettyString(Upgrade.valueOf(upgrade.toUpperCase()))));
                        }
                    } else {
                        player.sendMessage(string2.replace(Upgrade.valueOf(upgrade.toUpperCase()).toString(), prettyString(Upgrade.valueOf(upgrade.toUpperCase()))));
                    }
                }
            }
        });
    }

    /**
     * Format an Upgrade to a nice string.
     * @param upgrade EX. "MINECRATE_FINDER"
     * @return EX. "Minecrate Finder"
     */
    private String prettyString(Upgrade upgrade){
        String temp = upgradeAsString(upgrade);
        if(temp.contains("_")) {
            String[] split = upgrade.toString().toLowerCase().split("_");
            String temp1 = split[0].replaceFirst(String.valueOf(split[0].charAt(0)), String.valueOf(split[0].charAt(0)).toUpperCase());
            String temp2 = split[1].replaceFirst(String.valueOf(split[1].charAt(0)), String.valueOf(split[1].charAt(0)).toUpperCase());
            temp = temp1 + " " + temp2;
        } else {
            String s = String.valueOf(upgrade.toString().charAt(0));
            temp = upgrade.toString().replaceFirst(s, s.toUpperCase());
        }
        return temp;
    }

    private void addMultiplier (double multiplier){
       UltraPrisonCore.getInstance().getMultipliers().getApi().getSellMultiplier(Bukkit.getPlayer(ownerPlayer.getUniqueId())).addMultiplier(multiplier);
    }

    /**
     * Run the tax calculation for the players mine.
     * @param player Player to take the balance from.
     * @param level Upgrade Level
     */
    private void runTaxPrice(Player player, int level){
        econ.depositPlayer(ownerPlayer, runMath(config.getSectionValue("Enchant_Triggered", "Tax_Price"), level));
        econ.withdrawPlayer(player, runMath(config.getSectionValue("Enchant_Triggered", "Tax_Price"), level));
    }


    /**
     * Run the rent calculation for the players mine.
     * @param player Player to take the balance from.
     * @param level Upgrade level
     */
    private void runRentPrice(Player player, int level){
        econ.depositPlayer(ownerPlayer, runMath(config.getSectionValue("Enchant_Triggered", "Rent_Price"), level));
        econ.withdrawPlayer(player, runMath(config.getSectionValue("Enchant_Triggered", "Rent_Price"), level));
    }

    /**
     * Run the EToken Finder Upgrade.
     * @param player Player to give the rewards to.
     * @param level Upgrade level.
     */
    private void runETokenFinder(Player player, int level){
        UltraPrisonTokensAPI ultraPrisonTokensAPI = UltraPrisonCore.getInstance().getTokens().getApi();
        ultraPrisonTokensAPI.addTokens(player, (long) runMath(config.getSectionValue("Enchant_Triggered", "EToken_Finder"), level), ReceiveCause.GIVE);

    }

    /**
     * Run the Gem Drop Upgrade.
     * @param player Player to give the rewards to.
     * @param level Upgrade level.
     */
    private void runGemDrops(Player player, int level){
        UltraPrisonGemsAPI ultraPrisonGemsAPI = UltraPrisonCore.getInstance().getGems().getApi();
        ultraPrisonGemsAPI.addGems(player, (long) runMath(config.getSectionValue("Enchant_Triggered", "Gem_Drops"),level), ReceiveCause.GIVE);

    }

    /**
     * Run the Berserk Upgrade.
     */
    private void runBerserk(){
        Map<String, String> data = ownerJson.getData();
        data.put("Berserk_Count", new YmlConfig().getSectionValue("Enchant_Triggered","Berserk_Count"));
        ownerJson.setValue(data);
    }

    /**
     * Increase the size of the mine in every direction.
     * @param addLevel Size to increase the mine by.
     */
    private void setMineSize(int addLevel){
        ArrayList<String> cords = new JsonLocation(ownerPlayer).convertString(ownerJson.getValue("Mine-Size"));
        String[] first = cords.get(0).split(",");
        String[] second = cords.get(1).split(",");
        String newCoord = ("{" + ((Integer.parseInt(first[0]) - addLevel) +",") + (first[1] + ",") + (Integer.parseInt(first[2]) - addLevel)+"}" + ", " + "{" + ((Integer.parseInt(second[0]) + addLevel) +",") + (second[1] + ",") + (Integer.parseInt(second[2]) + addLevel)+ "}");
        ownerJson.setValue("Size", newCoord);
        new PrisonMine(ownerPlayer, new Regions(mineName)).createPrisonMine(new PlayerMine(ownerPlayer).getTeleportLocation());
    }




    private double runMath(String problem, double UpgradesLevel){
        String temp = problem.replace(" ", "");
        if(temp.contains("level")){
            temp = temp.replace("level", UpgradesLevel + "");
        }
        if(temp.contains("*")){
            String[] split = temp.split("\\*");
            return Double.parseDouble(split[0]) * Double.parseDouble(split[1]);
        }
        if(problem.contains("-")){
            String[] split = temp.split("-");
            return Double.parseDouble(split[0]) - Double.parseDouble(split[1]);
        }
        if(temp.contains("+")){
            String[] split = temp.split("\\+");
            return Double.parseDouble(split[0]) + Double.parseDouble(split[1]);
        }
        if(temp.contains("/")){
            String[] split = temp.split("/");
            return Double.parseDouble(split[0]) / Double.parseDouble(split[1]);
        }

        return Double.parseDouble(problem);
    }

}
