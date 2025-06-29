# TeleportPlugin Installation Guide

## Prerequisites
- Paper/Spigot Minecraft Server (1.21+)
- Java 17+
- Maven (for building)

## Building the Plugin

### Method 1: Using Maven
```bash
cd /path/to/mcplugin-teleport
mvn clean package
```

### Method 2: Using IDE
1. Open IntelliJ IDEA/Eclipse
2. File > Open > Select the mcplugin-teleport folder
3. Import as Maven project
4. Run Maven goals: clean package
5. JAR file will be in target/ folder

## Installation Steps

1. **Build the plugin** (see above methods)
2. **Locate the JAR file**: `target/teleportplugin-1.0-SNAPSHOT.jar`
3. **Copy to server**: Place the JAR in your server's `plugins/` folder
4. **Restart server**: Restart your Minecraft server
5. **Verify**: Check console for "TeleportPlugin enabled!"

## Server Directory Structure
```
minecraft-server/
├── plugins/
│   ├── teleportplugin-1.0-SNAPSHOT.jar  ← Place here
│   └── other-plugins.jar
├── server.jar
├── world/
└── ...
```

## Features Included
- /home, /sethome - Home teleportation
- /tp, /tpaccept - Player-to-player teleportation  
- /trade - Advanced item trading with anti-scam protection
- Death location tracking

## Commands
- `/home` - Teleport to your home
- `/sethome` - Set your home location
- `/tp <player>` - Request teleport to player
- `/tpaccept` - Accept teleport request
- `/trade <player>` - Start trade with player
- `/trade accept` - Accept trade request
- `/trade cancel` - Cancel trade

## Permissions
No permissions required - all players can use all commands.

## Support
- Minecraft Version: 1.21+
- Server Type: Paper/Spigot
- Java Version: 17+
