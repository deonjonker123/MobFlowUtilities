---
navigation:
  title: Mob Flow and Demise
  icon: mobflowutilities:flow_pad_fastest
  parent: index.md
  position: 2
item_ids:
  - mobflowutilities:flow_pad_fast
  - mobflowutilities:flow_pad_faster
  - mobflowutilities:flow_pad_fastest
  - mobflowutilities:damage_pad
  - mobflowutilities:fan
  - mobflowutilities:controller
  - mobflowutilities:pad_wrench
  - mobflowutilities:fan_width_increase_module
  - mobflowutilities:fan_height_increase_module
  - mobflowutilities:fan_distance_increase_module
  - mobflowutilities:bane_of_arthropods_module
  - mobflowutilities:fire_aspect_module
  - mobflowutilities:looting_module
  - mobflowutilities:sharpness_module
  - mobflowutilities:smite_module
---

# Mob Flow and Demise

Spawning mobs is only half the job — you need to move them where you want them and kill them efficiently. These are the tools that push, herd, and destroy.

## Flow Pads
<Row>
<RecipeFor id="flow_pad_fast" />
<RecipeFor id="flow_pad_faster" />
<RecipeFor id="flow_pad_fastest" />
</Row>

Flow Pads push any mob (or item) standing on them in the direction they face, shown by the large arrow on top. Place them in a line to carry mobs from the spawn area to your kill zone.

There are three tiers, each pushing twice as hard as the last:

*   **Fast Flow Pad** — gentle nudge, good for short runs
*   **Faster Flow Pad** — double the push of Fast
*   **Fastest Flow Pad** — double again, for long lanes or stubborn crowds

A **sneaking player** is never pushed, so you can walk across your own pads safely.

## Fan

<RecipeFor id="fan" />

Where Flow Pads push mobs standing *on* them, the Fan pushes everything in a column *in front* of it — including vertically. It only runs while receiving a **redstone signal**.

Open the Fan's GUI to shape its push zone with modules:

*   **Distance** — base reach of 4 blocks, +1 per module, up to **15**
*   **Width** and **Height** — up to **4** blocks each, widening the column

<Row>
<RecipeFor id="fan_width_increase_module" />
<RecipeFor id="fan_height_increase_module" />
<RecipeFor id="fan_distance_increase_module" />
</Row>

The Fan won't push through walls — a solid block in the way blocks the airflow beyond it, so keep its lane clear.

## Damage Pads

<RecipeFor id="damage_pad" />

Damage Pads kill mobs that pass over them and require a **redstone signal** to operate. They only ever harm mobs — players are always safe. On their own, each pad deals a flat amount of damage on a short interval.

## Controller

<RecipeFor id="controller" />

A Controller supercharges your Damage Pads. Link pads to it (see the Pad Wrench below) and every linked pad strikes with an enchanted weapon instead of plain damage — sharing the modules you load into the Controller's GUI.

Insert enchantment modules to apply them across **all** linked pads at once:

*   **Sharpness** — more damage, up to 10
*   **Smite** — bonus damage to undead, up to 10
*   **Bane of Arthropods** — bonus damage to spiders and the like, up to 10
*   **Fire Aspect** — sets mobs ablaze, up to 10
*   **Looting** — more and rarer drops, up to 10

<Row>
<RecipeFor id="bane_of_arthropods_module" />
<RecipeFor id="fire_aspect_module" />
<RecipeFor id="looting_module" />
<RecipeFor id="sharpness_module" />
<RecipeFor id="smite_module" />
</Row>

One Controller can manage as many pads as you link to it.

## Pad Wrench

<RecipeFor id="pad_wrench" />

The Pad Wrench links Damage Pads to a Controller. It has two toggles:

*   **Right-click in the air** — switch between **Single** and **Multi** selection
*   **Shift + Right-click in the air** — switch between **Add** and **Remove** mode

To link pads:

1.  **Right-click a Controller** to select it.
2.  In **Single** mode, right-click each Damage Pad to add or remove it.
3.  In **Multi** mode, **Shift + Right-click** two pads to mark opposite corners of a box — every Damage Pad inside is linked (or unlinked) at once.

The wrench's current modes and selected Controller are shown in its tooltip and in the action bar as you work.