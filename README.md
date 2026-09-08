![MFU](https://raw.githubusercontent.com/deonjonker123/MobFlowUtilities/refs/heads/26.1.2/banner2.png)

## Automate Mobs

A mob farming automation mod.

### Flow Pads

*   Push mobs and items in the direction you place them

### Damage Pads + Controller

*   One Controller manages multiple Damage Pads in multiple farms
*   Connect pads to a controllers with the pad wrench
*   5 damage enchantment slots, up to 10 modules per slot
    *  Sharpness
    *   Fire Aspect
    *   Smite
    *   Bane of Arthropods
    *   Looting
*   Requires a redstone signal to work
*   Prevents Endermen from teleporting

### Collector Block

*   Configurable collection area and size. Can be be increased with modules
*   Can collect and store XP as a fluid.

### Genesis Chamber

*   Spawn mobs from spawn eggs using fuel
*   Uses most vanilla furnace fuels (coal, lava buckets, blaze rods, etc.)
*   5x5 spawn area, expandable to 10x10 with modules
*   Base 200 tick spawn interval, down to 50 ticks with Speed Modules
*   Respects vanilla spawning rules (hostiles in darkness, passives in light)
*   Configurable mob limit per zone (default 12)

### Genesis Infuser

*   Infuses coal and charcoal with liquid XP to make Infused Coal and Infused Charcoal, each burning 8x longer than its vanilla counterpart
*   Takes 125mB of liquid XP per item — a full bucket makes 8 infused items

### Fan

Pushes mobs in the facing direction.
*   Area of effect can be upgraded using fan upgrade module

### Pad Wrench

*   **Operation Modes**: Add or Remove mode
*   **Selection Types**: Single pad or area selection

### Mob Spawning

*   **Gloom Spores**: Craftable item that transforms dirt into Dark Dirt
    *   Converts 5x5 area
    *   Can also be found in not-so-nice loot chests (fortress, bastion, stronghold, etc)
*   **Dark Dirt**: Hostile mob spawning blocks
    *   Spawns up to 12 hostile mobs per area (can be changed in config)
    *   Functions in darkness, ignores player proximity
    *   Reverts to dirt in direct sunlight
    *   Giving the dark dirt a redstone signal accelerates spawn tick rate for faster spawn bursts
*   **Glimmer Sprouts**: Craftable item that transforms dirt into Glimmer Grass
    *   Converts 5x5 area
    *   Can also be found in friendly loot chests (villages, shipwrecks, etc)
*   **Glimmer Grass**: Passive mob spawning blocks
    *   Spawns up to 12 passive mobs per area
    *   Functions in bright light, ignores player proximity

### Gene Sampling

*   **Empty Gene Vial**: Used to collect DNA from mobs
*   **Gene Sample Vial**: Contains collected DNA that can be used for crafting spawn eggs
*   **Life Catalyst**: Used for crafting spawn eggs

Using a Gene Sample Vial with some DNA, a life Catalyst and an emerald, you can craft the sampled DNA into a spawn egg. Doesn't work on bosses (except the Warden, cause why not)

### Infused Coal and Charcoal

*   Burns 8x longer than vanilla coal and charcoal. One Infused Coal/Charcoal can smelt a stack (64) of items.

### Utility Tools

*   **Mob Catcher**: Capture and release any non-boss mob

### Decorative Blocks

*   **Dark Glass**: Blocks light and is wither immune
*   **Glimmer Lamp**: Fancier redstone lamp