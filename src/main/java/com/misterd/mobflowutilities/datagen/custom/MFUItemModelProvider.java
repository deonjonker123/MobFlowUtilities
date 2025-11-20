package com.misterd.mobflowutilities.datagen.custom;

import com.misterd.mobflowutilities.MobFlowUtilities;
import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.item.MFUItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.LinkedHashMap;

public class MFUItemModelProvider extends ItemModelProvider {

    private static final LinkedHashMap<ResourceKey<TrimMaterial>, Float> TRIM_MATERIALS = new LinkedHashMap<>();

    static {
        TRIM_MATERIALS.put(TrimMaterials.QUARTZ, 0.1F);
        TRIM_MATERIALS.put(TrimMaterials.IRON, 0.2F);
        TRIM_MATERIALS.put(TrimMaterials.NETHERITE, 0.3F);
        TRIM_MATERIALS.put(TrimMaterials.REDSTONE, 0.4F);
        TRIM_MATERIALS.put(TrimMaterials.COPPER, 0.5F);
        TRIM_MATERIALS.put(TrimMaterials.GOLD, 0.6F);
        TRIM_MATERIALS.put(TrimMaterials.EMERALD, 0.7F);
        TRIM_MATERIALS.put(TrimMaterials.DIAMOND, 0.8F);
        TRIM_MATERIALS.put(TrimMaterials.LAPIS, 0.9F);
        TRIM_MATERIALS.put(TrimMaterials.AMETHYST, 1.0F);
    }

    public MFUItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MobFlowUtilities.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(MFUItems.BOA_MODULE.get());
        basicItem(MFUItems.FIRE_ASPECT_MODULE.get());
        basicItem(MFUItems.SHARPNESS_MODULE.get());
        basicItem(MFUItems.SMITE_MODULE.get());
        basicItem(MFUItems.LOOTING_MODULE.get());
        basicItem(MFUItems.COLLECTION_RADIUS_INCREASE_MODULE.get());
        basicItem(MFUItems.VOID_FILTER_MODULE.get());
        basicItem(MFUItems.SPEED_MODULE.get());

        basicItem(MFUItems.PAD_WRENCH.get());

        basicItem(MFUItems.RAW_GLOOMSTEEL.get());
        basicItem(MFUItems.GLOOMSTEEL_INGOT.get());
        basicItem(MFUItems.GLOOMSTEEL_NUGGET.get());

        basicItem(MFUItems.RAW_GLIMMERSTEEL.get());
        basicItem(MFUItems.GLIMMERSTEEL_INGOT.get());
        basicItem(MFUItems.GLIMMERSTEEL_NUGGET.get());

        basicItem(MFUItems.GLOOM_SPORE.get());
        basicItem(MFUItems.GLIMMER_SPROUT.get());

        basicItem(MFUItems.EMPTY_GENE_VIAL.get());
        basicItem(MFUItems.GENE_SAMPLE_VIAL.get());
        basicItem(MFUItems.INCUBATION_ORB.get());

        handheldItem(MFUItems.GLOOMSTEEL_SWORD);
        handheldItem(MFUItems.GLOOMSTEEL_PICKAXE);
        handheldItem(MFUItems.GLOOMSTEEL_AXE);
        handheldItem(MFUItems.GLOOMSTEEL_SHOVEL);
        handheldItem(MFUItems.GLOOMSTEEL_HOE);
        handheldItem(MFUItems.GLOOMSTEEL_PAXEL);
        handheldItem(MFUItems.GLOOMSTEEL_HAMMER);

        trimmedArmorItem(MFUItems.GLOOMSTEEL_HELMET);
        trimmedArmorItem(MFUItems.GLOOMSTEEL_CHESTPLATE);
        trimmedArmorItem(MFUItems.GLOOMSTEEL_LEGGINGS);
        trimmedArmorItem(MFUItems.GLOOMSTEEL_BOOTS);

        handheldItem(MFUItems.GLIMMERSTEEL_SWORD);
        handheldItem(MFUItems.GLIMMERSTEEL_PICKAXE);
        handheldItem(MFUItems.GLIMMERSTEEL_AXE);
        handheldItem(MFUItems.GLIMMERSTEEL_SHOVEL);
        handheldItem(MFUItems.GLIMMERSTEEL_HOE);
        handheldItem(MFUItems.GLIMMERSTEEL_PAXEL);
        handheldItem(MFUItems.GLIMMERSTEEL_HAMMER);

        trimmedArmorItem(MFUItems.GLIMMERSTEEL_HELMET);
        trimmedArmorItem(MFUItems.GLIMMERSTEEL_CHESTPLATE);
        trimmedArmorItem(MFUItems.GLIMMERSTEEL_LEGGINGS);
        trimmedArmorItem(MFUItems.GLIMMERSTEEL_BOOTS);

        saplingItem(MFUBlocks.GLOOMWOOD_SAPLING);
        saplingItem(MFUBlocks.GLIMMERWOOD_SAPLING);

        buttonItem(MFUBlocks.GLOOMWOOD_BUTTON, MFUBlocks.GLOOMWOOD_PLANKS);
        fenceItem(MFUBlocks.GLOOMWOOD_FENCE, MFUBlocks.GLOOMWOOD_PLANKS);
        basicItem(MFUBlocks.GLOOMWOOD_DOOR.asItem());

        buttonItem(MFUBlocks.GLIMMERWOOD_BUTTON, MFUBlocks.GLIMMERWOOD_PLANKS);
        fenceItem(MFUBlocks.GLIMMERWOOD_FENCE, MFUBlocks.GLIMMERWOOD_PLANKS);
        basicItem(MFUBlocks.GLIMMERWOOD_DOOR.asItem());

        basicItem(MFUItems.UMBRAL_BERRIES.get());
        basicItem(MFUItems.RADIANT_BERRIES.get());
    }

    private ItemModelBuilder handheldItem(DeferredItem<?> item) {
        return withExistingParent(item.getId().getPath(), ResourceLocation.parse("item/handheld"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(MobFlowUtilities.MODID, "item/" + item.getId().getPath()));
    }

    private void trimmedArmorItem(DeferredItem<ArmorItem> itemDeferredItem) {
        ArmorItem armorItem = itemDeferredItem.get();

        TRIM_MATERIALS.forEach((trimMaterial, trimValue) -> {
            String armorType = switch (armorItem.getEquipmentSlot()) {
                case HEAD -> "helmet";
                case CHEST -> "chestplate";
                case LEGS -> "leggings";
                case FEET -> "boots";
                default -> "";
            };

            if (armorType.isEmpty()) {
                return;
            }

            String armorItemPath = itemDeferredItem.getId().getPath();
            String trimPath = "trims/items/" + armorType + "_trim_" + trimMaterial.location().getPath();
            String currentTrimName = armorItemPath + "_" + trimMaterial.location().getPath() + "_trim";

            ResourceLocation armorItemResLoc = ResourceLocation.fromNamespaceAndPath(MobFlowUtilities.MODID, armorItemPath);
            ResourceLocation trimResLoc = ResourceLocation.parse(trimPath);
            ResourceLocation trimNameResLoc = ResourceLocation.fromNamespaceAndPath(MobFlowUtilities.MODID, currentTrimName);

            existingFileHelper.trackGenerated(trimResLoc, PackType.CLIENT_RESOURCES, ".png", "textures");

            getBuilder(currentTrimName)
                    .parent(new ModelFile.UncheckedModelFile("item/generated"))
                    .texture("layer0", MobFlowUtilities.MODID + ":item/" + armorItemPath)
                    .texture("layer1", trimResLoc);

            withExistingParent(armorItemPath, mcLoc("item/generated"))
                    .override()
                    .model(new ModelFile.UncheckedModelFile(trimNameResLoc.getNamespace() + ":item/" + trimNameResLoc.getPath()))
                    .predicate(mcLoc("trim_type"), trimValue)
                    .end()
                    .texture("layer0", ResourceLocation.fromNamespaceAndPath(MobFlowUtilities.MODID, "item/" + armorItemPath));
        });
    }

    private ItemModelBuilder saplingItem(DeferredBlock<Block> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(MobFlowUtilities.MODID,"block/" + item.getId().getPath()));
    }

    public void buttonItem(DeferredBlock<?> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/button_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(MobFlowUtilities.MODID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void fenceItem(DeferredBlock<?> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(MobFlowUtilities.MODID,
                        "block/" + baseBlock.getId().getPath()));
    }
}