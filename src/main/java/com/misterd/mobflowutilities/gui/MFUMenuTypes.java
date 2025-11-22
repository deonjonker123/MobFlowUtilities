package com.misterd.mobflowutilities.gui;

import com.misterd.mobflowutilities.gui.custom.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MFUMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "mobflowutilities");

    public static final DeferredHolder<MenuType<?>, MenuType<CollectorMenu>> COLLECTOR_MENU = registerMenuType("collector_menu", CollectorMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<ControllerMenu>> CONTROLLER_MENU = registerMenuType("controller_menu", ControllerMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<VoidFilterMenu>> VOID_FILTER_MENU = registerMenuType("void_filter_menu", VoidFilterMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<GenesisChamberMenu>> GENESIS_CHAMBER_MENU = registerMenuType("genesis_chamber_menu", GenesisChamberMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<MFUBarrelMenu>> MFU_BARREL_MENU = registerMenuType("mfu_barrel_menu", MFUBarrelMenu::new);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}