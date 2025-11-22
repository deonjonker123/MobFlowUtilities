# Mob Flow Utilities (MFU)

_Modular flow control for efficient mob farming_

----------

## Automate Mobs

Mob Flow Utilities is a modular automation mod focused on mob farm efficiency through directional flow control, centralized damage systems, intelligent item collection, and automated mob spawning. Clean design, minimal GUIs, and visual feedback.

## Features

### Flow Pads

-   **Directional Movement**: Push mobs and items in the direction you place them
-   **Three Speed Tiers**: Fast, Faster, and Fastest for different flow rates
-   **Waterloggable**: Works seamlessly with water-based farm designs
-   **No Configuration**: Direction set by placement orientation

### Damage Pads + Controller

-   **Centralized Damage Control**: One Controller manages multiple Damage Pads
-   **Pad Wrench Linking**: Connect pads to controllers with visual feedback
-   **Stackable Enchantment Modules**: Up to 10 modules per slot
    -   **Sharpness**: Increased damage output
    -   **Fire Aspect**: Burning damage over time
    -   **Smite**: Specialized undead damage
    -   **Bane of Arthropods**: Arthropod-specific damage
    -   **Looting**: Enhanced drop rates
-   **Redstone Activation**: Damage Pads require a redstone signal to operate
-   **Enderman Teleport Blocking**: Damage Pads prevent Endermen from escaping

### Collector Block

-   **Configurable Collection Zone**: Adjust pickup radius with Radius Increase Modules
-   **Zone Positioning**: Offset collection area in any direction
-   **XP Storage**: Collect and store experience with level-based deposit/withdrawal
-   **Void Filtering**: Automatically delete unwanted items with up to 3 configurable filter modules
-   **Visual Zone Preview**: Wireframe overlay shows collection boundaries

### Genesis Chamber

-   **Automated Mob Spawning**: Spawn mobs from spawn eggs using fuel
-   **Fuel System**: Uses vanilla furnace fuels (coal, lava buckets, etc.)
-   **Configurable Spawn Zone**: 5x5 base area, expandable to 10x10 with Radius Increase Modules
-   **Speed Control**: Base 200 tick spawn interval, reducible to 50 ticks with Speed Modules
-   **Light Level Aware**: Respects vanilla spawning rules (hostiles in darkness, passives in light)
-   **Spawn Cap**: Configurable mob limit per zone (default 12)
-   **3-Axis Positioning**: Offset spawn zone in any direction with wireframe preview
-   **Automation Ready**: Accepts fuel from hoppers/pipes automatically

### Pad Wrench

-   **Operation Modes**: Add or Remove mode for linking/unlinking pads
-   **Selection Types**: Single pad or area selection for bulk operations
-   **Visual Feedback**: Wireframe highlights linked systems

### Mob Spawn Control

-   **Gloom Spores**: Rare drops from hostile mobs that transform dirt into Dark Dirt
    -   Converts 5x5 area
    -   Only spawns mobs in low light conditions
-   **Dark Dirt**: Accelerated hostile mob spawning blocks
    -   Spawns up to 12 hostile mobs per area
    -   Functions in darkness, ignores player proximity
    -   Reverts to dirt in direct sunlight
-   **Glimmer Sprouts**: Rare drops from passive mobs that transform dirt into Glimmer Grass
    -   Converts 5x5 area
-   **Glimmer Grass**: Accelerated passive mob spawning blocks
    -   Spawns up to 12 passive mobs per area
    -   Functions in bright light, ignores player proximity

### Gene Sampling

-   **Empty Gene Vial**: Used to collect genetic material from mobs
-   **Gene Sample Vial**: Contains collected gene samples that can be used for crafting spawn eggs
-   **Incubation Crystal**: Boss mob drop only, used for crafting spawn eggs

Gene sampling allows players to collect gene samples from creatures using Empty Gene Vials, then combine the resulting Gene Sample Vials with Incubation Crystals and Emeralds to craft spawn eggs for use in the Genesis Chamber.

### Utility Tools

-   **Mob Catcher**: Capture and release any non-boss mob

### Custom Wood Types

-   **Gloomwood**: Dark-themed wood and **Glimmerwood**: Light-themed
    -   Full set of wood variants: logs, stripped logs, planks, stairs, slabs, fences, doors, trapdoors, buttons, pressure plates, barrels, and berries

### Decorative Blocks

-   **Dark Glass**: Blocks light transmission while maintaining transparency
-   **Glimmer Lamp**: High-output decorative lighting

### Gloomsteel & Glimmersteel Materials

-   **Multi-Dimensional Ore**: Spawns in Overworld, Nether, and End
-   **Full Tool Suites**
-   **Special Tools**: Paxel and Hammer (3x3 mining)
-   **Complete Armor Sets**
-   **Diamond-tier stats**

### Configuration

-   **Flexible Settings**: Adjust spawn rates, mob caps, particle effects, and more
-   **Genesis Chamber Spawn Cap**: Configurable per-zone mob limit
-   **Dark Dirt/Glimmer Grass**: Adjust spawn rates and check intervals
-   **Conversion Areas**: Configure Gloom Spore and Glimmer Sprout conversion radius

### Credit

Some textures used in this mod were adapted from  **"Unused Minecraft Textures" by Malcolm Riley**  ([GitHub Repository](https://github.com/malcolmriley/unused-textures))  
Licensed under the  [Creative Commons Attribution 4.0 International License](https://creativecommons.org/licenses/by/4.0/).

Modifications were made to some of the original assets.

## License

All rights reserved. This mod is protected by copyright and may not be redistributed or modified without explicit permission.

**Permitted Uses:**

-   Inclusion in modpacks (public or private)
-   Content creation (videos, streams, reviews, etc.)