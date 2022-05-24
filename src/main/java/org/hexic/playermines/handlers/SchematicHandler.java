package org.hexic.playermines.handlers;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.Blocks;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import me.jet315.prisonmines.mine.Mine;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftMagicNumbers;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.hexic.playermines.Main;
import org.hexic.playermines.PlayerMine.PlayerMine;
import org.hexic.playermines.data.yml.LangConfig;
import org.hexic.playermines.data.yml.YmlConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;

import static org.bukkit.Bukkit.getName;

public class SchematicHandler {

    private boolean busy = false;
    private boolean loaded = false;
    private Clipboard clipboard;

    private ArrayList<OfflinePlayer> queue = new ArrayList<>();
    private Map<BlockVector3, Material> blocks = new HashMap<>();

    private Map<BlockVector3, Material> nEast = new HashMap<>();
    private Map<BlockVector3, Material> nWest = new HashMap<>();
    private Map<BlockVector3, Material> sEast = new HashMap<>();
    private Map<BlockVector3, Material> sWest = new HashMap<>();
    private Map<BlockVector3, Material> missedBlocks = new HashMap<>();

    private BlockVector3 nEastBlock;
    private BlockVector3 nWestBlock;
    private BlockVector3 sEastBlock;
    private BlockVector3 sWestBlock;
    private BlockVector3 midBlock;

    /**
     * Initialize the schematic handler. Loads schematics into memory upon reference.
     */
    public SchematicHandler(){
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInitalizer().getPlugin(), this::loadSchematic, 100);
        new BukkitRunnable(){
            @Override
            public void run(){
                if(!queue.isEmpty() && !busy) loadAndPaste(queue.get(0));
            }
        }.runTaskTimer(Main.getInitalizer().getPlugin(), 0 ,20);
    }

    public ArrayList<OfflinePlayer> getQueue() {return queue;}

    /**
     * Paste the players schematic.
     * @param player Player to paste the schematic for.
     * @return Returns true if instant, false if there is a queue.
     */
    public boolean pastePlayersSchematic(OfflinePlayer player){
        queue.add(player);
        return busy;
    }


    /**
     * Load and paste the Player Mine schematic.
     */
    private void loadAndPaste(OfflinePlayer player){
        busy = true;
        if(!loaded) loadSchematic();
        pasteSplitSchematic(player);
        Bukkit.getPlayer(player.getUniqueId()).sendMessage(new LangConfig().getPrefixValue(getName(), "Mine_Created", "&cYour mine has been created!"));
        queue.remove(player);
        busy = false;
    }

    /**
     * Load the Schematic from the saved data.
     */
    public void loadSchematic(){
        Main.getInitalizer().getPlugin().getLogger().warning("Schematics will begin to load. Server will lag and TPS will fall.");
        File file = new File(Main.getInitalizer().getDataManager().getFolder().getAbsolutePath() + File.separator, new YmlConfig().getDefaultSchem() + ".schem");
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
            loadBlocks(clipboard);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInitalizer().getPlugin(), this::splitBlocks, 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
        loaded = true;
    }

    /**
     * Split all the blocks into sections for easier pasting.
     */
    public void splitBlocks(){
        BlockVector3 max = clipboard.getMaximumPoint();
        BlockVector3 min = clipboard.getMinimumPoint();
        int length = clipboard.getRegion().getLength();
        int width = clipboard.getRegion().getWidth();
        int height = max.getBlockY();
        midBlock = BlockVector3.at(min.getBlockX() + (width/2),0, min.getBlockZ() +(length/2));
        nEastBlock = BlockVector3.at(midBlock.getBlockX() - (width/2),height,midBlock.getBlockZ() + (length /2));
        nWestBlock = BlockVector3.at(midBlock.getBlockX() + (width/2), height, midBlock.getBlockZ() + (length/2));
        sEastBlock = BlockVector3.at(midBlock.getBlockX() - (width/2),height ,midBlock.getBlockZ() - (length /2));
        sWestBlock = BlockVector3.at(midBlock.getBlockX() + (length/2),height,midBlock.getBlockZ() - (length/2));
        blocks.forEach((at,block) -> {
            if(blockFallsBetween(midBlock,nEastBlock,at)){
                nEast.put(at, block);
            }
            if(blockFallsBetween(midBlock,nWestBlock,at)){
                nWest.put(at, block);
            }
            if(blockFallsBetween(midBlock,sEastBlock,at)){
                sEast.put(at, block);
            }
            if(blockFallsBetween(midBlock,sWestBlock,at)){
                sWest.put(at,block);
            }
            missedBlocks.put(at,block);
        });
    }

    /**
     * See if a block falls in-between a cuboid region
     * @param bottom First corner of region
     * @param top Second corner of region
     * @param block Block that may fall within the region
     * @return True if block is in region, false if not.
     */
    public boolean blockFallsBetween(BlockVector3 bottom, BlockVector3 top, BlockVector3 block){
        int minX = Math.min(bottom.getBlockX(), top.getBlockX());
        int minY = Math.min(bottom.getBlockY(), top.getBlockY());
        int minZ = Math.min(bottom.getBlockZ(), top.getBlockZ());
        int maxX = Math.max(bottom.getBlockX(), top.getBlockX());
        int maxY = Math.max(bottom.getBlockY(), top.getBlockY());
        int maxZ = Math.max(bottom.getBlockZ(), top.getBlockZ());
        if(block.getBlockX() <= minX || block.getBlockX() >= maxX) return false;
        if(block.getBlockY() <= minY || block.getBlockY() >= maxY) return false;
        if(block.getBlockZ() <= minZ || block.getBlockZ() >= maxZ) return false;
        return true;
    }

    /**
     * Load the schematics blocks from the clipboard
     * @param clipboard Loaded Clipboard
     */
    public void loadBlocks(Clipboard clipboard) {
        BlockVector3 max = clipboard.getMaximumPoint();
        BlockVector3 min = clipboard.getMinimumPoint();
        BaseBlock baseBlock = clipboard.getFullBlock(min);
        Material block = Material.matchMaterial(clipboard.getFullBlock(min).getBlockType().getId());
        BlockVector3 at;
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    at = BlockVector3.at(x,y,z);
                    if(baseBlock != clipboard.getFullBlock(at)) {
                        baseBlock = clipboard.getFullBlock(at);
                        block = Material.matchMaterial(baseBlock.getBlockType().getId()) ;
                    }
                    // Ignore air blocks
                    if (block.isAir()) continue;
                    blocks.put(at, block);

                    //setBlockFast(location.getBlockX() + x, y, location.getBlockZ() + z,Material.matchMaterial(block.getBlockType().getId()),world);
                }
            }
        }
        Main.getInitalizer().getPlugin().getLogger().info("Schematic " + new YmlConfig().getDefaultSchem()+ ".schem has been loaded.");
        Main.getInitalizer().getPlugin().getLogger().info("Schematics have been loaded. TPS should be normal and lag should stop.");
    }

    /**
     * Paste a Schematic that has been split. Schematic must be loaded before use. 1-Second delay between each section
     * @param player Player that will be getting the mine pasted for them.
     */
    private void pasteSplitSchematic(OfflinePlayer player){
        ArrayList<Map<BlockVector3,Material>> splitSchematic = new ArrayList<>();
        splitSchematic.add(nEast);
        splitSchematic.add(nWest);
        splitSchematic.add(sEast);
        splitSchematic.add(sWest);
        splitSchematic.add(missedBlocks);
        World world = PlayerMine.getMineWorld();
        Location mineLoc = new PlayerMine(player).getRawMineLocation().clone();
        splitSchematic.forEach((section) -> Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInitalizer().getPlugin(), () ->{
                    section.forEach((location, block) -> {setBlockFast(location.getBlockX()+ mineLoc.getBlockX(),location.getBlockY(), location.getBlockZ() + mineLoc.getBlockZ(),block, world);});
                }, 20));
    }


    /**
     * Set the block using Minecraft NMS
     * @param x Location X
     * @param y Location Y
     * @param z Location Z
     * @param material Block to be set
     * @param world World to set the block in
     */
    public void setBlockFast(int x, int y, int z, Material material, World world)
    {
        net.minecraft.world.level.World w = ((CraftWorld) world).getHandle();
        net.minecraft.world.level.chunk.Chunk chunk = w.l(net.minecraft.core.BlockPosition.b.b(x,y,z));
        BlockPosition bp = new BlockPosition(x,y,z);
        IBlockData ibd = CraftMagicNumbers.getBlock(material).n();

        chunk.a(bp,ibd,false);
    }


    /**
     * Paste the loaded schematic via worldedit.
     * @param location Location to paste the schematic.
     * @param clipboard Schematic copied to the clipboard.
     */
    private void pasteSchematic(Location location, Clipboard clipboard, boolean bool){
        try (EditSession editSession =   WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(PlayerMine.getMineWorld()),-1)) {
            editSession.setFastMode(true);
            Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(BlockVector3.at(location.getX(), location.getY(), location.getZ())).ignoreAirBlocks(true).build();
            Operations.complete(operation);
            editSession.flushSession();
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clean up all the schematic variables. Useful for when the plugin crashes the server loading a schematic.
     */
    public void cleanUp() {
        busy = false;
        loaded = false;
        clipboard = null;

        queue = null;
         blocks = null;

        nEast = null;
         nWest = null;
         sEast = null;
         sWest = null;
         missedBlocks = null;

        nEastBlock = null;
        nWestBlock = null;
        sEastBlock = null;
        sWestBlock = null;
        midBlock = null;
    }


}
