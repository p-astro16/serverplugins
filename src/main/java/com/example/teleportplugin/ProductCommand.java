package com.example.teleportplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * Command handler for product scraping and management
 * Provides commands to interact with the product scraping system
 */
public class ProductCommand implements CommandExecutor {
    
    private final ProductScraper productScraper;
    
    public ProductCommand(ProductScraper productScraper) {
        this.productScraper = productScraper;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "info":
                return handleProductInfo(sender, args);
            case "list":
                return handleProductList(sender, args);
            case "search":
                return handleProductSearch(sender, args);
            case "recover":
                return handleProductRecover(sender);
            case "stats":
                return handleProductStats(sender);
            case "rarity":
                return handleProductByRarity(sender, args);
            default:
                sendUsage(sender);
                return true;
        }
    }
    
    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Product Scraper Commands:");
        sender.sendMessage(ChatColor.WHITE + "/product info <id>" + ChatColor.GRAY + " - Get product information");
        sender.sendMessage(ChatColor.WHITE + "/product list" + ChatColor.GRAY + " - List all cached products");
        sender.sendMessage(ChatColor.WHITE + "/product search <name>" + ChatColor.GRAY + " - Search products by name");
        sender.sendMessage(ChatColor.WHITE + "/product recover" + ChatColor.GRAY + " - Run product recovery (fallback methods)");
        sender.sendMessage(ChatColor.WHITE + "/product stats" + ChatColor.GRAY + " - Show product statistics");
        sender.sendMessage(ChatColor.WHITE + "/product rarity <rarity>" + ChatColor.GRAY + " - List products by rarity");
    }
    
    private boolean handleProductInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /product info <product_id>");
            return true;
        }
        
        String productId = args[1];
        if (!productId.startsWith("minecraft:")) {
            productId = "minecraft:" + productId;
        }
        
        ProductScraper.ProductInfo product = productScraper.getProduct(productId);
        if (product == null) {
            sender.sendMessage(ChatColor.YELLOW + "Product not found in cache. Attempting to create with fallback methods...");
            product = productScraper.getOrCreateProduct(productId);
        }
        
        sender.sendMessage(ChatColor.GREEN + "=== Product Information ===");
        sender.sendMessage(ChatColor.YELLOW + "ID: " + ChatColor.WHITE + product.id);
        sender.sendMessage(ChatColor.YELLOW + "Name: " + ChatColor.WHITE + product.name);
        sender.sendMessage(ChatColor.YELLOW + "Description: " + ChatColor.WHITE + product.description);
        sender.sendMessage(ChatColor.YELLOW + "Rarity: " + ChatColor.WHITE + product.rarity);
        sender.sendMessage(ChatColor.YELLOW + "Max Stack: " + ChatColor.WHITE + product.max_stack_size);
        sender.sendMessage(ChatColor.YELLOW + "Tradeable: " + ChatColor.WHITE + (product.tradeable ? "Yes" : "No"));
        sender.sendMessage(ChatColor.YELLOW + "Value: " + ChatColor.WHITE + String.format("%.2f", product.value));
        sender.sendMessage(ChatColor.YELLOW + "Source: " + ChatColor.WHITE + product.source_method);
        sender.sendMessage(ChatColor.YELLOW + "Last Updated: " + ChatColor.WHITE + product.last_updated);
        
        return true;
    }
    
    private boolean handleProductList(CommandSender sender, String[] args) {
        Map<String, ProductScraper.ProductInfo> products = productScraper.getAllProducts();
        
        if (products.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No products in cache. Use '/product recover' to scrape products.");
            return true;
        }
        
        int page = 1;
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid page number.");
                return true;
            }
        }
        
        int itemsPerPage = 10;
        int totalPages = (int) Math.ceil((double) products.size() / itemsPerPage);
        page = Math.max(1, Math.min(page, totalPages));
        
        sender.sendMessage(ChatColor.GREEN + "=== Product List (Page " + page + "/" + totalPages + ") ===");
        
        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, products.size());
        
        int index = 0;
        for (ProductScraper.ProductInfo product : products.values()) {
            if (index >= startIndex && index < endIndex) {
                String rarityColor = getRarityColor(product.rarity);
                sender.sendMessage(ChatColor.YELLOW + "• " + ChatColor.WHITE + product.name + 
                                 " " + rarityColor + "(" + product.rarity + ")" + 
                                 ChatColor.GRAY + " - Value: " + String.format("%.2f", product.value));
            }
            index++;
            if (index >= endIndex) break;
        }
        
        if (totalPages > 1) {
            sender.sendMessage(ChatColor.GRAY + "Use '/product list <page>' to see other pages");
        }
        
        return true;
    }
    
    private boolean handleProductSearch(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /product search <name>");
            return true;
        }
        
        String searchTerm = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length)).toLowerCase();
        Map<String, ProductScraper.ProductInfo> products = productScraper.getAllProducts();
        
        sender.sendMessage(ChatColor.GREEN + "=== Search Results for '" + searchTerm + "' ===");
        
        int found = 0;
        for (ProductScraper.ProductInfo product : products.values()) {
            if (product.name.toLowerCase().contains(searchTerm) || 
                product.id.toLowerCase().contains(searchTerm) ||
                product.description.toLowerCase().contains(searchTerm)) {
                
                String rarityColor = getRarityColor(product.rarity);
                sender.sendMessage(ChatColor.YELLOW + "• " + ChatColor.WHITE + product.name + 
                                 " " + rarityColor + "(" + product.rarity + ")" + 
                                 ChatColor.GRAY + " - " + product.id);
                found++;
            }
        }
        
        if (found == 0) {
            sender.sendMessage(ChatColor.RED + "No products found matching '" + searchTerm + "'");
        } else {
            sender.sendMessage(ChatColor.GREEN + "Found " + found + " product(s)");
        }
        
        return true;
    }
    
    private boolean handleProductRecover(CommandSender sender) {
        if (!(sender instanceof Player) || sender.hasPermission("product.admin")) {
            sender.sendMessage(ChatColor.YELLOW + "Starting product recovery with fallback methods...");
            sender.sendMessage(ChatColor.GRAY + "This may take a few moments...");
            
            productScraper.runProductRecovery().thenAccept(success -> {
                if (success) {
                    sender.sendMessage(ChatColor.GREEN + "Product recovery completed successfully!");
                    sender.sendMessage(ChatColor.YELLOW + "Use '/product stats' to see the results");
                } else {
                    sender.sendMessage(ChatColor.RED + "Product recovery failed. Check console for errors.");
                }
            });
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission to run product recovery.");
        }
        
        return true;
    }
    
    private boolean handleProductStats(CommandSender sender) {
        Map<String, Integer> stats = productScraper.getProductStatistics();
        
        sender.sendMessage(ChatColor.GREEN + "=== Product Statistics ===");
        sender.sendMessage(ChatColor.YELLOW + "Total Products: " + ChatColor.WHITE + stats.getOrDefault("total", 0));
        sender.sendMessage(ChatColor.YELLOW + "Tradeable Products: " + ChatColor.WHITE + stats.getOrDefault("tradeable", 0));
        
        sender.sendMessage(ChatColor.GREEN + "By Source Method:");
        for (String method : new String[]{"primary_api", "fallback_api", "game_data", "community_db", "default_values", "default_java"}) {
            int count = stats.getOrDefault(method, 0);
            if (count > 0) {
                sender.sendMessage(ChatColor.YELLOW + "  " + method + ": " + ChatColor.WHITE + count);
            }
        }
        
        sender.sendMessage(ChatColor.GREEN + "By Rarity:");
        for (String rarity : new String[]{"common", "uncommon", "rare", "epic", "legendary"}) {
            int count = stats.getOrDefault(rarity, 0);
            if (count > 0) {
                String color = getRarityColor(rarity);
                sender.sendMessage(ChatColor.YELLOW + "  " + color + rarity + ": " + ChatColor.WHITE + count);
            }
        }
        
        return true;
    }
    
    private boolean handleProductByRarity(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /product rarity <rarity>");
            sender.sendMessage(ChatColor.GRAY + "Available rarities: common, uncommon, rare, epic, legendary");
            return true;
        }
        
        String rarity = args[1].toLowerCase();
        List<ProductScraper.ProductInfo> products = productScraper.getProductsByRarity(rarity);
        
        if (products.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No products found with rarity '" + rarity + "'");
            return true;
        }
        
        String rarityColor = getRarityColor(rarity);
        sender.sendMessage(ChatColor.GREEN + "=== " + rarityColor + rarity.toUpperCase() + ChatColor.GREEN + " Products ===");
        
        for (ProductScraper.ProductInfo product : products) {
            sender.sendMessage(ChatColor.YELLOW + "• " + ChatColor.WHITE + product.name + 
                             ChatColor.GRAY + " - Value: " + String.format("%.2f", product.value) +
                             " (Stack: " + product.max_stack_size + ")");
        }
        
        sender.sendMessage(ChatColor.GREEN + "Total: " + products.size() + " products");
        return true;
    }
    
    private String getRarityColor(String rarity) {
        switch (rarity.toLowerCase()) {
            case "common":
                return ChatColor.WHITE.toString();
            case "uncommon":
                return ChatColor.GREEN.toString();
            case "rare":
                return ChatColor.BLUE.toString();
            case "epic":
                return ChatColor.DARK_PURPLE.toString();
            case "legendary":
                return ChatColor.GOLD.toString();
            default:
                return ChatColor.GRAY.toString();
        }
    }
}