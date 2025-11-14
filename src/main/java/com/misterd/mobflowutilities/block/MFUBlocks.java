package com.misterd.mobflowutilities.block;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.item.MFUItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TintedGlassBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class MFUBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MobFlowUtilities.MODID);

    public static final DeferredBlock<Block> FAST_FLOW_PAD = registerBlock("flow_pad_fast",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)));

    public static final DeferredBlock<Block> FASTER_FLOW_PAD = registerBlock("flow_pad_faster",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)));

    public static final DeferredBlock<Block> FASTEST_FLOW_PAD = registerBlock("flow_pad_fastest",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)));

    public static final DeferredBlock<Block> DAMAGE_PAD = registerBlock("damage_pad",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)));

    public static final DeferredBlock<Block> DARK_DIRT = registerBlock("dark_dirt",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(1F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.GRASS))
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("block.mobflowutilities.dark_dirt.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredBlock<Block> GLIMMER_GRASS = registerBlock("glimmer_grass",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(1F)
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
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.GLASS)
                    .lightLevel((state) -> 15))
            {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("block.mobflowutilities.glimmer_lamp.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredBlock<Block> COLLECTOR = registerBlock("collector",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> CONTROLLER = registerBlock("controller",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> GLOOMSTEEL_BLOCK = registerBlock("gloomsteel_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> GLOOMSTEEL_DEEPSLATE_ORE = registerBlock("gloomsteel_deepslate_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> GLOOMSTEEL_NETHERRACK_ORE = registerBlock("gloomsteel_netherrack_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERRACK)));

    public static final DeferredBlock<Block> GLOOMSTEEL_ENDSTONE_ORE = registerBlock("gloomsteel_endstone_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> GLOOMSTEEL_STONE_ORE = registerBlock("gloomsteel_stone_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> RAW_GLOOMSTEEL_BLOCK = registerBlock("raw_gloomsteel_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

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
