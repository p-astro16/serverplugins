package com.example.teleportplugin;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import java.util.HashMap;
import java.util.UUID;

public class TeleportPlugin extends JavaPlugin implements Listener {
    private final HashMap<UUID, Location> homes = new HashMap<>();
    private final HashMap<UUID, TeleportRequest> teleportRequests = new HashMap<>();
    private TradeManager tradeManager;

    @Override
    public void onEnable() {
        // Initialize managers
        tradeManager = new TradeManager(this);
        
        // Register commands
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("sethome").setExecutor(new SetHomeCommand(this));
        getCommand("tp").setExecutor(new TpCommand(this));
        getCommand("tpaccept").setExecutor(new TpAcceptCommand(this));
        getCommand("trade").setExecutor(new TradeCommand(tradeManager));
        
        // Register events
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new TradeListener(tradeManager), this);
        
        getLogger().info("TeleportPlugin enabled!");
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Location deathLocation = event.getEntity().getLocation();
        String locationString = String.format("Death Location: World: %s, X: %.1f, Y: %.1f, Z: %.1f", 
            deathLocation.getWorld().getName(), 
            deathLocation.getX(), 
            deathLocation.getY(), 
            deathLocation.getZ());
        event.getEntity().sendMessage(locationString);
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

    public static class TeleportRequest {
        public final UUID from;
        public final UUID to;
        public TeleportRequest(UUID from, UUID to) {
            this.from = from;
            this.to = to;
        }
    }
}
