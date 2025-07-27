
package com.rtpqueue.managers;

import com.rtpqueue.RTPQueue1v1;
import com.rtpqueue.enums.WorldType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIManager implements Listener {
    
    private final RTPQueue1v1 plugin;
    private final Map<UUID, WorldType> pendingConfirmations;
    
    public GUIManager(RTPQueue1v1 plugin) {
        this.plugin = plugin;
        this.pendingConfirmations = new HashMap<>();
    }
    
    public void openWorldSelectionGUI(Player player) {
        String title = plugin.getMessageUtils().colorize(
                plugin.getMessageUtils().getMessage("gui-title"));
        Inventory gui = Bukkit.createInventory(null, 27, title);
        
        // Overworld
        ItemStack overworld = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta overworldMeta = overworld.getItemMeta();
        overworldMeta.setDisplayName("§a§lOverworld");
        overworldMeta.setLore(Arrays.asList(
                "§7Click to queue for the Overworld",
                "§7Fight in the main dimension!"
        ));
        overworld.setItemMeta(overworldMeta);
        gui.setItem(11, overworld);
        
        // Nether
        ItemStack nether = new ItemStack(Material.NETHERRACK);
        ItemMeta netherMeta = nether.getItemMeta();
        netherMeta.setDisplayName("§c§lNether");
        netherMeta.setLore(Arrays.asList(
                "§7Click to queue for the Nether",
                "§7Fight in the fiery depths!"
        ));
        nether.setItemMeta(netherMeta);
        gui.setItem(13, nether);
        
        // End
        ItemStack end = new ItemStack(Material.END_STONE);
        ItemMeta endMeta = end.getItemMeta();
        endMeta.setDisplayName("§d§lEnd");
        endMeta.setLore(Arrays.asList(
                "§7Click to queue for the End",
                "§7Fight in the void dimension!"
        ));
        end.setItemMeta(endMeta);
        gui.setItem(15, end);
        
        player.openInventory(gui);
    }
    
    public void openConfirmationGUI(Player player, WorldType worldType) {
        String title = plugin.getMessageUtils().colorize(
                plugin.getMessageUtils().getMessage("confirm-title")
                        .replace("{world}", worldType.getDisplayName()));
        Inventory gui = Bukkit.createInventory(null, 27, title);
        
        // Confirm (Green Wool)
        ItemStack confirm = new ItemStack(Material.GREEN_WOOL);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName("§a§lConfirm");
        confirmMeta.setLore(Arrays.asList(
                "§7Click to join the " + worldType.getDisplayName() + " queue",
                "§aConfirm your selection!"
        ));
        confirm.setItemMeta(confirmMeta);
        gui.setItem(11, confirm);
        
        // Cancel (Red Wool)
        ItemStack cancel = new ItemStack(Material.RED_WOOL);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName("§c§lCancel");
        cancelMeta.setLore(Arrays.asList(
                "§7Click to cancel and close GUI",
                "§cGo back to selection!"
        ));
        cancel.setItemMeta(cancelMeta);
        gui.setItem(15, cancel);
        
        // Store pending confirmation
        pendingConfirmations.put(player.getUniqueId(), worldType);
        
        player.openInventory(gui);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        
        String guiTitle = plugin.getMessageUtils().getMessage("gui-title");
        String confirmTitle = plugin.getMessageUtils().getMessage("confirm-title");
        
        if (event.getView().getTitle().contains(plugin.getMessageUtils().colorize(guiTitle))) {
            event.setCancelled(true);
            handleWorldSelection(player, event.getSlot());
        } else if (event.getView().getTitle().contains("Confirm Queue")) {
            event.setCancelled(true);
            handleConfirmation(player, event.getSlot());
        }
    }
    
    private void handleWorldSelection(Player player, int slot) {
        WorldType worldType = null;
        
        switch (slot) {
            case 11: // Overworld
                worldType = WorldType.OVERWORLD;
                break;
            case 13: // Nether
                worldType = WorldType.NETHER;
                break;
            case 15: // End
                worldType = WorldType.END;
                break;
            default:
                return;
        }
        
        player.closeInventory();
        openConfirmationGUI(player, worldType);
    }
    
    private void handleConfirmation(Player player, int slot) {
        WorldType worldType = pendingConfirmations.remove(player.getUniqueId());
        if (worldType == null) return;
        
        player.closeInventory();
        
        if (slot == 11) { // Confirm
            plugin.getQueueManager().addToQueue(player, worldType);
            player.sendMessage("§aYou have joined the " + worldType.getDisplayName() + " queue!");
        } else if (slot == 15) { // Cancel
            player.sendMessage("§cQueue cancelled.");
        }
    }
}
