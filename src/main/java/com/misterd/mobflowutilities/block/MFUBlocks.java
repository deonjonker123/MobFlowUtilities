package com.misterd.mobflowutilities.block;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.block.custom.*;
import com.misterd.mobflowutilities.item.MFUItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class MFUBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MobFlowUtilities.MODID);

    public static final DeferredBlock<Block> FAST_FLOW_PAD = registerBlock("flow_pad_fast",
            () -> new FastFlowPadBlock(BlockBehaviour.Properties.of()
                    .strength(3F, 6F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)));

    public static final DeferredBlock<Block> FASTER_FLOW_PAD = registerBlock("flow_pad_faster",
            () -> new FasterFlowPadBlock(BlockBehaviour.Properties.of()
                    .strength(3F, 6F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)));

    public static final DeferredBlock<Block> FASTEST_FLOW_PAD = registerBlock("flow_pad_fastest",
            () -> new FastestFlowPadBlock(BlockBehaviour.Properties.of()
                    .strength(3F, 6F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)));

    public static final DeferredBlock<Block> DAMAGE_PAD = registerBlock("damage_pad",
            () -> new DamagePadBlock(BlockBehaviour.Properties.of()
                    .strength(3F, 6F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)));

    public static final DeferredBlock<Block> DARK_DIRT = registerBlock("dark_dirt",
            () -> new DarkDirtBlock(BlockBehaviour.Properties.of()
                    .strength(1.5F, 6F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.GRASS))
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("block.mobflowutilities.dark_dirt.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredBlock<Block> GLIMMER_GRASS = registerBlock("glimmer_grass",
            () -> new GlimmerGrassBlock(BlockBehaviour.Properties.of()
                    .strength(1.5F, 6F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.GRASS))
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("block.mobflowutilities.glimmer_grass.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredBlock<Block> DARK_GLASS = registerBlock("dark_glass",
            () -> new TintedGlassBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 5000000.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.GLASS)
                    .noOcclusion())
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("block.mobflowutilities.dark_glass.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredBlock<Block> GLIMMER_LAMP = registerBlock("glimmer_lamp",
            () -> new GlimmerLampBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F, 3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.GLASS)
                    .lightLevel(state -> state.getValue(GlimmerLampBlock.LIT) ? 15 : 0)));

    public static final DeferredBlock<Block> COLLECTOR = registerBlock("collector",
            () -> new CollectorBlock(BlockBehaviour.Properties.of()
                    .strength(3F, 6F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .noLootTable()));

    public static final DeferredBlock<Block> CONTROLLER = registerBlock("controller",
            () -> new ControllerBlock(BlockBehaviour.Properties.of()
                    .strength(3F, 6F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> GENESIS_CHAMBER = registerBlock("genesis_chamber",
            () -> new GenesisChamberBlock(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .noOcclusion())
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("block.mobflowutilities.genesis_chamber.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    private static <T extends Block> DeferredBlock<T> registerBlock (String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem (String name, DeferredBlock<T> block) {
        MFUItems.ITEMS.register (name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register (IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
