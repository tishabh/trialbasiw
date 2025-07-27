
package com.rtpqueue.enums;

import org.bukkit.World;

public enum WorldType {
    OVERWORLD("Overworld", World.Environment.NORMAL),
    NETHER("Nether", World.Environment.NETHER),
    END("End", World.Environment.THE_END);
    
    private final String displayName;
    private final World.Environment environment;
    
    WorldType(String displayName, World.Environment environment) {
        this.displayName = displayName;
        this.environment = environment;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public World.Environment getEnvironment() {
        return environment;
    }
}
