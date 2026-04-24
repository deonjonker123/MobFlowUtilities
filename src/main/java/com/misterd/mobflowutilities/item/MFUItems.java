package com.misterd.mobflowutilities.item;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.item.custom.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

public class MFUItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("mobflowutilities");

    public static final DeferredItem<Item> BOA_MODULE = ITEMS.registerItem("bane_of_arthropods_module",
            props -> new Item(props) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
                    adder.accept(Component.translatable("item.mobflowutilities.bane_of_arthropods_module.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> FIRE_ASPECT_MODULE = ITEMS.registerItem("fire_aspect_module",
            props -> new Item(props) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
                    adder.accept(Component.translatable("item.mobflowutilities.fire_aspect_module.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> LOOTING_MODULE = ITEMS.registerItem("looting_module",
            props -> new Item(props) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
                    adder.accept(Component.translatable("item.mobflowutilities.looting_module.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> SHARPNESS_MODULE = ITEMS.registerItem("sharpness_module",
            props -> new Item(props) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
                    adder.accept(Component.translatable("item.mobflowutilities.sharpness_module.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> SMITE_MODULE = ITEMS.registerItem("smite_module",
            props -> new Item(props) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
                    adder.accept(Component.translatable("item.mobflowutilities.smite_module.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> COLLECTION_RADIUS_INCREASE_MODULE = ITEMS.registerItem("radius_increase_module",
            props -> new Item(props) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
                    adder.accept(Component.translatable("item.mobflowutilities.radius_increase_module.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> SPEED_MODULE = ITEMS.registerItem("speed_module",
            props -> new Item(props) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
                    adder.accept(Component.translatable("item.mobflowutilities.speed_module.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> FAN_WIDTH_INCREASE_MODULE = ITEMS.registerItem("fan_width_increase_module",
            props -> new Item(props) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
                    adder.accept(Component.translatable("item.mobflowutilities.fan_width_increase_module.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> FAN_HEIGHT_INCREASE_MODULE = ITEMS.registerItem("fan_height_increase_module",
            props -> new Item(props) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
                    adder.accept(Component.translatable("item.mobflowutilities.fan_height_increase_module.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> FAN_DISTANCE_INCREASE_MODULE = ITEMS.registerItem("fan_distance_increase_module",
            props -> new Item(props) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
                    adder.accept(Component.translatable("item.mobflowutilities.fan_distance_increase_module.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> GLOOM_SPORE = ITEMS.registerItem("gloom_spore",
            props -> new Item(props) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
                    adder.accept(Component.translatable("item.mobflowutilities.gloom_spore.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> GLIMMER_SPROUT = ITEMS.registerItem("glimmer_sprout",
            props -> new Item(props) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
                    adder.accept(Component.translatable("item.mobflowutilities.glimmer_sprout.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> INCUBATION_ORB = ITEMS.registerItem("incubation_orb",
            props -> new Item(props) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
                    adder.accept(Component.translatable("item.mobflowutilities.incubation_orb.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> EMPTY_GENE_VIAL = ITEMS.registerItem("empty_gene_vial",
            props -> new EmptyGeneVialItem(props.stacksTo(1)));

    public static final DeferredItem<Item> GENE_SAMPLE_VIAL = ITEMS.registerItem("gene_sample_vial",
            props -> new GeneSampleVialItem(props.stacksTo(1)));

    public static final DeferredItem<Item> MOB_CATCHER = ITEMS.registerItem("mob_catcher",
            props -> new MobCatcherItem(props.stacksTo(1)));

    public static final DeferredItem<Item> VOID_FILTER_MODULE = ITEMS.registerItem("void_filter_module",
            props -> new VoidFilterItem(props.stacksTo(1)));

    public static final DeferredItem<Item> PAD_WRENCH = ITEMS.registerItem("pad_wrench",
            props -> new PadWrenchItem(props));

    public static final DeferredItem<Item> FAST_FLOW_PAD = ITEMS.registerItem("flow_pad_fast",
            props -> new BlockItem(MFUBlocks.FAST_FLOW_PAD.get(), props));

    public static final DeferredItem<Item> FASTER_FLOW_PAD = ITEMS.registerItem("flow_pad_faster",
            props -> new BlockItem(MFUBlocks.FASTER_FLOW_PAD.get(), props));

    public static final DeferredItem<Item> FASTEST_FLOW_PAD = ITEMS.registerItem("flow_pad_fastest",
            props -> new BlockItem(MFUBlocks.FASTEST_FLOW_PAD.get(), props));

    public static final DeferredItem<Item> DAMAGE_PAD = ITEMS.registerItem("damage_pad",
            props -> new BlockItem(MFUBlocks.DAMAGE_PAD.get(), props));

    public static final DeferredItem<Item> GLIMMER_LAMP = ITEMS.registerItem("glimmer_lamp",
            props -> new BlockItem(MFUBlocks.GLIMMER_LAMP.get(), props));

    public static final DeferredItem<Item> COLLECTOR = ITEMS.registerItem("collector",
            props -> new BlockItem(MFUBlocks.COLLECTOR.get(), props));

    public static final DeferredItem<Item> CONTROLLER = ITEMS.registerItem("controller",
            props -> new BlockItem(MFUBlocks.CONTROLLER.get(), props));

    public static final DeferredItem<Item> DARK_DIRT = ITEMS.registerItem("dark_dirt",
            props -> new BlockItem(MFUBlocks.DARK_DIRT.get(), props) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
                    adder.accept(Component.translatable("item.mobflowutilities.dark_dirt.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> GLIMMER_GRASS = ITEMS.registerItem("glimmer_grass",
            props -> new BlockItem(MFUBlocks.GLIMMER_GRASS.get(), props) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
                    adder.accept(Component.translatable("item.mobflowutilities.glimmer_grass.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> DARK_GLASS = ITEMS.registerItem("dark_glass",
            props -> new BlockItem(MFUBlocks.DARK_GLASS.get(), props) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
                    adder.accept(Component.translatable("item.mobflowutilities.dark_glass.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> FAN = ITEMS.registerItem("fan",
            props -> new BlockItem(MFUBlocks.FAN.get(), props) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
                    adder.accept(Component.translatable("item.mobflowutilities.fan.subtitle_line1").withStyle(ChatFormatting.LIGHT_PURPLE));
                    adder.accept(Component.translatable("item.mobflowutilities.fan.subtitle_line2").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> GENESIS_CHAMBER = ITEMS.registerItem("genesis_chamber",
            props -> new BlockItem(MFUBlocks.GENESIS_CHAMBER.get(), props) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> adder, TooltipFlag flag) {
                    adder.accept(Component.translatable("item.mobflowutilities.genesis_chamber.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}