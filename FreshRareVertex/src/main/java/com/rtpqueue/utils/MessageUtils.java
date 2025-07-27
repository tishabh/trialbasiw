
package com.rtpqueue.utils;

import com.rtpqueue.RTPQueue1v1;
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {
    
    private final RTPQueue1v1 plugin;
    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<gradient:(#[a-fA-F0-9]{6}):(#[a-fA-F0-9]{6})>(.*?)</gradient>");
    
    public MessageUtils(RTPQueue1v1 plugin) {
        this.plugin = plugin;
    }
    
    public String getMessage(String key) {
        return plugin.getConfig().getString("messages." + key, "§cMessage not found: " + key);
    }
    
    public String colorize(String message) {
        // Handle gradient colors (simplified version)
        Matcher matcher = GRADIENT_PATTERN.matcher(message);
        if (matcher.find()) {
            String content = matcher.group(3);
            // For simplicity, just use the first color
            message = message.replaceAll(GRADIENT_PATTERN.pattern(), "§b" + content);
        }
        
        // Handle standard color codes
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
