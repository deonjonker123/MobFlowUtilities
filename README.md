![MFU](https://raw.githubusercontent.com/deonjonker123/MobFlowUtilities/refs/heads/26.1.2/banner2.png)
## Automate Mobs

A mob farming automation mod. Flow pads and fans push mobs where you want them, attack pads kill them, and collectors pick up whatever they drop.

## Features

### Flow Pads

*   **Directional Movement**: Push mobs and items in the direction you place them
*   **Three Speed Tiers**: Fast, Faster, and Fastest for different flow rates
*   **No Configuration**: Direction set by placement orientation

### Damage Pads + Controller

*   **Centralized Damage Control**: One Controller manages multiple Damage Pads in multiple farms
*   **Pad Wrench Linking**: Connect pads to controllers with visual feedback
*   **Stackable Enchantment Modules**: Up to 10 modules per slot
    *   **Sharpness**: Increased damage output
    *   **Fire Aspect**: Burning damage over time
    *   **Smite**: Specialized undead damage
    *   **Bane of Arthropods**: Arthropod-specific damage
    *   **Looting**: Enhanced drop rates
*   **Redstone Activation**: Damage Pads require a redstone signal to operate
*   **Enderman Teleport Blocking**: Damage Pads prevent Endermen from escaping

### Collector Block

*   **Configurable Collection Zone**: Adjust pickup radius with Radius Increase Modules
*   **Zone Positioning**: Offset collection area in any direction
*   **XP Storage**: Collect and store experience as Liquid XP — deposit and withdraw one level at a time, or all at once (26.1.2 Exclusive)
*   **Liquid XP Output**: Expose stored XP as a fluid via pipes or direct bucket interaction (26.1.2 Exclusive)
*   **Void Filtering**: Automatically delete unwanted items with up to 3 configurable filter modules
*   **Visual Zone Preview**: Wireframe overlay shows collection boundaries

### Genesis Chamber

*   **Automated Mob Spawning**: Spawn mobs from spawn eggs using fuel
*   **Fuel System**: Uses vanilla furnace fuels (coal, lava buckets, etc.)
*   **Configurable Spawn Zone**: 5x5 base area, expandable to 10x10 with Radius Increase Modules
*   **Speed Control**: Base 200 tick spawn interval, reducible to 50 ticks with Speed Modules
*   **Light Level Aware**: Respects vanilla spawning rules (hostiles in darkness, passives in light)
*   **Spawn Cap**: Configurable mob limit per zone (default 12)
*   **3-Axis Positioning**: Offset spawn zone in any direction with wireframe preview
*   **Automation Ready**: Accepts fuel from hoppers/pipes automatically

### Fan

*   **Pushes mobs in the facing direction.**
*   **Area of effect can be upgraded using fan upgrade module**

### Pad Wrench

*   **Operation Modes**: Add or Remove mode for linking/unlinking pads
*   **Selection Types**: Single pad or area selection for bulk operations
*   **Visual Feedback**: Wireframe highlights linked systems

### Mob Spawn Control

*   **Gloom Spores**: Craftable item that transforms dirt into Dark Dirt
    *   Converts 5x5 area
    *   Plays sound and block break particles on conversion
    *   Also obtainable from hostile mob loot chests
*   **Dark Dirt**: Accelerated hostile mob spawning blocks
    *   Spawns up to 12 hostile mobs per area
    *   Functions in darkness, ignores player proximity
    *   Reverts to dirt in direct sunlight
    *   **Redstone Pulse**: Receiving a redstone signal drastically accelerates spawn tick rate for exponential spawn bursts (26.1.2 Exclusive)
*   **Glimmer Sprouts**: Craftable item that transforms dirt into Glimmer Grass
    *   Converts 5x5 area
    *   Also obtainable from passive mob loot chests
*   **Glimmer Grass**: Accelerated passive mob spawning blocks
    *   Spawns up to 12 passive mobs per area
    *   Functions in bright light, ignores player proximity

### Gene Sampling

*   **Empty Gene Vial**: Used to collect genetic material from mobs
*   **Gene Sample Vial**: Contains collected gene samples that can be used for crafting spawn eggs
*   **Life Catalust**: Used for crafting spawn eggs

Gene sampling allows players to collect gene samples from creatures using Empty Gene Vials, then combine the resulting Gene Sample Vials with Incubation Crystals and Emeralds to craft spawn eggs for use in the Genesis Chamber.

### Utility Tools

*   **Mob Catcher**: Capture and release any non-boss mob

### Decorative Blocks

*   **Dark Glass**: Blocks light transmission while maintaining transparency and is wither immune
*   **Glimmer Lamp**: High-output decorative lighting

### Configuration

*   **Flexible Settings**: Adjust spawn rates, mob caps, particle effects, and more
*   **Genesis Chamber Spawn Cap**: Configurable per-zone mob limit
*   **Dark Dirt/Glimmer Grass**: Adjust spawn rates and check intervals
*   **Conversion Areas**: Configure Gloom Spore and Glimmer Sprout conversion radius