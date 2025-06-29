package com.example.teleportplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TpCommand implements CommandExecutor {
    private final TeleportPlugin plugin;
    public TpCommand(TeleportPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage("Usage: /tp <player>");
            return true;
        }
        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage("Player not found.");
            return true;
        }
        if (player.getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage("You cannot teleport to yourself.");
            return true;
        }
        plugin.addTeleportRequest(player.getUniqueId(), target.getUniqueId());
        player.sendMessage("Teleport request sent to " + target.getName() + ".");
        target.sendMessage(player.getName() + " wants to teleport to you. Type /tpaccept to allow.");
        return true;
    }
}
