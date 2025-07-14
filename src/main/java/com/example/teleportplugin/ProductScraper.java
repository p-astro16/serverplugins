package com.example.teleportplugin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/**
 * Java integration for the Python product scraping system
 * Handles failed product scraping with fallback methods
 */
public class ProductScraper {
    
    private final JavaPlugin plugin;
    private final Gson gson;
    private final File cacheFile;
    private final File logFile;
    private Map<String, ProductInfo> productCache;
    
    public ProductScraper(JavaPlugin plugin) {
        this.plugin = plugin;
        this.gson = new Gson();
        this.cacheFile = new File(plugin.getDataFolder(), "product_cache.json");
        this.logFile = new File(plugin.getDataFolder(), "pasted.txt");
        this.productCache = new HashMap<>();
        loadCache();
    }
    
    /**
     * Product information data class
     */
    public static class ProductInfo {
        public String id;
        public String name;
        public String description;
        public String rarity;
        public int max_stack_size;
        public boolean tradeable;
        public double value;
        public String source_method;
        public String last_updated;
        
        public ProductInfo() {}
        
        public ProductInfo(String id, String name, String description, String rarity, 
                          int maxStackSize, boolean tradeable, double value, 
                          String sourceMethod, String lastUpdated) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.rarity = rarity;
            this.max_stack_size = maxStackSize;
            this.tradeable = tradeable;
            this.value = value;
            this.source_method = sourceMethod;
            this.last_updated = lastUpdated;
        }
        
        @Override
        public String toString() {
            return String.format("ProductInfo{id='%s', name='%s', rarity='%s', value=%.2f, method='%s'}", 
                               id, name, rarity, value, source_method);
        }
    }
    
    /**
     * Load cached product data from file
     */
    private void loadCache() {
        if (!cacheFile.exists()) {
            plugin.getLogger().info("No product cache found, starting fresh");
            return;
        }
        
        try (FileReader reader = new FileReader(cacheFile)) {
            Type type = new TypeToken<Map<String, ProductInfo>>(){}.getType();
            Map<String, ProductInfo> loadedCache = gson.fromJson(reader, type);
            if (loadedCache != null) {
                productCache = loadedCache;
                plugin.getLogger().info("Loaded " + productCache.size() + " products from cache");
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error loading product cache", e);
        }
    }
    
    /**
     * Save product cache to file
     */
    private void saveCache() {
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            
            try (FileWriter writer = new FileWriter(cacheFile)) {
                gson.toJson(productCache, writer);
            }
            plugin.getLogger().info("Saved " + productCache.size() + " products to cache");
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error saving product cache", e);
        }
    }
    
    /**
     * Get product information from cache
     */
    public ProductInfo getProduct(String productId) {
        return productCache.get(productId);
    }
    
    /**
     * Get all cached products
     */
    public Map<String, ProductInfo> getAllProducts() {
        return new HashMap<>(productCache);
    }
    
    /**
     * Check if a product exists in cache
     */
    public boolean hasProduct(String productId) {
        return productCache.containsKey(productId);
    }
    
    /**
     * Get products by rarity
     */
    public List<ProductInfo> getProductsByRarity(String rarity) {
        List<ProductInfo> result = new ArrayList<>();
        for (ProductInfo product : productCache.values()) {
            if (rarity.equalsIgnoreCase(product.rarity)) {
                result.add(product);
            }
        }
        return result;
    }
    
    /**
     * Get tradeable products
     */
    public List<ProductInfo> getTradeableProducts() {
        List<ProductInfo> result = new ArrayList<>();
        for (ProductInfo product : productCache.values()) {
            if (product.tradeable) {
                result.add(product);
            }
        }
        return result;
    }
    
    /**
     * Run the Python scraper to recover failed products
     * This method implements the main requirement from the problem statement
     */
    public CompletableFuture<Boolean> runProductRecovery() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        // Run asynchronously to avoid blocking the main thread
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    plugin.getLogger().info("Starting product recovery using Python scraper");
                    
                    // Ensure the Python script exists
                    File pythonScript = new File(plugin.getDataFolder().getParentFile().getParentFile().getParentFile(), "scrape_products.py");
                    if (!pythonScript.exists()) {
                        plugin.getLogger().warning("Python scraper script not found: " + pythonScript.getAbsolutePath());
                        future.complete(false);
                        return;
                    }
                    
                    // Ensure the log file exists
                    if (!logFile.exists()) {
                        plugin.getLogger().warning("Log file not found: " + logFile.getAbsolutePath());
                        future.complete(false);
                        return;
                    }
                    
                    // Execute the Python script
                    ProcessBuilder pb = new ProcessBuilder("python3", pythonScript.getAbsolutePath());
                    pb.directory(pythonScript.getParentFile());
                    pb.redirectErrorStream(true);
                    
                    Process process = pb.start();
                    
                    // Capture output
                    StringBuilder output = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            output.append(line).append("\n");
                            plugin.getLogger().info("Python scraper: " + line);
                        }
                    }
                    
                    // Wait for completion
                    int exitCode = process.waitFor();
                    
                    if (exitCode == 0) {
                        plugin.getLogger().info("Python scraper completed successfully");
                        
                        // Reload cache with newly scraped products
                        loadCache();
                        
                        plugin.getLogger().info("Product recovery completed. Total products: " + productCache.size());
                        future.complete(true);
                    } else {
                        plugin.getLogger().warning("Python scraper failed with exit code: " + exitCode);
                        future.complete(false);
                    }
                    
                } catch (Exception e) {
                    plugin.getLogger().log(Level.SEVERE, "Error running product recovery", e);
                    future.complete(false);
                }
            }
        }.runTaskAsynchronously(plugin);
        
        return future;
    }
    
    /**
     * Add a failed product to the log for later recovery
     */
    public void logFailedProduct(String productId, String errorMessage) {
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            
            String timestamp = new Date().toString();
            String logEntry = String.format("[%s] ERROR: Failed to scrape %s - %s\n[%s] WARN: Product ID: %s - Primary scraping failed\n\n",
                                          timestamp, productId.replace("minecraft:", "").replace("_", " "), 
                                          errorMessage, timestamp, productId);
            
            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.write(logEntry);
            }
            
            plugin.getLogger().info("Logged failed product: " + productId);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error logging failed product", e);
        }
    }
    
    /**
     * Create a default product if not found in cache
     */
    public ProductInfo createDefaultProduct(String productId) {
        String name = productId.replace("minecraft:", "").replace("_", " ");
        name = Arrays.stream(name.split(" "))
                    .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                    .reduce("", (a, b) -> a + " " + b).trim();
        
        String rarity = "common";
        double value = 1.0;
        int maxStack = 64;
        
        // Determine rarity and value based on item name
        String lowerId = productId.toLowerCase();
        if (lowerId.contains("diamond") || lowerId.contains("netherite") || lowerId.contains("dragon")) {
            rarity = "rare";
            value = 100.0;
        } else if (lowerId.contains("enchanted") || lowerId.contains("golden") || lowerId.contains("emerald")) {
            rarity = "uncommon";
            value = 25.0;
        }
        
        if (lowerId.contains("sword") || lowerId.contains("bow") || lowerId.contains("trident") || 
            lowerId.contains("helmet") || lowerId.contains("chestplate") || lowerId.contains("leggings") || 
            lowerId.contains("boots")) {
            maxStack = 1;
            value = Math.max(value, 10.0);
        }
        
        ProductInfo product = new ProductInfo(productId, name, "Default information for " + name, 
                                            rarity, maxStack, true, value, "default_java", 
                                            new Date().toString());
        
        // Add to cache
        productCache.put(productId, product);
        saveCache();
        
        plugin.getLogger().info("Created default product: " + product);
        return product;
    }
    
    /**
     * Get or create product with fallback methods
     * This implements the main requirement: if primary scraping fails, use alternative methods
     */
    public ProductInfo getOrCreateProduct(String productId) {
        // First check cache
        ProductInfo cached = getProduct(productId);
        if (cached != null) {
            return cached;
        }
        
        // Log as failed and create default
        logFailedProduct(productId, "Product not found in cache");
        return createDefaultProduct(productId);
    }
    
    /**
     * Get product statistics
     */
    public Map<String, Integer> getProductStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        Map<String, Integer> methodCounts = new HashMap<>();
        Map<String, Integer> rarityCounts = new HashMap<>();
        
        for (ProductInfo product : productCache.values()) {
            methodCounts.merge(product.source_method, 1, Integer::sum);
            rarityCounts.merge(product.rarity, 1, Integer::sum);
        }
        
        stats.put("total", productCache.size());
        stats.put("tradeable", (int) productCache.values().stream().filter(p -> p.tradeable).count());
        stats.putAll(methodCounts);
        stats.putAll(rarityCounts);
        
        return stats;
    }
}