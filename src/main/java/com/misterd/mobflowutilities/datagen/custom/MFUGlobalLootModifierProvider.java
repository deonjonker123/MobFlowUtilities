package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.item.MFUItems;
import com.misterd.mobflowutilities.loot.AddItemModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class MFUGlobalLootModifierProvider extends GlobalLootModifierProvider {

    private static final String MOD_ID = "mobflowutilities";

    public MFUGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MOD_ID);
    }

    @Override
    protected void start() {
        // Hostile mobs that drop Gloom Spores (0.5% chance)
        String[] hostileMobs = {
                "entities/wither_skeleton", "entities/skeleton", "entities/zombie", "entities/creeper",
                "entities/spider", "entities/cave_spider", "entities/enderman", "entities/stray",
                "entities/husk", "entities/drowned", "entities/blaze", "entities/magma_cube",
                "entities/ghast", "entities/phantom", "entities/pillager", "entities/vindicator",
                "entities/evoker", "entities/witch", "entities/bat", "entities/breeze", "entities/bogged"
        };

        for (String mob : hostileMobs) {
            addMobLoot("gloom_spore_from_" + mob.replace("entities/", ""),
                    mob, MFUItems.GLOOM_SPORE, 0.005F);
        }

        // Hostile chests that contain Gloom Spores (30% chance)
        String[] hostileChests = {
                "chests/bastion_bridge", "chests/bastion_treasure", "chests/bastion_other",
                "chests/end_city_treasure", "chests/stronghold_corridor", "chests/stronghold_crossing",
                "chests/stronghold_library", "chests/pillager_outpost", "chests/nether_bridge",
                "chests/woodland_mansion"
        };

        for (String chest : hostileChests) {
            addChestLoot("gloom_spore_from_" + chest.replace("chests/", ""),
                    chest, MFUItems.GLOOM_SPORE, 0.3F);
        }

        // Passive mobs that drop Glimmer Sprouts (0.5% chance)
        String[] passiveMobs = {
                "entities/rabbit", "entities/fox", "entities/horse", "entities/donkey",
                "entities/cow", "entities/sheep", "entities/chicken", "entities/pig",
                "entities/llama", "entities/trader_llama", "entities/cat", "entities/wolf",
                "entities/mooshroom", "entities/parrot", "entities/villager", "entities/wandering_trader",
                "entities/goat", "entities/strider", "entities/iron_golem"
        };

        for (String mob : passiveMobs) {
            addMobLoot("glimmer_sprout_from_" + mob.replace("entities/", ""),
                    mob, MFUItems.GLIMMER_SPROUT, 0.05F);
        }

        // Friendly chests that contain Glimmer Sprouts (30% chance)
        String[] friendlyChests = {
                "chests/village/village_plains_house", "chests/village/village_desert_house",
                "chests/village/village_savanna_house", "chests/village/village_snowy_house",
                "chests/village/village_taiga_house", "chests/village/village_armorer",
                "chests/village/village_cartographer", "chests/village/village_mason",
                "chests/village/village_toolsmith", "chests/village/village_weaponsmith",
                "chests/shipwreck_treasure", "chests/shipwreck_supply", "chests/buried_treasure",
                "chests/ruined_portal", "chests/jungle_temple", "chests/desert_pyramid"
        };

        for (String chest : friendlyChests) {
            addChestLoot("glimmer_sprout_from_" + chest.replace("chests/", ""),
                    chest, MFUItems.GLIMMER_SPROUT, 0.3F);
        }

        // All common mobs drop Incubation Crystals (small% chance)
        String[] allMobs = {
                "entities/wither_skeleton", "entities/skeleton", "entities/zombie", "entities/creeper",
                "entities/spider", "entities/cave_spider", "entities/enderman", "entities/stray",
                "entities/husk", "entities/drowned", "entities/blaze", "entities/magma_cube",
                "entities/ghast", "entities/phantom", "entities/pillager", "entities/vindicator",
                "entities/evoker", "entities/witch", "entities/bat", "entities/breeze", "entities/bogged",
                "entities/rabbit", "entities/fox", "entities/horse", "entities/donkey",
                "entities/cow", "entities/sheep", "entities/chicken", "entities/pig",
                "entities/llama", "entities/trader_llama", "entities/cat", "entities/wolf",
                "entities/mooshroom", "entities/parrot", "entities/villager", "entities/wandering_trader",
                "entities/goat", "entities/strider", "entities/iron_golem"
        };

        for (String mob : allMobs) {
            addMobLoot("incubation_crystal_from_" + mob.replace("entities/", ""),
                    mob, MFUItems.INCUBATION_CRYSTAL, 0.0001F);
        }

        // Boss mobs - guaranteed Glimmer Sprouts
        addBossLoot("glimmer_sprout_from_ender_dragon", "entities/ender_dragon", MFUItems.GLIMMER_SPROUT);
        addBossLoot("glimmer_sprout_from_elder_guardian", "entities/elder_guardian", MFUItems.GLIMMER_SPROUT);

        // Boss mobs - guaranteed Gloom Spores
        addBossLoot("gloom_spore_from_warden", "entities/warden", MFUItems.GLOOM_SPORE);
        addBossLoot("gloom_spore_from_wither", "entities/wither", MFUItems.GLOOM_SPORE);
        addBossLoot("gloom_spore_from_ravager", "entities/ravager", MFUItems.GLOOM_SPORE);

        // Boss mobs - guaranteed Incubation Crystals
        addBossLoot("incubation_crystal_from_ender_dragon", "entities/ender_dragon", MFUItems.INCUBATION_CRYSTAL);
        addBossLoot("incubation_crystal_from_elder_guardian", "entities/elder_guardian", MFUItems.INCUBATION_CRYSTAL);
        addBossLoot("incubation_crystal_from_warden", "entities/warden", MFUItems.INCUBATION_CRYSTAL);
        addBossLoot("incubation_crystal_from_wither", "entities/wither", MFUItems.INCUBATION_CRYSTAL);
        addBossLoot("incubation_crystal_from_ravager", "entities/ravager", MFUItems.INCUBATION_CRYSTAL);

        // Treasure chests with Gloomsteel equipment
        String[] treasureChests = {
                "chests/village/village_armorer", "chests/village/village_mason",
                "chests/village/village_toolsmith", "chests/village/village_weaponsmith",
                "chests/shipwreck_treasure", "chests/shipwreck_supply", "chests/buried_treasure",
                "chests/ruined_portal", "chests/jungle_temple", "chests/desert_pyramid",
                "chests/bastion_bridge", "chests/bastion_treasure", "chests/bastion_other",
                "chests/end_city_treasure", "chests/stronghold_corridor", "chests/stronghold_crossing",
                "chests/stronghold_library", "chests/pillager_outpost", "chests/nether_bridge",
                "chests/woodland_mansion"
        };

        // Gloomsteel tools in chests (2% chance each)
        for (String chest : treasureChests) {
            addChestLoot("gloomsteel_sword_from_" + chest.replace("chests/", ""),
                    chest, MFUItems.GLOOMSTEEL_SWORD, 0.02F);
            addChestLoot("gloomsteel_pickaxe_from_" + chest.replace("chests/", ""),
                    chest, MFUItems.GLOOMSTEEL_PICKAXE, 0.02F);
            addChestLoot("gloomsteel_axe_from_" + chest.replace("chests/", ""),
                    chest, MFUItems.GLOOMSTEEL_AXE, 0.02F);
            addChestLoot("gloomsteel_shovel_from_" + chest.replace("chests/", ""),
                    chest, MFUItems.GLOOMSTEEL_SHOVEL, 0.02F);
            addChestLoot("gloomsteel_hoe_from_" + chest.replace("chests/", ""),
                    chest, MFUItems.GLOOMSTEEL_HOE, 0.02F);
        }

        // Gloomsteel armor in chests (1.5% chance each)
        for (String chest : treasureChests) {
            addChestLoot("gloomsteel_helmet_from_" + chest.replace("chests/", ""),
                    chest, MFUItems.GLOOMSTEEL_HELMET, 0.015F);
            addChestLoot("gloomsteel_chestplate_from_" + chest.replace("chests/", ""),
                    chest, MFUItems.GLOOMSTEEL_CHESTPLATE, 0.015F);
            addChestLoot("gloomsteel_leggings_from_" + chest.replace("chests/", ""),
                    chest, MFUItems.GLOOMSTEEL_LEGGINGS, 0.015F);
            addChestLoot("gloomsteel_boots_from_" + chest.replace("chests/", ""),
                    chest, MFUItems.GLOOMSTEEL_BOOTS, 0.015F);
        }
    }

    /**
     * Helper method to add loot to mob drops with a chance
     */
    private void addMobLoot(String name, String entityPath, net.neoforged.neoforge.registries.DeferredItem<?> item, float chance) {
        add(name, new AddItemModifier(
                new LootItemCondition[] {
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace(entityPath)).build(),
                        LootItemRandomChanceCondition.randomChance(chance).build()
                },
                item.get()
        ));
    }

    /**
     * Helper method to add loot to chests with a chance
     */
    private void addChestLoot(String name, String chestPath, net.neoforged.neoforge.registries.DeferredItem<?> item, float chance) {
        add(name, new AddItemModifier(
                new LootItemCondition[] {
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace(chestPath)).build(),
                        LootItemRandomChanceCondition.randomChance(chance).build()
                },
                item.get()
        ));
    }

    /**
     * Helper method to add guaranteed loot to boss mobs
     */
    private void addBossLoot(String name, String entityPath, net.neoforged.neoforge.registries.DeferredItem<?> item) {
        add(name, new AddItemModifier(
                new LootItemCondition[] {
                        new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace(entityPath)).build()
                },
                item.get()
        ));
    }
}