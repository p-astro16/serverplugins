package com.example.teleportplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TradeManager {
    private final HashMap<UUID, TradeSession> activeTrades = new HashMap<>();
    private final HashMap<UUID, UUID> tradeRequests = new HashMap<>();
    private final TeleportPlugin plugin;

    public TradeManager(TeleportPlugin plugin) {
        this.plugin = plugin;
    }

    public void sendTradeRequest(UUID from, UUID to) {
        tradeRequests.put(to, from);
        
        // Auto-remove request after 30 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                if (tradeRequests.containsKey(to) && tradeRequests.get(to).equals(from)) {
                    tradeRequests.remove(to);
                    Player fromPlayer = Bukkit.getPlayer(from);
                    if (fromPlayer != null) {
                        fromPlayer.sendMessage(ChatColor.RED + "Your trade request expired.");
                    }
                }
            }
        }.runTaskLater(plugin, 600L); // 30 seconds
    }

    public UUID getTradeRequest(UUID to) {
        return tradeRequests.get(to);
    }

    public void removeTradeRequest(UUID to) {
        tradeRequests.remove(to);
    }

    public void startTrade(UUID player1, UUID player2) {
        TradeSession session = new TradeSession(player1, player2, plugin);
        activeTrades.put(player1, session);
        activeTrades.put(player2, session);
        removeTradeRequest(player1);
        removeTradeRequest(player2);
        
        Player p1 = Bukkit.getPlayer(player1);
        Player p2 = Bukkit.getPlayer(player2);
        if (p1 != null && p2 != null) {
            session.openTradeInventory(p1);
            session.openTradeInventory(p2);
        }
    }

    public TradeSession getTradeSession(UUID player) {
        return activeTrades.get(player);
    }

    public void cancelTrade(UUID player) {
        TradeSession session = activeTrades.get(player);
        if (session != null) {
            session.cancelTrade();
            activeTrades.remove(session.player1);
            activeTrades.remove(session.player2);
        }
    }

    public void removeTrade(UUID player1, UUID player2) {
        activeTrades.remove(player1);
        activeTrades.remove(player2);
    }

    public static class TradeSession {
        public final UUID player1;
        public final UUID player2;
        private final Inventory tradeInventory;
        private final TeleportPlugin plugin;
        
        private boolean player1Ready = false;
        private boolean player2Ready = false;
        private boolean player1Confirmed = false;
        private boolean player2Confirmed = false;
        private boolean tradeCompleted = false;
        
        private final ItemStack[] player1Items = new ItemStack[16];
        private final ItemStack[] player2Items = new ItemStack[16];
        
        private long lastModification = System.currentTimeMillis();

        public TradeSession(UUID player1, UUID player2, TeleportPlugin plugin) {
            this.player1 = player1;
            this.player2 = player2;
            this.plugin = plugin;
            Player p1 = Bukkit.getPlayer(player1);
            Player p2 = Bukkit.getPlayer(player2);
            String title = ChatColor.DARK_GREEN + "Trade: " + (p1 != null ? p1.getName() : "Unknown") + " ↔ " + (p2 != null ? p2.getName() : "Unknown");
            this.tradeInventory = Bukkit.createInventory(null, 54, title);
            setupTradeInventory();
        }

        private void setupTradeInventory() {
            // Fill with glass panes
            ItemStack grayPane = createGlassPane(Material.GRAY_STAINED_GLASS_PANE, " ");
            ItemStack greenPane = createGlassPane(Material.LIME_STAINED_GLASS_PANE, " ");
            ItemStack redPane = createGlassPane(Material.RED_STAINED_GLASS_PANE, " ");
            
            // Fill borders and dividers
            for (int i = 0; i < 54; i++) {
                if (i < 9 || i >= 45 || i % 9 == 4) {
                    tradeInventory.setItem(i, grayPane);
                }
            }
            
            // Ready/Confirm buttons
            updateReadyButton(false, false); // player1
            updateReadyButton(true, false);  // player2
            
            // Cancel button
            ItemStack cancelButton = new ItemStack(Material.BARRIER);
            ItemMeta cancelMeta = cancelButton.getItemMeta();
            cancelMeta.setDisplayName(ChatColor.RED + "Cancel Trade");
            cancelMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to cancel the trade"));
            cancelButton.setItemMeta(cancelMeta);
            tradeInventory.setItem(49, cancelButton);
        }

        private ItemStack createGlassPane(Material material, String name) {
            ItemStack pane = new ItemStack(material);
            ItemMeta meta = pane.getItemMeta();
            meta.setDisplayName(name);
            pane.setItemMeta(meta);
            return pane;
        }

        public void openTradeInventory(Player player) {
            player.openInventory(tradeInventory);
        }

        public boolean isValidSlot(int slot, UUID playerUUID) {
            if (playerUUID.equals(player1)) {
                // Player 1 slots: 9-12, 18-21, 27-30, 36-39 (left side)
                return (slot >= 9 && slot <= 12) || (slot >= 18 && slot <= 21) || 
                       (slot >= 27 && slot <= 30) || (slot >= 36 && slot <= 39);
            } else if (playerUUID.equals(player2)) {
                // Player 2 slots: 14-17, 23-26, 32-35, 41-44 (right side)
                return (slot >= 14 && slot <= 17) || (slot >= 23 && slot <= 26) || 
                       (slot >= 32 && slot <= 35) || (slot >= 41 && slot <= 44);
            }
            return false;
        }

        public void updateItems() {
            // Reset ready states when items change (anti-scam measure)
            if (player1Ready || player2Ready) {
                player1Ready = false;
                player2Ready = false;
                player1Confirmed = false;
                player2Confirmed = false;
                
                Player p1 = Bukkit.getPlayer(player1);
                Player p2 = Bukkit.getPlayer(player2);
                if (p1 != null) p1.sendMessage(ChatColor.YELLOW + "Items changed! Please ready up again.");
                if (p2 != null) p2.sendMessage(ChatColor.YELLOW + "Items changed! Please ready up again.");
                
                updateReadyButton(false, false);
                updateReadyButton(true, false);
            }
            
            lastModification = System.currentTimeMillis();
            
            // Update item displays
            updateItemDisplay();
        }

        private void updateItemDisplay() {
            // Store current items for verification
            int p1Index = 0, p2Index = 0;
            
            // Player 1 items
            for (int slot : Arrays.asList(9,10,11,12,18,19,20,21,27,28,29,30,36,37,38,39)) {
                player1Items[p1Index++] = tradeInventory.getItem(slot);
            }
            
            // Player 2 items  
            for (int slot : Arrays.asList(14,15,16,17,23,24,25,26,32,33,34,35,41,42,43,44)) {
                player2Items[p2Index++] = tradeInventory.getItem(slot);
            }
        }

        public void toggleReady(UUID playerUUID) {
            // Anti-scam: Must wait 2 seconds after last modification
            if (System.currentTimeMillis() - lastModification < 2000) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player != null) {
                    player.sendMessage(ChatColor.RED + "Please wait 2 seconds after changing items before readying up!");
                }
                return;
            }
            
            if (playerUUID.equals(player1)) {
                if (!player1Ready) {
                    player1Ready = true;
                    player1Confirmed = false;
                } else if (!player1Confirmed) {
                    player1Confirmed = true;
                } else {
                    player1Ready = false;
                    player1Confirmed = false;
                }
                updateReadyButton(false, player1Ready);
            } else if (playerUUID.equals(player2)) {
                if (!player2Ready) {
                    player2Ready = true;
                    player2Confirmed = false;
                } else if (!player2Confirmed) {
                    player2Confirmed = true;
                } else {
                    player2Ready = false;
                    player2Confirmed = false;
                }
                updateReadyButton(true, player2Ready);
            }
            
            checkTradeCompletion();
        }

        private void updateReadyButton(boolean isPlayer2, boolean ready) {
            int slot = isPlayer2 ? 52 : 46;
            UUID playerUUID = isPlayer2 ? player2 : player1;
            boolean confirmed = isPlayer2 ? player2Confirmed : player1Confirmed;
            
            ItemStack button;
            String playerName = Bukkit.getPlayer(playerUUID) != null ? Bukkit.getPlayer(playerUUID).getName() : "Unknown";
            
            if (confirmed) {
                button = new ItemStack(Material.EMERALD_BLOCK);
                ItemMeta meta = button.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN + playerName + " - CONFIRMED");
                meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "This player has confirmed the trade",
                    ChatColor.GRAY + "Click to unready"
                ));
                button.setItemMeta(meta);
            } else if (ready) {
                button = new ItemStack(Material.GOLD_BLOCK);
                ItemMeta meta = button.getItemMeta();
                meta.setDisplayName(ChatColor.YELLOW + playerName + " - READY");
                meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "This player is ready",
                    ChatColor.GRAY + "Click again to confirm",
                    ChatColor.RED + "Warning: Double check the items!"
                ));
                button.setItemMeta(meta);
            } else {
                button = new ItemStack(Material.RED_WOOL);
                ItemMeta meta = button.getItemMeta();
                meta.setDisplayName(ChatColor.RED + playerName + " - NOT READY");
                meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Click when ready to trade"
                ));
                button.setItemMeta(meta);
            }
            
            tradeInventory.setItem(slot, button);
        }

        public void handleButtonClick(UUID playerUUID, int slot) {
            if (slot == 46 && playerUUID.equals(player1)) {
                toggleReady(playerUUID);
            } else if (slot == 52 && playerUUID.equals(player2)) {
                toggleReady(playerUUID);
            } else if (slot == 49) {
                cancelTrade();
            }
        }

        private void checkTradeCompletion() {
            if (player1Confirmed && player2Confirmed && !tradeCompleted) {
                completeTrade();
            }
        }

        public void completeTrade() {
            if (tradeCompleted) return;
            tradeCompleted = true;
            
            Player p1 = Bukkit.getPlayer(player1);
            Player p2 = Bukkit.getPlayer(player2);
            
            if (p1 != null && p2 != null) {
                // Final verification - check if items match stored items
                if (!verifyItems()) {
                    p1.sendMessage(ChatColor.RED + "Trade cancelled - items were modified during confirmation!");
                    p2.sendMessage(ChatColor.RED + "Trade cancelled - items were modified during confirmation!");
                    cancelTrade();
                    return;
                }
                
                // Give player1 items to player2
                for (ItemStack item : player1Items) {
                    if (item != null && item.getType() != Material.AIR) {
                        HashMap<Integer, ItemStack> overflow = p2.getInventory().addItem(item);
                        for (ItemStack overflowItem : overflow.values()) {
                            p2.getWorld().dropItem(p2.getLocation(), overflowItem);
                        }
                    }
                }
                
                // Give player2 items to player1
                for (ItemStack item : player2Items) {
                    if (item != null && item.getType() != Material.AIR) {
                        HashMap<Integer, ItemStack> overflow = p1.getInventory().addItem(item);
                        for (ItemStack overflowItem : overflow.values()) {
                            p1.getWorld().dropItem(p1.getLocation(), overflowItem);
                        }
                    }
                }
                
                p1.sendMessage(ChatColor.GREEN + "Trade completed successfully with " + p2.getName() + "!");
                p2.sendMessage(ChatColor.GREEN + "Trade completed successfully with " + p1.getName() + "!");
                
                p1.closeInventory();
                p2.closeInventory();
                
                // Log trade for admins
                plugin.getLogger().info("Trade completed between " + p1.getName() + " and " + p2.getName());
            }
        }

        private boolean verifyItems() {
            int p1Index = 0, p2Index = 0;
            
            // Verify player 1 items
            for (int slot : Arrays.asList(9,10,11,12,18,19,20,21,27,28,29,30,36,37,38,39)) {
                ItemStack current = tradeInventory.getItem(slot);
                ItemStack stored = player1Items[p1Index++];
                if (!itemsEqual(current, stored)) {
                    return false;
                }
            }
            
            // Verify player 2 items
            for (int slot : Arrays.asList(14,15,16,17,23,24,25,26,32,33,34,35,41,42,43,44)) {
                ItemStack current = tradeInventory.getItem(slot);
                ItemStack stored = player2Items[p2Index++];
                if (!itemsEqual(current, stored)) {
                    return false;
                }
            }
            
            return true;
        }

        private boolean itemsEqual(ItemStack item1, ItemStack item2) {
            if (item1 == null && item2 == null) return true;
            if (item1 == null || item2 == null) return false;
            return item1.equals(item2);
        }

        public void cancelTrade() {
            if (tradeCompleted) return;
            
            Player p1 = Bukkit.getPlayer(player1);
            Player p2 = Bukkit.getPlayer(player2);
            
            // Return items to players
            if (p1 != null) {
                for (int slot : Arrays.asList(9,10,11,12,18,19,20,21,27,28,29,30,36,37,38,39)) {
                    ItemStack item = tradeInventory.getItem(slot);
                    if (item != null && item.getType() != Material.AIR) {
                        HashMap<Integer, ItemStack> overflow = p1.getInventory().addItem(item);
                        for (ItemStack overflowItem : overflow.values()) {
                            p1.getWorld().dropItem(p1.getLocation(), overflowItem);
                        }
                    }
                }
                p1.sendMessage(ChatColor.YELLOW + "Trade cancelled.");
                p1.closeInventory();
            }
            
            if (p2 != null) {
                for (int slot : Arrays.asList(14,15,16,17,23,24,25,26,32,33,34,35,41,42,43,44)) {
                    ItemStack item = tradeInventory.getItem(slot);
                    if (item != null && item.getType() != Material.AIR) {
                        HashMap<Integer, ItemStack> overflow = p2.getInventory().addItem(item);
                        for (ItemStack overflowItem : overflow.values()) {
                            p2.getWorld().dropItem(p2.getLocation(), overflowItem);
                        }
                    }
                }
                p2.sendMessage(ChatColor.YELLOW + "Trade cancelled.");
                p2.closeInventory();
            }
        }

        public Inventory getTradeInventory() {
            return tradeInventory;
        }
    }
}
