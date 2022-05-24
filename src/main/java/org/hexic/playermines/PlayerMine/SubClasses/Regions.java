package org.hexic.playermines.PlayerMine.SubClasses;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import dev.drawethree.ultraprisoncore.UltraPrisonCore;
import me.jet315.prisonmines.JetsPrisonMinesAPI;
import org.bukkit.Location;
import org.bukkit.World;
import org.hexic.playermines.PlayerMine.PlayerMine;
import org.hexic.playermines.data.yml.YmlConfig;

import java.util.Map;
import java.util.UUID;

public class Regions {

    private String mineName;
    private final JetsPrisonMinesAPI jetsPrisonMinesAPI = new JetsPrisonMinesAPI();

    public Regions(String mineName) {
        this.mineName = mineName;
    }

    /**
     * Region interface for that player mine.
     */
    public void createMineRegion(){
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(PlayerMine.getMineWorld()));
        if(regions.hasRegion(mineName)) {
            regions.removeRegion(mineName);
            try {
                regions.save();
            } catch (StorageException e) {
                e.printStackTrace();
            }
        }
        Location first = jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getMineRegion().getMinPoint();
        Location second = jetsPrisonMinesAPI.getMineManager().getMineByName(mineName).getMineRegion().getMaxPoint();
        BlockVector3 min = BlockVector3.at(first.getX(), first.getY(), first.getZ());
        BlockVector3 max = BlockVector3.at(second.getX(), second.getY(), second.getZ());
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(mineName, min, max);
        region.setPriority(1);
        regions.addRegion(region);
        Map<String, Boolean> flags = new YmlConfig().getMineRegionFlags();
        flags.forEach((s, state) -> {
            String str = s;
            if(str.contains(" ")){
                str = s.replace(" ", "");
            }
            StateFlag.State temp;
            if(state){
                temp = StateFlag.State.ALLOW;
            } else {
                temp = StateFlag.State.DENY;
            }
            Flag<?> flag = WorldGuard.getInstance().getFlagRegistry().get(str);
            region.setFlag((StateFlag) flag, temp);
        });
    }

    /**
     * Get the Region for the mine.
     * @return Mine Region.
     */
    public ProtectedRegion getMineRegion(){
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(PlayerMine.getMineWorld()));
        assert regions != null;
        if(!regions.hasRegion(mineName)) {
            createMineRegion();
        }
        return regions.getRegion(mineName);
    }

    /**
     * Create the Global Region for the Player Mine world.
     */
    public static void createGlobalRegion(){
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(PlayerMine.getMineWorld()));
        assert regions != null;
        if(regions.hasRegion("__global__")) {
            regions.removeRegion("__global__");
            try {
                regions.save();
            } catch (StorageException e) {
                e.printStackTrace();
            }
        }
        GlobalProtectedRegion region = new GlobalProtectedRegion("__global__");
        Map<String, Boolean> flags = new YmlConfig().getGlobalRegionFlags();
        region.setPriority(0);
        flags.forEach((s, state) -> {
            String str = s;
            if(str.contains(" ")){
                str = s.replace(" ", "");
            }
            StateFlag.State temp;
            if(state){
                temp = StateFlag.State.ALLOW;
            } else {
                temp = StateFlag.State.DENY;
            }
            Flag<?> flag = WorldGuard.getInstance().getFlagRegistry().get(str);
            region.setFlag((StateFlag) flag, temp);

        });
        regions.addRegion(region);
    }
}
