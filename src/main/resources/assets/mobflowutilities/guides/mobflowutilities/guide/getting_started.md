---
navigation:
  title: Getting Started
  icon: mobflowutilities:gloom_spore
  parent: index.md
  position: 1
item_ids:
  - mobflowutilities:gloom_spore
  - mobflowutilities:glimmer_sprout
  - mobflowutilities:void_filter_module
  - mobflowutilities:dark_dirt
  - mobflowutilities:glimmer_grass
---

# Getting Started

The core loop in Mob Flow Utilities is simple: **spawn** mobs on special blocks, **push** them with Flow Pads, **kill** them on Damage Pads, and **collect** the drops. Everything builds on that flow — and you can start it long before any machines.

## 1. Get a Gloom Spore or Glimmer Sprout

<RecipeFor id="gloom_spore" />

<RecipeFor id="glimmer_sprout" />

Both can be crafted, or found as loot in chests. Gloom Spores convert blocks to hostile-spawning blocks; Glimmer Sprouts converts blocks to
passive-spawning blocks.

## 2. Convert the Ground

<Row>
<ItemImage id="mobflowutilities:dark_dirt" />
<ItemImage id="mobflowutilities:glimmer_grass" />
</Row>

Right-click dirt, grass, or similar with a spore or sprout to convert a **5x5 area** into **Dark Dirt** (hostiles) or **Glimmer Grass** (passives).

**Dark Dirt** only spawns hostile mobs in **complete darkness (light level 0)**. Any direct sunlight reverts it to plain dirt. Pulsing it with a **redstone signal** accelerates spawning — the faster the pulse, the faster mobs appear.

**Glimmer Grass** only spawns passive mobs at **light level 9 or higher**. It never reverts and never accelerates — to stop it, simply put it in darkness.

## 3. Lay Down [Flow Pads](mob_flow.md)

<RecipeFor id="flow_pad_fast" />

Flow Pads push mobs in the direction they face, shown by the large arrow on top. Run them toward a central point — usually wherever you place your Damage Pads. Three speed tiers are available for faster flow.

## 4. Set Up [Damage Pads](mob_flow.md)

<RecipeFor id="damage_pad" />

Damage Pads kill mobs that pass over them and require a **redstone signal** to operate. They only harm mobs, never players.

## 5. Link a [Controller (Optional)](mob_flow.md)

<RecipeFor id="controller" />

Link Damage Pads to a Controller with the **Pad Wrench** to manage all their enchantment upgrades from one place, anywhere in range. Available enchantments: **Sharpness**, **Smite**, **Fire Aspect**, **Bane of Arthropods**, and **Looting** — each stackable up to 10.

## 6. Place a Collector

<RecipeFor id="collector" />

The Collector picks up dropped items, and experience too if you enable the **XP collection toggle** in its GUI. Its range is configurable there and can be extended with **Radius Increase Modules**. Three filter slots accept **Void Filter** items — anything filtered is voided instantly, with 45 filter slots each.

<RecipeFor id="void_filter_module" />

## 7. Advance to the [Genesis Chamber](genesis_chamber.md)

<RecipeFor id="genesis_chamber" />

Once you've gathered diamonds, iron, and emeralds, the Genesis Chamber lets you spawn *exactly* the mob you want. It runs on **spawn eggs** and **fuel** (most furnace fuels work).

To craft a spawn egg you'll need a **[Life Catalyst](gene_sampling.md)**, an **emerald**, and a **DNA Sample**:

*   **Gene Sample** — craft an **Empty Gene Vial**, then **Shift + Right-click** any mob to sample its DNA.
*   **Life Catalyst** — crafted from **4 Liquid XP buckets**, **4 Eyes of Ender**, and **1 Ender Pearl**.