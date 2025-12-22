package com.misterd.mobflowutilities;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.client.renderer.ber.GenesisChamberBlockEntityRenderer;
import com.misterd.mobflowutilities.component.MFUDataComponents;
import com.misterd.mobflowutilities.config.Config;
import com.misterd.mobflowutilities.entity.MFUBlockEntities;
import com.misterd.mobflowutilities.gui.MFUMenuTypes;
import com.misterd.mobflowutilities.gui.custom.*;
import com.misterd.mobflowutilities.item.MFUCreativeTab;
import com.misterd.mobflowutilities.item.MFUItems;
import com.misterd.mobflowutilities.loot.MFULootModifiers;
import com.misterd.mobflowutilities.network.MFUNetwork;
import com.misterd.mobflowutilities.recipe.MFURecipeSerializers;
import com.misterd.mobflowutilities.util.MFUItemProperties;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(MobFlowUtilities.MODID)
public class MobFlowUtilities {
    public static final String MODID = "mobflowutilities";

    public static final Logger LOGGER = LogUtils.getLogger();

    public MobFlowUtilities(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);

        MFUItems.register(modEventBus);
        MFUBlocks.register(modEventBus);
        MFUCreativeTab.register(modEventBus);
        MFULootModifiers.register(modEventBus);
        MFURecipeSerializers.register(modEventBus);
        MFUBlockEntities.register(modEventBus);
        MFUMenuTypes.register(modEventBus);
        MFUNetwork.register(modEventBus);
        MFUDataComponents.register(modEventBus);
        Config.register(modContainer);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ItemBlockRenderTypes.setRenderLayer(MFUBlocks.DARK_GLASS.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(MFUBlocks.GENESIS_CHAMBER.get(), RenderType.translucent());
            });
            MFUItemProperties.addCustomItemProperties();
        }

        @SubscribeEvent
        public static void registerParticleFactories(RegisterParticleProvidersEvent event) {

        }

        @SubscribeEvent
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(MFUBlockEntities.GENESIS_CHAMBER_BE.get(), GenesisChamberBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(MFUMenuTypes.COLLECTOR_MENU.get(), CollectorScreen::new);
            event.register(MFUMenuTypes.VOID_FILTER_MENU.get(), VoidFilterScreen::new);
            event.register(MFUMenuTypes.CONTROLLER_MENU.get(), ControllerScreen::new);
            event.register(MFUMenuTypes.GENESIS_CHAMBER_MENU.get(), GenesisChamberScreen::new);
        }
    }
}
