package com.example.teleportplugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class TradeListener implements Listener {
    private final TradeManager tradeManager;

    public TradeListener(TradeManager tradeManager) {
        this.tradeManager = tradeManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        
        TradeManager.TradeSession session = tradeManager.getTradeSession(player.getUniqueId());
        if (session == null) return;
        
        // Check if this is the trade inventory
        if (!event.getInventory().equals(session.getTradeInventory())) return;
        
        int slot = event.getSlot();
        
        // Handle button clicks
        if (slot == 46 || slot == 52 || slot == 49) {
            event.setCancelled(true);
            session.handleButtonClick(player.getUniqueId(), slot);
            return;
        }
        
        // Check if player is clicking in a valid slot
        if (!session.isValidSlot(slot, player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You can only place items on your side of the trade!");
            return;
        }
        
        // Prevent placing certain items
        ItemStack item = event.getCursor();
        if (item != null && isBlacklistedItem(item)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot trade this item!");
            return;
        }
        
        // Allow the click and update the trade
        session.updateItems();
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        
        TradeManager.TradeSession session = tradeManager.getTradeSession(player.getUniqueId());
        if (session == null) return;
        
        // Check if this is the trade inventory
        if (!event.getInventory().equals(session.getTradeInventory())) return;
        
        // If inventory was closed, cancel the trade after a short delay
        // This gives the player a chance to reopen if it was accidental
        player.getServer().getScheduler().runTaskLater(
            player.getServer().getPluginManager().getPlugin("TeleportPlugin"), 
            () -> {
                TradeManager.TradeSession currentSession = tradeManager.getTradeSession(player.getUniqueId());
                if (currentSession != null && currentSession.equals(session)) {
                    // Check if player still has the trade inventory open
                    if (!player.getOpenInventory().getTopInventory().equals(session.getTradeInventory())) {
                        tradeManager.cancelTrade(player.getUniqueId());
                        player.sendMessage(ChatColor.YELLOW + "Trade cancelled because you closed the trade window.");
                    }
                }
            }, 
            20L // 1 second delay
        );
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        TradeManager.TradeSession session = tradeManager.getTradeSession(player.getUniqueId());
        if (session != null) {
            tradeManager.cancelTrade(player.getUniqueId());
        }
    }
    
    private boolean isBlacklistedItem(ItemStack item) {
        // Add items that shouldn't be tradeable
        Material type = item.getType();
        return type == Material.BEDROCK || 
               type == Material.COMMAND_BLOCK ||
               type == Material.CHAIN_COMMAND_BLOCK ||
               type == Material.REPEATING_COMMAND_BLOCK ||
               type == Material.BARRIER ||
               type == Material.STRUCTURE_BLOCK ||
               type == Material.STRUCTURE_VOID;
    }
}
