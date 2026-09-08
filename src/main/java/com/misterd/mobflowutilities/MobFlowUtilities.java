package com.misterd.mobflowutilities;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.client.renderer.ber.GenesisChamberBlockEntityRenderer;
import com.misterd.mobflowutilities.client.renderer.ber.GigaTankBlockEntityRenderer;
import com.misterd.mobflowutilities.component.MFUDataComponents;
import com.misterd.mobflowutilities.config.Config;
import com.misterd.mobflowutilities.blockentity.MFUBlockEntities;
import com.misterd.mobflowutilities.fluid.MFUFluids;
import com.misterd.mobflowutilities.gui.MFUMenuTypes;
import com.misterd.mobflowutilities.gui.custom.*;
import com.misterd.mobflowutilities.item.MFUCreativeTab;
import com.misterd.mobflowutilities.item.MFUItems;
import com.misterd.mobflowutilities.network.MFUNetwork;
import com.misterd.mobflowutilities.recipe.MFURecipeSerializers;
import com.misterd.mobflowutilities.util.MFUItemProperties;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
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
        MFUFluids.register(modEventBus);
        MFUCreativeTab.register(modEventBus);
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

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ItemBlockRenderTypes.setRenderLayer(MFUBlocks.GENESIS_CHAMBER.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(MFUBlocks.COLLECTOR.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(MFUBlocks.FAN.get(), RenderType.translucent());
            });
            MFUItemProperties.addCustomItemProperties();
        }

        @SubscribeEvent
        public static void registerParticleFactories(RegisterParticleProvidersEvent event) {

        }

        @SubscribeEvent
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(MFUBlockEntities.GENESIS_CHAMBER_BE.get(), GenesisChamberBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(MFUBlockEntities.GIGATANK_BE.get(), GigaTankBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(MFUMenuTypes.COLLECTOR_MENU.get(), CollectorScreen::new);
            event.register(MFUMenuTypes.CONTROLLER_MENU.get(), ControllerScreen::new);
            event.register(MFUMenuTypes.GENESIS_CHAMBER_MENU.get(), GenesisChamberScreen::new);
            event.register(MFUMenuTypes.FAN_MENU.get(), FanScreen::new);
        }

        @SubscribeEvent
        public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
            event.registerFluidType(new IClientFluidTypeExtensions() {
                private static final ResourceLocation STILL =
                        ResourceLocation.fromNamespaceAndPath(MobFlowUtilities.MODID, "block/xp_still");
                private static final ResourceLocation FLOWING =
                        ResourceLocation.fromNamespaceAndPath(MobFlowUtilities.MODID, "block/xp_flow");
                private static final ResourceLocation OVERLAY =
                        ResourceLocation.fromNamespaceAndPath(MobFlowUtilities.MODID, "block/xp_overlay");

                @Override
                public ResourceLocation getStillTexture() {
                    return STILL;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return FLOWING;
                }

                @Override
                public ResourceLocation getOverlayTexture() {
                    return OVERLAY;
                }

                @Override
                public int getTintColor() {
                    return 0xFF8ddcbb;
                }
            }, MFUFluids.LIQUID_XP_TYPE.get());
        }
    }
}
