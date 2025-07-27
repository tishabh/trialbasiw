
package com.rtpqueue.utils;

import com.rtpqueue.RTPQueue1v1;

public class ConfigManager {
    
    private final RTPQueue1v1 plugin;
    
    public ConfigManager(RTPQueue1v1 plugin) {
        this.plugin = plugin;
    }
    
    public int getAnnouncementInterval() {
        return plugin.getConfig().getInt("announcement-interval", 10);
    }
    
    public int getMaxTeleportAttempts() {
        return plugin.getConfig().getInt("max-teleport-attempts", 10);
    }
    
    public int getBlindnessDuration() {
        return plugin.getConfig().getInt("effects.blindness-duration", 40);
    }
    
    public int getFreezeDuration() {
        return plugin.getConfig().getInt("effects.freeze-duration", 100);
    }
    
    public int getSpawnDistance() {
        return plugin.getConfig().getInt("teleportation.spawn-distance", 5);
    }
    
    public int getSafeHeightBuffer() {
        return plugin.getConfig().getInt("teleportation.safe-height-buffer", 3);
    }
}
