package com.misterd.mobflowutilities.item.equipment;

import com.misterd.mobflowutilities.item.MFUItems;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class MFUArmorMaterials {

    public static final Holder<ArmorMaterial> GLOOMSTEEL_ARMOR_MATERIAL;

    static {
        GLOOMSTEEL_ARMOR_MATERIAL = register(
                "gloomsteel",
                Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.BOOTS, 3);
                    map.put(ArmorItem.Type.LEGGINGS, 6);
                    map.put(ArmorItem.Type.CHESTPLATE, 7);
                    map.put(ArmorItem.Type.HELMET, 3);
                    map.put(ArmorItem.Type.BODY, 11);
                }),
                16,
                2.0F,
                0.1F,
                () -> MFUItems.GLOOMSTEEL_INGOT.get()
        );
    }

    private static Holder<ArmorMaterial> register(
            String name,
            EnumMap<ArmorItem.Type, Integer> typeProtection,
            int enchantability,
            float toughness,
            float knockbackResistance,
            Supplier<Item> ingredientItem
    ) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("mobflowutilities", name);

        Holder<SoundEvent> equipSound = SoundEvents.ARMOR_EQUIP_NETHERITE;
        Supplier<Ingredient> ingredient = () -> Ingredient.of(ingredientItem.get());

        List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(id));

        EnumMap<ArmorItem.Type, Integer> protectionMap = new EnumMap<>(ArmorItem.Type.class);
        for (ArmorItem.Type type : ArmorItem.Type.values()) {
            protectionMap.put(type, typeProtection.get(type));
        }

        return Registry.registerForHolder(
                BuiltInRegistries.ARMOR_MATERIAL,
                id,
                new ArmorMaterial(
                        protectionMap,
                        enchantability,
                        equipSound,
                        ingredient,
                        layers,
                        toughness,
                        knockbackResistance
                )
        );
    }
}
