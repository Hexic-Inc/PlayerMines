package org.hexic.playermines.PlayerMine.SubClasses;

import dev.drawethree.ultraprisoncore.UltraPrisonCore;
import me.jet315.prisonmines.JetsPrisonMinesAPI;
import me.jet315.prisonmines.mine.Mine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.hexic.playermines.Main;
import org.hexic.playermines.PlayerMine.PlayerMine;
import org.hexic.playermines.handlers.MineCrateHandler;

import java.util.Collection;
import java.util.Objects;
import java.util.Random;

public class MineCrate {

    private String mineName;
    private JetsPrisonMinesAPI jetsPrisonMinesAPI = new JetsPrisonMinesAPI();

    /**
     * MineCrate interface for each PlayerMine
     * @param mineName
     */
    public MineCrate(String mineName){
        this.mineName = mineName;
    }

    /**
     * Create a MineCrate Randomly in the mine. This gets ran 5 seconds after the mine gets created to make sure the crate gets created.
     *
     */
    public void createMineCrate(){
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInitalizer().getPlugin(), () ->{
            Mine mine = jetsPrisonMinesAPI.getMineManager().getMineByName(mineName);
            Location min = mine.getMineRegion().getMinPoint();
            Location max = mine.getMineRegion().getMaxPoint();
            int minX = min.getBlockX();
            int minY = min.getBlockY();
            int minZ = min.getBlockZ();
            int maxX = max.getBlockX();
            int maxY = max.getBlockY();
            int maxZ = max.getBlockZ();
            Random random = new Random();
            int randomX = random.nextInt(maxX - minX);
            int randomY = random.nextInt(maxY - minY);
            int randomZ = random.nextInt(maxZ - minZ);
            Location randomLoc = new Location(PlayerMine.getMineWorld(), minX +randomX, minY + randomY, minZ + randomZ);
            Location holoLoc = new Location(PlayerMine.getMineWorld(), minX + randomX, minY + randomY, minZ + randomZ);
            randomLoc.getBlock().setType(Material.AIR);
            randomLoc.getBlock().setType(new MineCrateHandler().getMaterial());
            ArmorStand hologram = (ArmorStand) PlayerMine.getMineWorld().spawnEntity(holoLoc, EntityType.ARMOR_STAND);
            hologram.setVisible(false);
            hologram.setCustomNameVisible(true);
            hologram.setCustomName(ChatColor.RED + "MineCrate");
            hologram.setGravity(false);
        }, 100);
    }

    /**
     * Similar to creating a MineCrate randomly in the mine, this will spawn the crate on the top layer at a random X and Y cord.
     *
     */
    public void createMineCrateTopLayer(){
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInitalizer().getPlugin(), () ->{
            Mine mine = jetsPrisonMinesAPI.getMineManager().getMineByName(mineName);
            Location min = mine.getMineRegion().getMinPoint();
            Location max = mine.getMineRegion().getMaxPoint();
            int minX = min.getBlockX();
            int minZ = min.getBlockZ();
            int maxX = max.getBlockX();
            int maxY = max.getBlockY();
            int maxZ = max.getBlockZ();
            Random random = new Random();
            int randomX = random.nextInt(maxX - minX);
            int randomZ = random.nextInt(maxZ - minZ);
            Location randomLoc = new Location(PlayerMine.getMineWorld(), minX +randomX, maxY, minZ + randomZ);
            Location holoLoc = new Location(PlayerMine.getMineWorld(), minX + randomX, maxY, minZ + randomZ);
            randomLoc.getBlock().setType(Material.AIR);
            randomLoc.getBlock().setType(new MineCrateHandler().getMaterial());
            ArmorStand hologram = (ArmorStand) PlayerMine.getMineWorld().spawnEntity(holoLoc, EntityType.ARMOR_STAND);
            hologram.setVisible(false);
            hologram.setCustomNameVisible(true);
            hologram.setCustomName(ChatColor.RED + "MineCrate");
            hologram.setGravity(false);
        }, 100);
    }


    /**
     * Remove holograms at the specified location.
     * @param location Approximate location of where the hologram is.
     */
    public void removeHologram(Location location, int boxRadius){
        Collection<Entity> nearbyEntities = Objects.requireNonNull(location.getWorld()).getNearbyEntities(location, boxRadius, boxRadius, boxRadius);
        nearbyEntities.forEach(entity -> {
            if(entity.getType().equals(EntityType.ARMOR_STAND)){
                entity.remove();
            }
        });
    }
}
