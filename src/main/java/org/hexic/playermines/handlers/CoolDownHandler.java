package org.hexic.playermines.handlers;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoolDownHandler {

    private Map<UUID, Integer> playerCoolDownMap = new HashMap<>();
    public CoolDownHandler(JavaPlugin plugin){
        new BukkitRunnable(){
            @Override
            public void run(){
               for(UUID uuid : playerCoolDownMap.keySet()){
                   if(playerCoolDownMap.get(uuid) == 1){
                       playerCoolDownMap.remove(uuid);
                       continue;
                   }
                   playerCoolDownMap.put(uuid,playerCoolDownMap.get(uuid)-1);
               }

            }
        }.runTaskTimer(plugin, 0 ,20);
    }

    public void addPlayerToMap(Player player, Integer time){
        playerCoolDownMap.put(player.getUniqueId(), time);
    }

    public boolean isPlayerInCoolDown(Player player){
        return playerCoolDownMap.containsKey(player.getUniqueId());
    }

    public Integer getTimeLeft(Player player){
        return playerCoolDownMap.get(player.getUniqueId());
    }
}
