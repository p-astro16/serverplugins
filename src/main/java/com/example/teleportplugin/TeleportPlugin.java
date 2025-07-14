package com.example.teleportplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import java.util.HashMap;
import java.util.UUID;
import java.util.List;

public class TeleportPlugin extends JavaPlugin implements Listener {
    private final HashMap<UUID, Location> homes = new HashMap<>();
    private final HashMap<UUID, TeleportRequest> teleportRequests = new HashMap<>();
    private TradeManager tradeManager;
    private ProductScraper productScraper;

    @Override
    public void onEnable() {
        // Initialize managers
        tradeManager = new TradeManager(this);
        productScraper = new ProductScraper(this);
        
        // Register commands
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("sethome").setExecutor(new SetHomeCommand(this));
        getCommand("tp").setExecutor(new TpCommand(this));
        getCommand("tpaccept").setExecutor(new TpAcceptCommand(this));
        getCommand("trade").setExecutor(new TradeCommand(tradeManager));
        getCommand("product").setExecutor(new ProductCommand(productScraper));
        
        // Register events
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new TradeListener(tradeManager), this);
        
        getLogger().info("TeleportPlugin enabled!");
        getLogger().info("Product scraping system initialized with fallback methods");
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location deathLocation = player.getLocation();
        
        // Get all dropped items
        List<ItemStack> droppedItems = event.getDrops();
        
        // Debug message
        player.sendMessage(ChatColor.GRAY + "Death detected! Items to store: " + droppedItems.size());
        
        // Clear drops so they don't scatter
        event.getDrops().clear();
        
        // Announce death location in chat
        String locationString = String.format("%s%s died at: %sWorld: %s, X: %.0f, Y: %.0f, Z: %.0f", 
            ChatColor.RED, player.getName(),
            ChatColor.YELLOW,
            deathLocation.getWorld().getName(), 
            deathLocation.getX(), 
            deathLocation.getY(), 
            deathLocation.getZ());
        
        // Broadcast to all players
        Bukkit.broadcastMessage(locationString);
        
        // Always create death chest and gravestone, even if no items
        try {
            createDeathChest(deathLocation, droppedItems, player);
            player.sendMessage(ChatColor.GREEN + "Death chest and gravestone created successfully!");
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Error creating death chest: " + e.getMessage());
            getLogger().warning("Error creating death chest for " + player.getName() + ": " + e.getMessage());
            
            // Fallback: give items back to player when they respawn
            if (!droppedItems.isEmpty()) {
                getServer().getScheduler().runTaskLater(this, () -> {
                    for (ItemStack item : droppedItems) {
                        if (item != null && item.getType() != Material.AIR) {
                            player.getInventory().addItem(item);
                        }
                    }
                    player.sendMessage(ChatColor.YELLOW + "Items returned to your inventory due to death chest error!");
                }, 20L);
            }
        }
    }
    
    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            // Skip night with just one player
            World world = event.getPlayer().getWorld();
            getServer().getScheduler().runTaskLater(this, () -> {
                if (world.getTime() > 12000) { // If it's night time
                    world.setTime(0); // Set to morning
                    world.setStorm(false);
                    world.setThundering(false);
                    Bukkit.broadcastMessage(ChatColor.YELLOW + event.getPlayer().getName() + " slept through the night!");
                }
            }, 20L); // 1 second delay
        }
    }
    
    private void createDeathChest(Location location, List<ItemStack> items, Player player) {
        // Find a safe location for the chest (ground level)
        Location chestLocation = location.clone();
        chestLocation.setY(Math.floor(location.getY()));
        
        // Make sure the location is safe and clear
        Block blockAtLocation = chestLocation.getBlock();
        
        // If the block is not air, try to find a safe spot nearby
        if (blockAtLocation.getType() != Material.AIR) {
            boolean foundSafeSpot = false;
            
            // Try above first
            for (int y = 1; y <= 10; y++) {
                Block testBlock = chestLocation.clone().add(0, y, 0).getBlock();
                if (testBlock.getType() == Material.AIR && 
                    testBlock.getRelative(0, 1, 0).getType() == Material.AIR) {
                    chestLocation.add(0, y, 0);
                    foundSafeSpot = true;
                    break;
                }
            }
            
            // If no safe spot above, try nearby horizontal spots
            if (!foundSafeSpot) {
                for (int x = -2; x <= 2 && !foundSafeSpot; x++) {
                    for (int z = -2; z <= 2 && !foundSafeSpot; z++) {
                        if (x == 0 && z == 0) continue;
                        
                        Block testBlock = chestLocation.clone().add(x, 0, z).getBlock();
                        if (testBlock.getType() == Material.AIR && 
                            testBlock.getRelative(0, 1, 0).getType() == Material.AIR) {
                            chestLocation.add(x, 0, z);
                            foundSafeSpot = true;
                            break;
                        }
                    }
                }
            }
        }
        
        // Place chest
        Block chestBlock = chestLocation.getBlock();
        chestBlock.setType(Material.CHEST);
        
        // Fill chest with items
        if (chestBlock.getState() instanceof Chest) {
            Chest chest = (Chest) chestBlock.getState();
            
            for (ItemStack item : items) {
                if (item != null && item.getType() != Material.AIR) {
                    chest.getInventory().addItem(item);
                }
            }
            
            chest.update();
        }
        
        // Place gravestone using custom block display and decorations
        createGravestone(chestLocation, player);
        
        // Send message to player
        player.sendMessage(ChatColor.GREEN + "Your items have been stored in a death chest at your death location!");
        player.sendMessage(ChatColor.GRAY + "Coordinates: X: " + (int)chestLocation.getX() + 
                          ", Y: " + (int)chestLocation.getY() + 
                          ", Z: " + (int)chestLocation.getZ());
    }
    
    private void createGravestone(Location chestLocation, Player player) {
        // Create a more elaborate gravestone structure behind the chest
        Location gravestoneLocation = chestLocation.clone().add(0, 0, -1);
        
        // Only place gravestone blocks if the spots are available
        Block baseBlock = gravestoneLocation.getBlock();
        if (baseBlock.getType() == Material.AIR) {
            baseBlock.setType(Material.COBBLESTONE);
            
            // Place gravestone marker (stone slab) on top of base
            Location markerLocation = gravestoneLocation.clone().add(0, 1, 0);
            Block markerBlock = markerLocation.getBlock();
            if (markerBlock.getType() == Material.AIR) {
                markerBlock.setType(Material.STONE_SLAB);
                
                // Place player head as memorial on top of slab
                Location headLocation = markerLocation.clone().add(0, 1, 0);
                Block headBlock = headLocation.getBlock();
                if (headBlock.getType() == Material.AIR) {
                    headBlock.setType(Material.PLAYER_HEAD);
                    // Note: Setting player head texture would require more complex NBT manipulation
                }
            }
        }
        
        // Add some decorative flowers around the grave
        addGraveDecoration(gravestoneLocation);
    }
    
    private void addGraveDecoration(Location gravestoneLocation) {
        // Add some flowers around the gravestone
        Location[] flowerSpots = {
            gravestoneLocation.clone().add(1, 0, 0),
            gravestoneLocation.clone().add(-1, 0, 0),
            gravestoneLocation.clone().add(0, 0, 1),
            gravestoneLocation.clone().add(1, 0, 1),
            gravestoneLocation.clone().add(-1, 0, 1)
        };
        
        Material[] flowers = {
            Material.POPPY, Material.DANDELION, Material.BLUE_ORCHID, 
            Material.ALLIUM, Material.AZURE_BLUET, Material.RED_TULIP
        };
        
        for (Location spot : flowerSpots) {
            Block block = spot.getBlock();
            Block blockBelow = spot.clone().add(0, -1, 0).getBlock();
            
            // Only place flowers on air blocks that have solid ground beneath
            if (block.getType() == Material.AIR && 
                (blockBelow.getType().isSolid() || blockBelow.getType() == Material.GRASS_BLOCK)) {
                
                // Randomly place flowers
                if (Math.random() < 0.4) { // 40% chance for flower
                    Material flower = flowers[(int) (Math.random() * flowers.length)];
                    block.setType(flower);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("TeleportPlugin disabled!");
    }

    public Location getHome(UUID uuid) {
        return homes.get(uuid);
    }

    public void setHome(UUID uuid, Location location) {
        homes.put(uuid, location);
    }

    public void addTeleportRequest(UUID from, UUID to) {
        teleportRequests.put(to, new TeleportRequest(from, to));
    }

    public TeleportRequest getTeleportRequest(UUID to) {
        return teleportRequests.get(to);
    }

    public void removeTeleportRequest(UUID to) {
        teleportRequests.remove(to);
    }

    public ProductScraper getProductScraper() {
        return productScraper;
    }

    public static class TeleportRequest {
        public final UUID from;
        public final UUID to;
        public TeleportRequest(UUID from, UUID to) {
            this.from = from;
            this.to = to;
        }
    }
}
