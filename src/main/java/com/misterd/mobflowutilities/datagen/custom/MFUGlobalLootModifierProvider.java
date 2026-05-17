package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.item.MFUItems;
import com.misterd.mobflowutilities.loot.AddItemModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class MFUGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public MFUGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MobFlowUtilities.MODID);
    }

    @Override
    protected void start() {
        String[] hostileChests = {
                "chests/bastion_bridge", "chests/bastion_treasure", "chests/bastion_other",
                "chests/end_city_treasure", "chests/stronghold_corridor", "chests/stronghold_crossing",
                "chests/stronghold_library", "chests/pillager_outpost", "chests/nether_bridge",
                "chests/woodland_mansion"
        };

        for (String chest : hostileChests) {
            addChestLoot("gloom_spore_from_" + chest.replace("chests/", ""),
                    chest, MFUItems.GLOOM_SPORE, 0.2F);
        }

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
                    chest, MFUItems.GLIMMER_SPROUT, 0.2F);
        }
    }

    private void addChestLoot(String name, String chestPath, net.neoforged.neoforge.registries.DeferredItem<?> item, float chance) {
        add(name, new AddItemModifier(
                new LootItemCondition[] {
                        new LootTableIdCondition.Builder(Identifier.withDefaultNamespace(chestPath)).build(),
                        LootItemRandomChanceCondition.randomChance(chance).build()
                },
                0,
                item.get()
        ));
    }
}