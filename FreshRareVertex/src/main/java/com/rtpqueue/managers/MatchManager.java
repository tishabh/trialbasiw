
package com.rtpqueue.managers;

import com.rtpqueue.RTPQueue1v1;
import com.rtpqueue.enums.WorldType;
import com.rtpqueue.utils.TeleportUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MatchManager {
    
    private final RTPQueue1v1 plugin;
    private final Set<UUID> playersInMatch;
    private final Set<UUID> frozenPlayers;
    
    public MatchManager(RTPQueue1v1 plugin) {
        this.plugin = plugin;
        this.playersInMatch = ConcurrentHashMap.newKeySet();
        this.frozenPlayers = ConcurrentHashMap.newKeySet();
    }
    
    public void startMatch(Player player1, Player player2, WorldType worldType) {
        // Add players to active match
        playersInMatch.add(player1.getUniqueId());
        playersInMatch.add(player2.getUniqueId());
        
        // Send match found message with opponent names
        String matchMessage1 = plugin.getMessageUtils().colorize(
                plugin.getMessageUtils().getMessage("match-found")
                        .replace("{opponent}", player2.getName()));
        String matchMessage2 = plugin.getMessageUtils().colorize(
                plugin.getMessageUtils().getMessage("match-found")
                        .replace("{opponent}", player1.getName()));
        
        player1.sendMessage(matchMessage1);
        player2.sendMessage(matchMessage2);
        
        // Start countdown
        startCountdown(player1, player2, worldType);
    }
    
    private void startCountdown(Player player1, Player player2, WorldType worldType) {
        BukkitTask countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            int countdown = 5;
            
            @Override
            public void run() {
                if (countdown > 0) {
                    String countdownMessage = plugin.getMessageUtils().colorize(
                            plugin.getMessageUtils().getMessage("countdown")
                                    .replace("{seconds}", String.valueOf(countdown)));
                    player1.sendMessage(countdownMessage);
                    player2.sendMessage(countdownMessage);
                    countdown--;
                } else {
                    // Teleport players
                    teleportPlayers(player1, player2, worldType);
                    
                    // Cancel this task
                    Bukkit.getScheduler().cancelTask(this.hashCode());
                }
            }
        }, 0L, 20L); // Run every second
    }
    
    private void teleportPlayers(Player player1, Player player2, WorldType worldType) {
        Location[] locations = TeleportUtils.findSafeLocations(worldType, plugin.getConfigManager().getSpawnDistance());
        
        if (locations != null && locations.length == 2) {
            // Teleport players
            player1.teleport(locations[0]);
            player2.teleport(locations[1]);
            
            // Apply effects
            applyMatchEffects(player1);
            applyMatchEffects(player2);
            
            // Freeze players
            freezePlayer(player1);
            freezePlayer(player2);
            
            // Schedule unfreeze
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                unfreezePlayer(player1);
                unfreezePlayer(player2);
            }, plugin.getConfigManager().getFreezeDuration());
            
            // Send teleport message
            player1.sendMessage("§aTeleported! Get ready to fight!");
            player2.sendMessage("§aTeleported! Get ready to fight!");
        } else {
            // Teleportation failed, return players to queue
            player1.sendMessage("§cTeleportation failed! Returning to queue...");
            player2.sendMessage("§cTeleportation failed! Returning to queue...");
            
            playersInMatch.remove(player1.getUniqueId());
            playersInMatch.remove(player2.getUniqueId());
            
            plugin.getQueueManager().addToQueue(player1, worldType);
            plugin.getQueueManager().addToQueue(player2, worldType);
        }
    }
    
    private void applyMatchEffects(Player player) {
        // Blindness
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 
                plugin.getConfigManager().getBlindnessDuration(), 0));
    }
    
    private void freezePlayer(Player player) {
        frozenPlayers.add(player.getUniqueId());
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 
                plugin.getConfigManager().getFreezeDuration(), 255, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 
                plugin.getConfigManager().getFreezeDuration(), 128, true, false));
    }
    
    private void unfreezePlayer(Player player) {
        frozenPlayers.remove(player.getUniqueId());
        player.removePotionEffect(PotionEffectType.SLOW);
        player.removePotionEffect(PotionEffectType.JUMP);
    }
    
    public void handlePlayerDisconnect(Player player) {
        if (isPlayerInMatch(player)) {
            // Kill the player for combat logging
            player.setHealth(0);
            
            // Broadcast combat log message
            String message = plugin.getMessageUtils().colorize(
                    plugin.getMessageUtils().getMessage("combat-log")
                            .replace("{player}", player.getName()));
            Bukkit.broadcastMessage(message);
            
            // Remove from match
            playersInMatch.remove(player.getUniqueId());
            frozenPlayers.remove(player.getUniqueId());
        }
    }
    
    public boolean isPlayerInMatch(Player player) {
        return playersInMatch.contains(player.getUniqueId());
    }
    
    public boolean isPlayerFrozen(Player player) {
        return frozenPlayers.contains(player.getUniqueId());
    }
    
    public void removeFromMatch(Player player) {
        playersInMatch.remove(player.getUniqueId());
        frozenPlayers.remove(player.getUniqueId());
    }
    
    public void shutdown() {
        playersInMatch.clear();
        frozenPlayers.clear();
    }
}
