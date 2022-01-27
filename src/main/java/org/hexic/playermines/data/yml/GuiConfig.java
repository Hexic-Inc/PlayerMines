package org.hexic.playermines.data.yml;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.hexic.playermines.PlayerMines;
import org.hexic.playermines.managers.data.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GuiConfig {


    private static Config config;

    public GuiConfig(){
        config = PlayerMines.getInitalizer().getDataManager().getConfig("Gui.yml");
        config.getKeys();
    }

    public ConfigurationSection getSection(String section){
        return config.getConfigurationSection(section);
    }

    public Set<String> getKeys(){
        return config.getKeys();
    }

    public String getValue(String path){
        return config.getString(path);
    }

    public int getInt(String path){
        return config.getInt(path);
    }

    public void createConfig(){
        createMenuGui();
        createUpgradeGui();
        createTP();
        createBlank();
        createReset();
        createChangeBlocks();
        createUpgrades();
        createSettings();
        createUpgradeItems();
        config.saveConfig();
    }

    public void createUpgradeItems(){
        createBerserk();
        createGemDrops();
        createMinecrate();
        createMoneyMultiplier();
        createTokenFinder();
        createRentPrice();
        createTaxPrice();
        createUpgradeFinder();
        createSize();
        createRegen();
        createBlocksGui();
        createMineBlockPercent();
    }

    private void createBlocksGui(){
        String section = "Blocks-Gui";
        config.createSection(section);
        //Full lava slot block
        config.setListValue(section, "Display_Name", "&8Mine Contents");
        config.setListValue(section, "Size", 54);
        config.setListValue(section, "Contents", "[{1,5;$mine-blocks}, {1,x;$blank}, {x,9;$blank}, {6,x;$blank}, {x,1;$blank}");
    }

    private void createUpgradeGui(){
        String section = "Upgrade-Gui";
        config.createSection(section);
        //Full lava slot block
        config.setListValue(section, "Display_Name", "&4Upgrades");
        config.setListValue(section, "Size", 36);
        config.setListValue(section, "Contents", "[{2,3;$gem_drops}, {2,4;$minecrate_finder}, {2,5;$etoken_finder}, {2,6;$mine_multiplier}, {2,7;$regen_time}, " +
                "{3,3;$size}, {3,4;$upgrade_finder}, {3,5;$tax_price}, {3,6;$rent_price}, {3,7;$berserk}," +
                "{x,x;$blank}]");
    }


    private void createMenuGui(){
        String section = "Menu-Gui";
        config.createSection(section);
        //Full lava slot block
        config.setListValue(section, "Display_Name", "&4Menu");
        config.setListValue(section, "Size", 9);
        config.setListValue(section, "Contents", "[{1,1-2;$blank}, {1,3;$tp}, {1,4;$reset}, {1,5;$upgrade}, {1,6;$change_blocks}, {1,7;$settings}, {1,8-9;$blank}]");
    }

    private void createMineBlockPercent(){
        String section = "mine-blocks";
        config.createSection(section);
        config.setListValue(section, "Item", "crafting_table");
        config.setListValue(section, "Display_Name", "&b&cMine-Contents");
        config.setListValue(section, "Lore", "$mine-contents");
        config.setListValue(section, "Action", "");
    }

    private void createTP(){
        String section = "tp";
        config.createSection(section);
        config.setListValue(section, "Item", "compass");
        config.setListValue(section, "Display_Name", "&b&l&cTp to mine");
        config.setListValue(section, "Lore", "");
        config.setListValue(section, "Action", "command;tp");
    }

    private void createBlank(){
        String section = "blank";
        config.createSection(section);
        config.setListValue(section, "Item", "gray_stained_glass_pane");
        config.setListValue(section, "Display_Name", " ");
        config.setListValue(section, "Lore", " ");
        config.setListValue(section, "Action", "");
    }

    private void createReset(){
        String section = "reset";
        config.createSection(section);
        config.setListValue(section, "Item", "spruce_button");
        config.setListValue(section, "Display_Name", "&6Reset the Mine");
        config.setListValue(section, "Lore", "");
        config.setListValue(section, "Action", "command;reset");
    }

    private void createUpgrades(){
        String section = "upgrade";
        config.createSection(section);
        config.setListValue(section, "Item", "diamond_pickaxe");
        config.setListValue(section, "Display_Name", "&cMine Upgrades");
        config.setListValue(section, "Lore", "");
        config.setListValue(section, "Action", "gui;Upgrade");
    }

    private void createChangeBlocks(){
        String section = "change_blocks";
        config.createSection(section);
        config.setListValue(section, "Item", "stone");
        config.setListValue(section, "Display_Name", "&8Change Blocks in Mine");
        config.setListValue(section, "Lore", "");
        config.setListValue(section, "Action", "gui;blocks");
    }

    private void createSettings(){
        String section = "settings";
        config.createSection(section);
        config.setListValue(section, "Item", "redstone_torch");
        config.setListValue(section, "Display_Name", "&cSettings");
        config.setListValue(section, "Lore", "");
        config.setListValue(section, "Action", "");
    }





    private void createSize(){
        String section = "size";
        config.createSection(section);
        config.setListValue(section, "Item", "end_stone");
        config.setListValue(section, "Display_Name", "&cIncrease Size");
        config.setListValue(section, "Lore", "&6Increases the size of the physical mine.;&c$upgrade_level / $upgrade_max");
        config.setListValue(section, "Action", "upgrade;size");
    }
    private void createRegen(){
        String section = "regen_time";
        config.createSection(section);
        config.setListValue(section, "Item", "quartz");
        config.setListValue(section, "Display_Name", "&cReset Time");
        config.setListValue(section, "Lore", "&6Lets you reset the mine more frequently.;&c$upgrade_level / $upgrade_max");
        config.setListValue(section, "Action", "upgrade;regen_time");
    }

    private void createMoneyMultiplier(){
        String section = "mine_multiplier";
        config.createSection(section);
        config.setListValue(section, "Item", "diamond");
        config.setListValue(section, "Display_Name", "&cMoney Multiplier");
        config.setListValue(section, "Lore", "&6Money Multiplier buff for the mine.;&c$upgrade_level / $upgrade_max");
        config.setListValue(section, "Action", "upgrade;mine_multiplier");
    }

    private void createTokenFinder(){
        String section = "etoken_finder";
        config.createSection(section);
        config.setListValue(section, "Item", "sunflower");
        config.setListValue(section, "Display_Name", "&cToken Finder");
        config.setListValue(section, "Lore", "&6Ability to find tokens while you mine.;&c$upgrade_level / $upgrade_max");
        config.setListValue(section, "Action", "upgrade;etoken_finder");
    }

    private void createTaxPrice(){
        String section = "tax_price";
        config.createSection(section);
        config.setListValue(section, "Item", "paper");
        config.setListValue(section, "Display_Name", "&cPlayer Tax");
        config.setListValue(section, "Lore", "&6Passive income from people mining at your public mine.;&c$upgrade_level / $upgrade_max");
        config.setListValue(section, "Action", "upgrade;tax_price");
    }

    private void createRentPrice(){
        String section = "rent_price";
        config.createSection(section);
        config.setListValue(section, "Item", "book");
        config.setListValue(section, "Display_Name", "&cPlayer Rent");
        config.setListValue(section, "Lore", "&6Passive income from people mining at your private mine.;&c$upgrade_level / $upgrade_max");
        config.setListValue(section, "Action", "upgrade;rent_price");
    }

    private void createMinecrate(){
        String section = "minecrate_finder";
        config.createSection(section);
        config.setListValue(section, "Item", "chest");
        config.setListValue(section, "Display_Name", "&cMinecrate Finder");
        config.setListValue(section, "Lore", "&6Increases the chances of a Minecrate to spawn.;&c$upgrade_level / $upgrade_max");
        config.setListValue(section, "Action", "upgrade;minecrate_finder");
    }

    private void createUpgradeFinder(){
        String section = "upgrade_finder";
        config.createSection(section);
        config.setListValue(section, "Item", "nether_star");
        config.setListValue(section, "Display_Name", "&cUpgrade Finder");
        config.setListValue(section, "Lore", "&6Find mine upgrades while you mine.;&c$upgrade_level / $upgrade_max");
        config.setListValue(section, "Action", "upgrade;upgrade_finder");
    }

    private void createBerserk(){
        String section = "berserk";
        config.createSection(section);
        config.setListValue(section, "Item", "diamond_pickaxe");
        config.setListValue(section, "Display_Name", "&cBerserk");
        config.setListValue(section, "Lore", "&6Buff your mine enchants temporarily to your pickaxe level.;&c$upgrade_level / $upgrade_max");
        config.setListValue(section, "Action", "upgrade;berserk");
    }

    private void createGemDrops(){
        String section = "gem_drops";
        config.createSection(section);
        config.setListValue(section, "Item", "emerald");
        config.setListValue(section, "Display_Name", "&cGem Dropper");
        config.setListValue(section, "Lore", "&6Find gems while you mine.;&c$upgrade_level / $upgrade_max");
        config.setListValue(section, "Action", "upgrade;gem_drops");
    }

}

