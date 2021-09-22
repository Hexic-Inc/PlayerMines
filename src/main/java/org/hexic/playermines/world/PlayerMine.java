package org.hexic.playermines.world;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.hexic.playermines.PlayerMines;
import org.hexic.playermines.data.json.PminesJson;
import org.hexic.playermines.data.yml.YmlConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

public class PlayerMine {

    private Player player;

    /**
     * Create the instance of the players mine.
     * @param player Player to make the mine for.
     */
    public PlayerMine(Player player){
        this.player = player;
    }

    /**
     *
     * @return The location of the players mine.
     */
    public Location mineLocation(){
        String pMineCoord = new PminesJson().getMineCoord(player);
        String[] split = pMineCoord.split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[0]);
        return new Location(Bukkit.getWorld(new YmlConfig().getWorldName()),xAsCoord(x),100,yAsCoord(y)-299);
    }

    /**
     * Create the mine for that player instance.
     */
    public void createMine(){
        new PminesJson().createPMine(player);
        Location defLocation = new Location(Bukkit.getWorld(new YmlConfig().getWorldName()),0,100,0);
        String pMineCoord = new PminesJson().getMineCoord(player);
        String[] split = pMineCoord.split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[0]);
        Location areaLocation = new Location(Bukkit.getWorld(new YmlConfig().getWorldName()),xAsCoord(x),100,yAsCoord(y) - 299);
        Clipboard clipboard;
        File file = new File(PlayerMines.getDataManager().getFolder(), "/" +"default" + ".schem");
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
            pasteSchematic(areaLocation,clipboard);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Get the necessary location for the border.
     * @return The center point inside the players mine as a Location.
     */
    public Location borderLocation(){
        String pMineCoord = new PminesJson().getMineCoord(player);
        String[] split = pMineCoord.split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[0]);
        return new Location(Bukkit.getWorld(new YmlConfig().getWorldName()),xAsCoord(x) + 150,100,yAsCoord(y)-149);
    }

    private void pasteSchematic(Location location, Clipboard clipboard){
        World world = Bukkit.getWorld(new YmlConfig().getWorldName());
        com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(world);
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(adaptedWorld)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    private int xAsCoord(int x){
        String areaSize = new YmlConfig().getAreaSize();
        String[] split = areaSize.split(",");
        return (Integer.parseInt(split[0]) * 3) * x;
    }


    private int yAsCoord(int y){
        String areaSize = new YmlConfig().getAreaSize();
        String[] split = areaSize.split(",");
        return (Integer.parseInt(split[0]) * 3) * y + 299;
    }

}
