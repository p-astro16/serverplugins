package com.example.teleportplugin;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HomeCommand implements CommandExecutor {
    private final TeleportPlugin plugin;
    public HomeCommand(TeleportPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        Location home = plugin.getHome(uuid);
        if (home == null) {
            player.sendMessage("Home is not set.");
            return true;
        }
        player.teleport(home);
        player.sendMessage("Teleported to home!");
        return true;
    }
}
