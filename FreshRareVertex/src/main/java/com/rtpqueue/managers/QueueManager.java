
package com.rtpqueue.managers;

import com.rtpqueue.RTPQueue1v1;
import com.rtpqueue.enums.WorldType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QueueManager {
    
    private final RTPQueue1v1 plugin;
    private final Map<WorldType, Set<Player>> queues;
    private final Map<Player, WorldType> playerQueues;
    private BukkitTask announcementTask;
    
    public QueueManager(RTPQueue1v1 plugin) {
        this.plugin = plugin;
        this.queues = new ConcurrentHashMap<>();
        this.playerQueues = new ConcurrentHashMap<>();
        
        // Initialize queues for each world type
        for (WorldType worldType : WorldType.values()) {
            queues.put(worldType, ConcurrentHashMap.newKeySet());
        }
        
        startAnnouncementTask();
    }
    
    public void addToQueue(Player player, WorldType worldType) {
        // Remove from any existing queue first
        removeFromQueue(player);
        
        queues.get(worldType).add(player);
        playerQueues.put(player, worldType);
        
        // Send professional join message to player
        String joinMessage = plugin.getMessageUtils().getMessage("queue-join")
                .replace("{player}", player.getName())
                .replace("{world}", worldType.getDisplayName());
        player.sendMessage(plugin.getMessageUtils().colorize(joinMessage));
        
        // Broadcast to server (optional - can be disabled by setting announcement-interval to 0)
        if (plugin.getConfigManager().getAnnouncementInterval() > 0) {
            String broadcastMessage = plugin.getMessageUtils().getMessage("queue-join-broadcast")
                    .replace("{player}", player.getName())
                    .replace("{world}", worldType.getDisplayName());
            Bukkit.broadcastMessage(plugin.getMessageUtils().colorize(broadcastMessage));
        }
        
        // Check for match
        checkForMatch(worldType);
    }
    
    public void removeFromQueue(Player player) {
        WorldType worldType = playerQueues.remove(player);
        if (worldType != null) {
            queues.get(worldType).remove(player);
            // Send professional leave message
            String leaveMessage = plugin.getMessageUtils().getMessage("queue-leave");
            player.sendMessage(plugin.getMessageUtils().colorize(leaveMessage));
        }
    }
    
    public boolean isPlayerInQueue(Player player) {
        return playerQueues.containsKey(player);
    }
    
    public WorldType getPlayerQueueWorld(Player player) {
        return playerQueues.get(player);
    }
    
    private void checkForMatch(WorldType worldType) {
        Set<Player> queue = queues.get(worldType);
        if (queue.size() >= 2) {
            Iterator<Player> iterator = queue.iterator();
            Player player1 = iterator.next();
            Player player2 = iterator.next();
            
            // Remove both players from queue
            iterator.remove();
            queue.remove(player1);
            playerQueues.remove(player1);
            playerQueues.remove(player2);
            
            // Start match
            plugin.getMatchManager().startMatch(player1, player2, worldType);
        }
    }
    
    private void startAnnouncementTask() {
        int interval = plugin.getConfigManager().getAnnouncementInterval();
        announcementTask = Bukkit.getScheduler().runTaskTimer(plugin, this::announceQueues, 
                interval * 20L, interval * 20L);
    }
    
    private void announceQueues() {
        // Count total players in all queues
        int totalInQueue = 0;
        StringBuilder queueStatus = new StringBuilder();
        
        for (Map.Entry<WorldType, Set<Player>> entry : queues.entrySet()) {
            Set<Player> queue = entry.getValue();
            if (!queue.isEmpty()) {
                totalInQueue += queue.size();
                WorldType worldType = entry.getKey();
                queueStatus.append("&b").append(worldType.getDisplayName())
                          .append("&7: &a").append(queue.size()).append(" &7| ");
            }
        }
        
        // Only announce if there are players in queue
        if (totalInQueue > 0) {
            String announcement = "&8[&6&lRTP Queue&8] &7Queue Status: " + queueStatus.toString();
            announcement = announcement.substring(0, announcement.length() - 3); // Remove last " | "
            announcement += " &8(&7Total: &a" + totalInQueue + "&8)";
            
            Bukkit.broadcastMessage(plugin.getMessageUtils().colorize(announcement));
        }
    }
    
    public void shutdown() {
        if (announcementTask != null) {
            announcementTask.cancel();
        }
        queues.clear();
        playerQueues.clear();
    }
    
    public Set<Player> getQueue(WorldType worldType) {
        return new HashSet<>(queues.get(worldType));
    }
}
