
package com.rtpqueue;

import com.rtpqueue.commands.RTPQueueCommand;
import com.rtpqueue.listeners.PlayerListener;
import com.rtpqueue.managers.QueueManager;
import com.rtpqueue.managers.MatchManager;
import com.rtpqueue.managers.GUIManager;
import com.rtpqueue.utils.ConfigManager;
import com.rtpqueue.utils.MessageUtils;
import org.bukkit.plugin.java.JavaPlugin;

public class RTPQueue1v1 extends JavaPlugin {
    
    private static RTPQueue1v1 instance;
    private QueueManager queueManager;
    private MatchManager matchManager;
    private GUIManager guiManager;
    private ConfigManager configManager;
    private MessageUtils messageUtils;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Initialize managers
        configManager = new ConfigManager(this);
        messageUtils = new MessageUtils(this);
        queueManager = new QueueManager(this);
        matchManager = new MatchManager(this);
        guiManager = new GUIManager(this);
        
        // Register commands
        getCommand("rtpqueue").setExecutor(new RTPQueueCommand(this));
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(guiManager, this);
        
        getLogger().info("RTPQueue1v1 has been enabled!");
    }
    
    @Override
    public void onDisable() {
        if (queueManager != null) {
            queueManager.shutdown();
        }
        if (matchManager != null) {
            matchManager.shutdown();
        }
        getLogger().info("RTPQueue1v1 has been disabled!");
    }
    
    public static RTPQueue1v1 getInstance() {
        return instance;
    }
    
    public QueueManager getQueueManager() {
        return queueManager;
    }
    
    public MatchManager getMatchManager() {
        return matchManager;
    }
    
    public GUIManager getGUIManager() {
        return guiManager;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public MessageUtils getMessageUtils() {
        return messageUtils;
    }
}
