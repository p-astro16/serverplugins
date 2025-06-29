# 🪦 Gravestone Model Setup

## Current Model Implementation

The plugin includes a custom gravestone model created with Blockbench in GLTF format.

## Model Files

### Location
```
src/main/resources/models/
├── source/
│   └── model.gltf          # Main 3D model file
└── textures/
    ├── gltf_embedded_0.png # Texture 1 (campfire log style)
    ├── gltf_embedded_1.png # Texture 2
    └── gltf_embedded_2.png # Texture 3
```

### Model Details
- **Format**: GLTF 2.0 (Blockbench export)
- **Components**: Multi-mesh gravestone with detailed textures
- **Size**: Optimized for Minecraft scale
- **Materials**: 3 different texture materials with alpha masking

## Gravestone Features

### Automatic Death System
1. **Death Detection**: Triggers when a player dies
2. **Item Storage**: All dropped items stored in chest
3. **Gravestone Creation**: Multi-block structure placed near chest:
   - Cobblestone base
   - Stone slab marker  
   - Player head memorial
   - Decorative flowers around the grave

### Gravestone Structure
```
    [HEAD]     <- Player head
    [SLAB]     <- Stone slab
    [BASE]     <- Cobblestone base
[F] [CHEST] [F] <- Chest with decorative flowers
```

## Enhanced Death Experience

### Chat Announcements
- Death coordinates broadcast to all players
- Private message to dead player with chest location

### Visual Elements
- **Base**: Cobblestone foundation
- **Marker**: Stone slab gravestone
- **Memorial**: Player head (future: custom texture)
- **Decoration**: Random flowers (poppies, dandelions, etc.)
- **Storage**: Chest containing all dropped items

## Customization Options

### Model Replacement
To use the included GLTF model for custom block displays:

1. The model is already included in the plugin resources
2. Future updates may include custom block display integration
3. Current implementation uses traditional Minecraft blocks for compatibility

### Texture Customization
The included textures can be modified:
- `gltf_embedded_0.png` - Primary texture (wood-like)
- `gltf_embedded_1.png` - Secondary details
- `gltf_embedded_2.png` - Additional elements

### Configuration
The gravestone appearance can be customized by modifying:
- `createGravestone()` method for structure
- `addGraveDecoration()` method for flowers
- Material choices in the methods

## Technical Implementation

### Current System
- Uses traditional Minecraft blocks for maximum compatibility
- Supports all server versions without custom resource packs
- Automatic placement with collision detection

### Future Enhancements
- Custom block display integration with GLTF model
- Player-specific head textures on gravestones  
- Configurable gravestone styles
- Custom model positioning and scaling

## Installation Notes

- Model files are automatically included in the plugin JAR
- No additional resource packs required for basic functionality
- Works on any Spigot/Paper 1.21 server
- Future custom model features may require client-side resource packs

---

The gravestone system provides a meaningful way to mark death locations while ensuring item recovery is both functional and atmospheric!
