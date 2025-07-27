
package com.rtpqueue.listeners;

import com.rtpqueue.RTPQueue1v1;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;

public class PlayerListener implements Listener {
    
    private final RTPQueue1v1 plugin;
    
    public PlayerListener(RTPQueue1v1 plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Remove from queue
        plugin.getQueueManager().removeFromQueue(player);
        
        // Handle combat logging
        plugin.getMatchManager().handlePlayerDisconnect(player);
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // Cancel movement if player is frozen
        if (plugin.getMatchManager().isPlayerFrozen(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        // Cancel interactions if player is frozen
        if (plugin.getMatchManager().isPlayerFrozen(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        // Remove from match on death
        if (plugin.getMatchManager().isPlayerInMatch(player)) {
            plugin.getMatchManager().removeFromMatch(player);
        }
    }
}
