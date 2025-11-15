package com.misterd.mobflowutilities.item;

import com.misterd.mobflowutilities.item.custom.*;
import com.misterd.mobflowutilities.item.equipment.GloomsteelHammerItem;
import com.misterd.mobflowutilities.item.equipment.GloomsteelPaxelItem;
import com.misterd.mobflowutilities.item.equipment.MFUArmorMaterials;
import com.misterd.mobflowutilities.item.equipment.MFUToolTiers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class MFUItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("mobflowutilities");

    // Controller upgrade modules
    public static final DeferredItem<Item> BOA_MODULE = ITEMS.register("bane_of_arthropods_module",
            () -> new Item(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.mobflowutilities.bane_of_arthropods_module.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> FIRE_ASPECT_MODULE = ITEMS.register("fire_aspect_module",
            () -> new Item(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.mobflowutilities.fire_aspect_module.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> LOOTING_MODULE = ITEMS.register("looting_module",
            () -> new Item(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.mobflowutilities.looting_module.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> SHARPNESS_MODULE = ITEMS.register("sharpness_module",
            () -> new Item(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.mobflowutilities.sharpness_module.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> SMITE_MODULE = ITEMS.register("smite_module",
            () -> new Item(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.mobflowutilities.smite_module.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> COLLECTION_RADIUS_INCREASE_MODULE = ITEMS.register("collection_radius_increase_module",
            () -> new Item(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.mobflowutilities.collection_radius_increase_module.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    // Gloomsteel materials
    public static final DeferredItem<Item> RAW_GLOOMSTEEL = ITEMS.register("raw_gloomsteel",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> GLOOMSTEEL_INGOT = ITEMS.register("gloomsteel_ingot",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> GLOOMSTEEL_NUGGET = ITEMS.register("gloomsteel_nugget",
            () -> new Item(new Item.Properties()));

    // Spawn control items
    public static final DeferredItem<Item> GLOOM_SPORE = ITEMS.register("gloom_spore",
            () -> new GloomSporeItem(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.mobflowutilities.gloom_spore.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> GLIMMER_SPROUT = ITEMS.register("glimmer_sprout",
            () -> new GlimmerSproutItem(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.mobflowutilities.glimmer_sprout.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    // Spawn egg crafting components
    public static final DeferredItem<Item> INCUBATION_CRYSTAL = ITEMS.register("incubation_crystal",
            () -> new Item(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("item.mobflowutilities.incubation_crystal.subtitle").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            });

    public static final DeferredItem<Item> EMPTY_GENE_VIAL = ITEMS.register("empty_gene_vial",
            () -> new EmptyGeneVialItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> GENE_SAMPLE_VIAL = ITEMS.register("gene_sample_vial",
            () -> new GeneSampleVialItem(new Item.Properties()));

    // Utility items
    public static final DeferredItem<Item> MOB_CATCHER = ITEMS.register("mob_catcher",
            () -> new MobCatcherItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> VOID_FILTER_MODULE = ITEMS.register("void_filter_module",
            () -> new VoidFilterItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> PAD_WRENCH = ITEMS.register("pad_wrench",
            () -> new PadWrenchItem(new Item.Properties()));

    // Gloomsteel tools
    public static final DeferredItem<SwordItem> GLOOMSTEEL_SWORD = ITEMS.register("gloomsteel_sword",
            () -> new SwordItem(MFUToolTiers.GLOOMSTEEL,
                    new Item.Properties().attributes(SwordItem.createAttributes(MFUToolTiers.GLOOMSTEEL, 3, -2.4F))));

    public static final DeferredItem<AxeItem> GLOOMSTEEL_AXE = ITEMS.register("gloomsteel_axe",
            () -> new AxeItem(MFUToolTiers.GLOOMSTEEL,
                    new Item.Properties().attributes(AxeItem.createAttributes(MFUToolTiers.GLOOMSTEEL, 6.0F, -3.0F))));

    public static final DeferredItem<PickaxeItem> GLOOMSTEEL_PICKAXE = ITEMS.register("gloomsteel_pickaxe",
            () -> new PickaxeItem(MFUToolTiers.GLOOMSTEEL,
                    new Item.Properties().attributes(PickaxeItem.createAttributes(MFUToolTiers.GLOOMSTEEL, 1.0F, -2.8F))));

    public static final DeferredItem<HoeItem> GLOOMSTEEL_HOE = ITEMS.register("gloomsteel_hoe",
            () -> new HoeItem(MFUToolTiers.GLOOMSTEEL,
                    new Item.Properties().attributes(HoeItem.createAttributes(MFUToolTiers.GLOOMSTEEL, -2.0F, 0.0F))));

    public static final DeferredItem<ShovelItem> GLOOMSTEEL_SHOVEL = ITEMS.register("gloomsteel_shovel",
            () -> new ShovelItem(MFUToolTiers.GLOOMSTEEL,
                    new Item.Properties().attributes(ShovelItem.createAttributes(MFUToolTiers.GLOOMSTEEL, 1.5F, -3.0F))));

    // Special Gloomsteel tools
    public static final DeferredItem<GloomsteelPaxelItem> GLOOMSTEEL_PAXEL = ITEMS.register("gloomsteel_paxel",
            () -> new GloomsteelPaxelItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<GloomsteelHammerItem> GLOOMSTEEL_HAMMER = ITEMS.register("gloomsteel_hammer",
            () -> new GloomsteelHammerItem(MFUToolTiers.GLOOMSTEEL, new Item.Properties()
                    .attributes(PickaxeItem.createAttributes(MFUToolTiers.GLOOMSTEEL, 6F, -3f))));

    // Gloomsteel armor
    public static final DeferredItem<ArmorItem> GLOOMSTEEL_HELMET = ITEMS.register("gloomsteel_helmet",
            () -> new ArmorItem(MFUArmorMaterials.GLOOMSTEEL_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(27))));

    public static final DeferredItem<ArmorItem> GLOOMSTEEL_CHESTPLATE = ITEMS.register("gloomsteel_chestplate",
            () -> new ArmorItem(MFUArmorMaterials.GLOOMSTEEL_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(27))));

    public static final DeferredItem<ArmorItem> GLOOMSTEEL_LEGGINGS = ITEMS.register("gloomsteel_leggings",
            () -> new ArmorItem(MFUArmorMaterials.GLOOMSTEEL_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(27))));

    public static final DeferredItem<ArmorItem> GLOOMSTEEL_BOOTS = ITEMS.register("gloomsteel_boots",
            () -> new ArmorItem(MFUArmorMaterials.GLOOMSTEEL_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(27))));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}