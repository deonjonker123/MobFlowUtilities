package com.misterd.mobflowutilities.item;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.block.MFUBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MFUCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MobFlowUtilities.MODID);

    public static final Supplier<CreativeModeTab> MOBFLOWUTILITIES = CREATIVE_MODE_TAB.register("mobflowutilities_creativetab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(MFUBlocks.FAST_FLOW_PAD.get()))
                    .title(Component.translatable("creativetab.mobflowutilities"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(MFUBlocks.FAST_FLOW_PAD);
                        output.accept(MFUBlocks.FASTER_FLOW_PAD);
                        output.accept(MFUBlocks.FASTEST_FLOW_PAD);
                        output.accept(MFUBlocks.DAMAGE_PAD);
                        output.accept(MFUBlocks.DARK_DIRT);
                        output.accept(MFUBlocks.GLIMMER_GRASS);
                        output.accept(MFUBlocks.DARK_GLASS);
                        output.accept(MFUBlocks.GLIMMER_LAMP);
                        output.accept(MFUBlocks.COLLECTOR);
                        output.accept(MFUBlocks.CONTROLLER);
                        output.accept(MFUBlocks.RAW_GLOOMSTEEL_BLOCK);
                        output.accept(MFUBlocks.GLOOMSTEEL_BLOCK);
                        output.accept(MFUBlocks.GLOOMSTEEL_STONE_ORE);
                        output.accept(MFUBlocks.GLOOMSTEEL_DEEPSLATE_ORE);
                        output.accept(MFUBlocks.GLOOMSTEEL_NETHERRACK_ORE);
                        output.accept(MFUBlocks.GLOOMSTEEL_ENDSTONE_ORE);

                        output.accept(MFUItems.BOA_MODULE);
                        output.accept(MFUItems.FIRE_ASPECT_MODULE);
                        output.accept(MFUItems.SHARPNESS_MODULE);
                        output.accept(MFUItems.LOOTING_MODULE);
                        output.accept(MFUItems.SMITE_MODULE);
                        output.accept(MFUItems.COLLECTION_RADIUS_INCREASE_MODULE);
                        output.accept(MFUItems.VOID_FILTER_MODULE);
                        output.accept(MFUItems.RAW_GLOOMSTEEL);
                        output.accept(MFUItems.GLOOMSTEEL_INGOT);
                        output.accept(MFUItems.GLOOMSTEEL_NUGGET);
                        output.accept(MFUItems.GLOOM_SPORE);
                        output.accept(MFUItems.GLIMMER_SPROUT);
                        output.accept(MFUItems.INCUBATION_CRYSTAL);
                        output.accept(MFUItems.EMPTY_GENE_VIAL);
                        output.accept(MFUItems.GENE_SAMPLE_VIAL);
                        output.accept(MFUItems.MOB_CATCHER);
                        output.accept(MFUItems.PAD_WRENCH);

                        output.accept(MFUItems.GLOOMSTEEL_SWORD);
                        output.accept(MFUItems.GLOOMSTEEL_AXE);
                        output.accept(MFUItems.GLOOMSTEEL_PICKAXE);
                        output.accept(MFUItems.GLOOMSTEEL_HOE);
                        output.accept(MFUItems.GLOOMSTEEL_SHOVEL);
                        output.accept(MFUItems.GLOOMSTEEL_PAXEL);
                        output.accept(MFUItems.GLOOMSTEEL_HAMMER);
                        output.accept(MFUItems.GLOOMSTEEL_HELMET);
                        output.accept(MFUItems.GLOOMSTEEL_CHESTPLATE);
                        output.accept(MFUItems.GLOOMSTEEL_LEGGINGS);
                        output.accept(MFUItems.GLOOMSTEEL_BOOTS);
                    }).build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
