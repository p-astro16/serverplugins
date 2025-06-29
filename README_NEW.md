# TeleportPlugin

A comprehensive Spigot 1.21 plugin featuring player teleportation, trading, home system, and enhanced death mechanics.

## ✨ Features

### 🏠 Home System
- **`/sethome`** - Set your home location
- **`/home`** - Teleport to your home

### 🔄 Player Teleportation  
- **`/tp <player>`** - Request to teleport to another player
- **`/tpaccept`** - Accept a teleport request

### 💎 Advanced Trading System
- **`/trade <player>`** - Start a secure trade with another player
- **Anti-scam protection**: Double confirmation system
- **Visual trade interface**: Skyblock-style trading UI
- **Item verification**: Prevents trading of blacklisted items
- **Disconnect protection**: Trade cancels if a player leaves

### ⚰️ Enhanced Death System
- **Death location broadcasting**: Coordinates are announced in chat when a player dies
- **Death chests**: Items are automatically stored in a chest at death location
- **Gravestone marker**: Decorative skull placed near death chest
- **Coordinate display**: Players receive exact coordinates of their death chest

### 🌙 Sleep Enhancement
- **Single player sleep**: Only one player needs to sleep to skip the night
- **Weather reset**: Clears storms when skipping night

## 🎮 Commands

| Command | Description |
|---------|-------------|
| `/home` | Teleport to your home location |
| `/sethome` | Set your current location as home |
| `/tp <player>` | Request to teleport to a player |
| `/tpaccept` | Accept a pending teleport request |
| `/trade <player>` | Start a trade with another player |

## 🔧 Installation

1. Download the latest `TeleportPlugin-1.0.jar` from releases
2. Place it in your server's `plugins/` folder
3. Restart your server
4. Enjoy the enhanced gameplay experience!

## 📋 Requirements

- **Minecraft Version**: 1.21
- **Server Software**: Spigot or Paper
- **Java Version**: 17 or higher

## 🛠️ Building from Source

1. Clone this repository
2. Make sure you have Maven and Java 17+ installed
3. Run: `mvn clean package`
4. Find the JAR in the `target/` folder

## 📝 Notes

- All player data is stored in memory (resets on server restart)
- Trading system includes comprehensive anti-scam measures
- Death chests are automatically created at death locations
- Single player can skip night for the entire server

---

*Made for enhanced Minecraft server gameplay*
