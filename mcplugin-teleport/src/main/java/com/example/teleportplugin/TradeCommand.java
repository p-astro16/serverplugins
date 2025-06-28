package com.example.teleportplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TradeCommand implements CommandExecutor {
    private final TradeManager tradeManager;

    public TradeCommand(TradeManager tradeManager) {
        this.tradeManager = tradeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /trade <player> | /trade accept | /trade cancel");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "accept":
                return handleTradeAccept(player);
            case "cancel":
                return handleTradeCancel(player);
            default:
                return handleTradeRequest(player, args[0]);
        }
    }

    private boolean handleTradeRequest(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null || !target.isOnline()) {
            player.sendMessage(ChatColor.RED + "Player not found or not online.");
            return true;
        }

        if (player.getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You cannot trade with yourself.");
            return true;
        }

        // Check if either player is already in a trade
        if (tradeManager.getTradeSession(player.getUniqueId()) != null) {
            player.sendMessage(ChatColor.RED + "You are already in a trade.");
            return true;
        }

        if (tradeManager.getTradeSession(target.getUniqueId()) != null) {
            player.sendMessage(ChatColor.RED + target.getName() + " is already in a trade.");
            return true;
        }

        tradeManager.sendTradeRequest(player.getUniqueId(), target.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Trade request sent to " + target.getName() + ".");
        target.sendMessage(ChatColor.YELLOW + player.getName() + " wants to trade with you!");
        target.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.WHITE + "/trade accept" + ChatColor.YELLOW + " to accept or " + ChatColor.WHITE + "/trade cancel" + ChatColor.YELLOW + " to decline.");
        return true;
    }

    private boolean handleTradeAccept(Player player) {
        UUID requesterUUID = tradeManager.getTradeRequest(player.getUniqueId());
        if (requesterUUID == null) {
            player.sendMessage(ChatColor.RED + "You have no pending trade requests.");
            return true;
        }

        Player requester = Bukkit.getPlayer(requesterUUID);
        if (requester == null || !requester.isOnline()) {
            player.sendMessage(ChatColor.RED + "The player who sent the trade request is no longer online.");
            tradeManager.removeTradeRequest(player.getUniqueId());
            return true;
        }

        // Check if either player is already in a trade
        if (tradeManager.getTradeSession(player.getUniqueId()) != null) {
            player.sendMessage(ChatColor.RED + "You are already in a trade.");
            return true;
        }

        if (tradeManager.getTradeSession(requester.getUniqueId()) != null) {
            player.sendMessage(ChatColor.RED + requester.getName() + " is already in a trade.");
            return true;
        }

        tradeManager.startTrade(requesterUUID, player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Trade started with " + requester.getName() + "!");
        player.sendMessage(ChatColor.YELLOW + "Place items on your side (left), then click your ready button when ready.");
        requester.sendMessage(ChatColor.GREEN + "Trade started with " + player.getName() + "!");
        requester.sendMessage(ChatColor.YELLOW + "Place items on your side (left), then click your ready button when ready.");
        return true;
    }

    private boolean handleTradeCancel(Player player) {
        TradeManager.TradeSession session = tradeManager.getTradeSession(player.getUniqueId());
        if (session != null) {
            tradeManager.cancelTrade(player.getUniqueId());
        } else {
            tradeManager.removeTradeRequest(player.getUniqueId());
            player.sendMessage(ChatColor.YELLOW + "Trade request cancelled.");
        }
        return true;
    }
}
