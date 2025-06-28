package com.example.teleportplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TpAcceptCommand implements CommandExecutor {
    private final TeleportPlugin plugin;
    public TpAcceptCommand(TeleportPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        Player target = (Player) sender;
        TeleportPlugin.TeleportRequest request = plugin.getTeleportRequest(target.getUniqueId());
        if (request == null) {
            target.sendMessage("No teleport request to accept.");
            return true;
        }
        Player from = Bukkit.getPlayer(request.from);
        if (from == null || !from.isOnline()) {
            target.sendMessage("Requesting player is not online.");
            plugin.removeTeleportRequest(target.getUniqueId());
            return true;
        }
        from.teleport(target.getLocation());
        from.sendMessage("Teleported to " + target.getName() + "!");
        target.sendMessage("Teleport request accepted.");
        plugin.removeTeleportRequest(target.getUniqueId());
        return true;
    }
}
