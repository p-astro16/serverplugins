# TeleportPlugin

![Build Status](https://github.com/p-astro16/serverplugins/workflows/Build%20Plugin/badge.svg)

Een geavanceerde Minecraft plugin voor Spigot servers met teleportatie en trading functionaliteiten.

## ✨ Features

### 🏠 Home Systeem
- `/home` - Teleporteer naar je home locatie
- `/sethome` - Stel je home locatie in

### 🔄 Player-to-Player Teleportatie
- `/tp <player>` - Vraag om te teleporteren naar een andere speler
- `/tpaccept` - Accepteer een teleport verzoek

### 🛒 Geavanceerd Trading Systeem
- `/trade <player>` - Start een item trade met een andere speler
- `/trade accept` - Accepteer een trade verzoek
- `/trade cancel` - Annuleer een trade
- **Anti-scam beveiliging** met dubbele confirmatie
- **Professional UI** zoals Skyblock servers
- **Item verificatie** om scamming te voorkomen

### 💀 Death Location Tracking
- Automatische death locatie melding in chat wanneer je sterft

## 🛡️ Anti-Scam Features

- **Dubbele confirmatie** systeem (Ready → Confirm)
- **2 seconden cooldown** na item wijzigingen
- **Item verificatie** tijdens trade completion
- **Trade request expiry** (30 seconden)
- **Blacklisted items** (bedrock, command blocks, etc.)
- **Automatic trade cancellation** bij disconnect
- **Overflow protection** voor volle inventories

## 📋 Vereisten

- **Minecraft**: 1.21+
- **Server**: Spigot (Paper ook compatible)
- **Java**: 17+

## 🚀 Installatie

### Automatische Build (GitHub Actions)
1. Download de nieuwste JAR van [Releases](https://github.com/p-astro16/serverplugins/releases)
2. Plaats in je server's `plugins/` folder
3. Herstart je server

### Handmatige Build
```bash
git clone https://github.com/p-astro16/serverplugins.git
cd serverplugins
mvn clean package
```

## 🎮 Commands

| Command | Beschrijving | Gebruik |
|---------|--------------|---------|
| `/home` | Teleporteer naar je home | `/home` |
| `/sethome` | Stel je home in | `/sethome` |
| `/tp <player>` | Vraag teleport naar speler | `/tp Steve` |
| `/tpaccept` | Accepteer teleport verzoek | `/tpaccept` |
| `/trade <player>` | Start trade met speler | `/trade Alex` |
| `/trade accept` | Accepteer trade verzoek | `/trade accept` |
| `/trade cancel` | Annuleer trade | `/trade cancel` |

## 🔧 Permissions

Geen permissions nodig - alle spelers kunnen alle commands gebruiken.

## 📱 Trade UI Layout

```
┌─────────────────────────────────────────────────────┐
│ [Player1 Items]  │ [DIVIDER] │  [Player2 Items]    │
│ ┌───┬───┬───┬───┐ │ ░░░░░░░░░ │ ┌───┬───┬───┬───┐    │
│ │   │   │   │   │ │ ░░░░░░░░░ │ │   │   │   │   │    │
│ ├───┼───┼───┼───┤ │ ░░░░░░░░░ │ ├───┼───┼───┼───┤    │
│ │   │   │   │   │ │ ░░░░░░░░░ │ │   │   │   │   │    │
│ ├───┼───┼───┼───┤ │ ░░░░░░░░░ │ ├───┼───┼───┼───┤    │
│ │   │   │   │   │ │ ░░░░░░░░░ │ │   │   │   │   │    │
│ ├───┼───┼───┼───┤ │ ░░░░░░░░░ │ ├───┼───┼───┼───┤    │
│ │   │   │   │   │ │ ░░░░░░░░░ │ │   │   │   │   │    │
│ └───┴───┴───┴───┘ │ ░░░░░░░░░ │ └───┴───┴───┴───┘    │
│                    │           │                      │
│ [P1 READY BUTTON]  │ [CANCEL]  │ [P2 READY BUTTON]   │
└─────────────────────────────────────────────────────┘
```

## 🔄 Trade Process

1. **Request**: `/trade <player>`
2. **Accept**: Target player: `/trade accept`
3. **Setup**: Beide spelers plaatsen items
4. **Ready**: Klik op ready button (wordt goud)
5. **Confirm**: Klik opnieuw om te bevestigen (wordt emerald)
6. **Complete**: Trade voltooid wanneer beide confirmed

## 🛠️ Development

### Project Structure
```
src/
├── main/
│   ├── java/com/example/teleportplugin/
│   │   ├── TeleportPlugin.java      # Main plugin class
│   │   ├── TradeManager.java        # Trade system
│   │   ├── TradeCommand.java        # Trade commands
│   │   ├── TradeListener.java       # Trade events
│   │   ├── HomeCommand.java         # Home system
│   │   ├── SetHomeCommand.java      # Set home
│   │   ├── TpCommand.java          # Teleport requests
│   │   └── TpAcceptCommand.java    # Accept teleports
│   └── resources/
│       └── plugin.yml              # Plugin configuration
├── .github/workflows/
│   └── build.yml                   # GitHub Actions
└── pom.xml                         # Maven configuration
```

### Building
```bash
mvn clean package
```

JAR wordt gegenereerd in `target/teleportplugin-1.0-SNAPSHOT.jar`

## 📄 License

Dit project is gelicenseerd onder de MIT License - zie het [LICENSE](LICENSE) bestand voor details.

## 🤝 Contributing

1. Fork het project
2. Maak je feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit je changes (`git commit -m 'Add some AmazingFeature'`)
4. Push naar de branch (`git push origin feature/AmazingFeature`)
5. Open een Pull Request

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/p-astro16/serverplugins/issues)
- **Discussions**: [GitHub Discussions](https://github.com/p-astro16/serverplugins/discussions)
- **Author**: p-astro16

## 🎯 Roadmap

- [ ] Economy integration
- [ ] Multiple homes support
- [ ] Trade history logging
- [ ] Permissions support
- [ ] Config file voor customization
- [ ] Database support voor persistent data
