
package com.rtpqueue.commands;

import com.rtpqueue.RTPQueue1v1;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RTPQueueCommand implements CommandExecutor {
    
    private final RTPQueue1v1 plugin;
    
    public RTPQueueCommand(RTPQueue1v1 plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("rtpqueue.use")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        // Check if player is already in queue
        if (plugin.getQueueManager().isPlayerInQueue(player)) {
            plugin.getQueueManager().removeFromQueue(player);
            return true;
        }
        
        // Check if player is in an active match
        if (plugin.getMatchManager().isPlayerInMatch(player)) {
            player.sendMessage("§cYou are currently in a match!");
            return true;
        }
        
        // Open GUI
        plugin.getGUIManager().openWorldSelectionGUI(player);
        return true;
    }
}
