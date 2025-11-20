package com.misterd.mobflowutilities.block;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.block.custom.*;
import com.misterd.mobflowutilities.item.MFUItems;
import com.misterd.mobflowutilities.worldgen.trees.MFUTreeGrowers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class MFUBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MobFlowUtilities.MODID);

    public static final DeferredBlock<Block> FAST_FLOW_PAD = registerBlock("flow_pad_fast",
            () -> new FastFlowPadBlock(BlockBehaviour.Properties.of()
                    .strength(2F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)));

    public static final DeferredBlock<Block> FASTER_FLOW_PAD = registerBlock("flow_pad_faster",
            () -> new FasterFlowPadBlock(BlockBehaviour.Properties.of()
                    .strength(2F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)));

    public static final DeferredBlock<Block> FASTEST_FLOW_PAD = registerBlock("flow_pad_fastest",
            () -> new FastestFlowPadBlock(BlockBehaviour.Properties.of()
                    .strength(2F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)));

    public static final DeferredBlock<Block> DAMAGE_PAD = registerBlock("damage_pad",
            () -> new DamagePadBlock(BlockBehaviour.Properties.of()
                    .strength(2F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE)));

    public static final DeferredBlock<Block> DARK_DIRT = registerBlock("dark_dirt",
            () -> new DarkDirtBlock(BlockBehaviour.Properties.of()
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
            () -> new GlimmerGrassBlock(BlockBehaviour.Properties.of()
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
            () -> new CollectorBlock(BlockBehaviour.Properties.of()
                    .strength(2F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .noLootTable()));

    public static final DeferredBlock<Block> CONTROLLER = registerBlock("controller",
            () -> new ControllerBlock(BlockBehaviour.Properties.of()
                    .strength(2F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    //Gloomsteel blocks
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

    //Glimmersteel blocks
    public static final DeferredBlock<Block> GLIMMERSTEEL_BLOCK = registerBlock("glimmersteel_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> GLIMMERSTEEL_DEEPSLATE_ORE = registerBlock("glimmersteel_deepslate_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> GLIMMERSTEEL_NETHERRACK_ORE = registerBlock("glimmersteel_netherrack_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERRACK)));

    public static final DeferredBlock<Block> GLIMMERSTEEL_ENDSTONE_ORE = registerBlock("glimmersteel_endstone_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> GLIMMERSTEEL_STONE_ORE = registerBlock("glimmersteel_stone_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final DeferredBlock<Block> RAW_GLIMMERSTEEL_BLOCK = registerBlock("raw_glimmersteel_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4F, 50F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    //woods
    public static final DeferredBlock<Block> GLOOMWOOD_LOG = registerBlock("gloomwood_log",
            () -> new MFUFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)));

    public static final DeferredBlock<Block> STRIPPED_GLOOMWOOD_LOG = registerBlock("stripped_gloomwood_log",
            () -> new MFUFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG)));

    public static final DeferredBlock<Block> GLOOMWOOD = registerBlock("gloomwood",
            () -> new MFUFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD)));

    public static final DeferredBlock<Block> STRIPPED_GLOOMWOOD = registerBlock("stripped_gloomwood",
            () -> new MFUFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD)));

    public static final DeferredBlock<Block> GLOOMWOOD_PLANKS = registerBlock("gloomwood_planks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
            {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }
            });

    public static final DeferredBlock<Block> GLOOMWOOD_LEAVES = registerBlock("gloomwood_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES))
            {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 60;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 30;
                }
            });

    public static final DeferredBlock<Block> GLOOMWOOD_SAPLING = registerBlock("gloomwood_sapling",
            () -> new SaplingBlock(MFUTreeGrowers.GLOOMWOOD, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));

    public static final DeferredBlock<Block> GLIMMERWOOD_LOG = registerBlock("glimmerwood_log",
            () -> new MFUFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG)));

    public static final DeferredBlock<Block> STRIPPED_GLIMMERWOOD_LOG = registerBlock("stripped_glimmerwood_log",
            () -> new MFUFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG)));

    public static final DeferredBlock<Block> GLIMMERWOOD = registerBlock("glimmerwood",
            () -> new MFUFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD)));

    public static final DeferredBlock<Block> STRIPPED_GLIMMERWOOD = registerBlock("stripped_glimmerwood",
            () -> new MFUFlammableRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD)));

    public static final DeferredBlock<Block> GLIMMERWOOD_PLANKS = registerBlock("glimmerwood_planks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))
            {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 20;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }
            }
    );

    public static final DeferredBlock<Block> GLIMMERWOOD_LEAVES = registerBlock("glimmerwood_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES))
            {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 60;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 30;
                }
            });

    public static final DeferredBlock<Block> GLIMMERWOOD_SAPLING = registerBlock("glimmerwood_sapling",
            () -> new SaplingBlock(MFUTreeGrowers.GLIMMERWOOD, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));

    //non-block blocks
    public static final DeferredBlock<StairBlock> GLOOMWOOD_STAIRS = registerBlock("gloomwood_stairs",
            () -> new StairBlock(MFUBlocks.GLOOMWOOD_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<StairBlock> GLIMMERWOOD_STAIRS = registerBlock("glimmerwood_stairs",
            () -> new StairBlock(MFUBlocks.GLIMMERWOOD_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<SlabBlock> GLOOMWOOD_SLAB = registerBlock("gloomwood_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<SlabBlock> GLIMMERWOOD_SLAB = registerBlock("glimmerwood_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<PressurePlateBlock> GLOOMWOOD_PRESSURE_PLATE = registerBlock("gloomwood_pressure_plate",
            () -> new PressurePlateBlock(BlockSetType.BIRCH, BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<PressurePlateBlock> GLIMMERWOOD_PRESSURE_PLATE = registerBlock("glimmerwood_pressure_plate",
            () -> new PressurePlateBlock(BlockSetType.BIRCH, BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<ButtonBlock> GLOOMWOOD_BUTTON = registerBlock("gloomwood_button",
            () -> new ButtonBlock(BlockSetType.BIRCH, 20, BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops().noCollission()));
    public static final DeferredBlock<ButtonBlock> GLIMMERWOOD_BUTTON = registerBlock("glimmerwood_button",
            () -> new ButtonBlock(BlockSetType.BIRCH, 20, BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops().noCollission()));

    public static final DeferredBlock<FenceBlock> GLOOMWOOD_FENCE = registerBlock("gloomwood_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<FenceBlock> GLIMMERWOOD_FENCE = registerBlock("glimmerwood_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<FenceGateBlock> GLOOMWOOD_FENCE_GATE = registerBlock("gloomwood_fence_gate",
            () -> new FenceGateBlock(WoodType.BIRCH, BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<FenceGateBlock> GLIMMERWOOD_FENCE_GATE = registerBlock("gilmmerwood_fence_gate",
            () -> new FenceGateBlock(WoodType.BIRCH, BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<DoorBlock> GLOOMWOOD_DOOR = registerBlock("gloomwood_door",
            () -> new DoorBlock(BlockSetType.BIRCH, BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops().noOcclusion()));
    public static final DeferredBlock<DoorBlock> GLIMMERWOOD_DOOR = registerBlock("glimmerwood_door",
            () -> new DoorBlock(BlockSetType.BIRCH, BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops().noOcclusion()));

    public static final DeferredBlock<TrapDoorBlock> GLOOMWOOD_TRAPDOOR = registerBlock("gloomwood_trapdoor",
            () -> new TrapDoorBlock(BlockSetType.BIRCH, BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops().noOcclusion()));
    public static final DeferredBlock<TrapDoorBlock> GLIMMERWOOD_TRAPDOOR = registerBlock("gilmmerwood_trapdoor",
            () -> new TrapDoorBlock(BlockSetType.BIRCH, BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops().noOcclusion()));

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
