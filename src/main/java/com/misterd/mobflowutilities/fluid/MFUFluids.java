package com.misterd.mobflowutilities.fluid;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.fluid.custom.MFUXpFluid;
import com.misterd.mobflowutilities.fluid.custom.MFUXpFluidBlock;
import com.misterd.mobflowutilities.fluid.custom.MFUXpFluidType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class MFUFluids {

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, MobFlowUtilities.MODID);

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, MobFlowUtilities.MODID);

    public static final DeferredRegister<Block> FLUID_BLOCKS =
            DeferredRegister.create(Registries.BLOCK, MobFlowUtilities.MODID);

    public static final DeferredRegister<Item> FLUID_ITEMS =
            DeferredRegister.create(Registries.ITEM, MobFlowUtilities.MODID);

    public static final DeferredHolder<FluidType, MFUXpFluidType> LIQUID_XP_TYPE =
            FLUID_TYPES.register("liquid_xp", MFUXpFluidType::new);

    public static final DeferredHolder<Fluid, MFUXpFluid.Source> LIQUID_XP_SOURCE =
            FLUIDS.register("liquid_xp", MFUXpFluid.Source::new);

    public static final DeferredHolder<Fluid, MFUXpFluid.Flowing> LIQUID_XP_FLOWING =
            FLUIDS.register("liquid_xp_flowing", MFUXpFluid.Flowing::new);

    public static final DeferredHolder<Block, MFUXpFluidBlock> LIQUID_XP_BLOCK =
            FLUID_BLOCKS.register("liquid_xp_block",
                    id -> new MFUXpFluidBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
                            .setId(ResourceKey.create(Registries.BLOCK, id))
                            .noLootTable()));

    public static final DeferredHolder<Item, BucketItem> LIQUID_XP_BUCKET =
            FLUID_ITEMS.register("liquid_xp_bucket",
                    id -> new BucketItem(LIQUID_XP_SOURCE.get(),
                            new Item.Properties()
                                    .setId(ResourceKey.create(Registries.ITEM, id))
                                    .craftRemainder(Items.BUCKET)
                                    .stacksTo(1)));

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
        FLUID_BLOCKS.register(eventBus);
        FLUID_ITEMS.register(eventBus);
    }
}