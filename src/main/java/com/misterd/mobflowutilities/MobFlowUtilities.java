package com.misterd.mobflowutilities;

import com.misterd.mobflowutilities.block.MFUBlocks;
import com.misterd.mobflowutilities.client.MobCatcherOccupiedProperty;
import com.misterd.mobflowutilities.client.renderer.ber.GenesisChamberBlockEntityRenderer;
import com.misterd.mobflowutilities.client.renderer.ber.GigaTankBlockEntityRenderer;
import com.misterd.mobflowutilities.component.MFUDataComponents;
import com.misterd.mobflowutilities.config.Config;
import com.misterd.mobflowutilities.entity.MFUBlockEntities;
import com.misterd.mobflowutilities.fluid.MFUFluids;
import com.misterd.mobflowutilities.gui.MFUMenuTypes;
import com.misterd.mobflowutilities.gui.custom.*;
import com.misterd.mobflowutilities.item.MFUCreativeTab;
import com.misterd.mobflowutilities.item.MFUItems;
import com.misterd.mobflowutilities.loot.MFULootModifiers;
import com.misterd.mobflowutilities.network.MFUNetwork;
import com.misterd.mobflowutilities.recipe.MFURecipeSerializers;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.fluid.FluidTintSources;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

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
        MFUFluids.register(modEventBus);
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

        }

        @SubscribeEvent
        public static void registerParticleFactories(RegisterParticleProvidersEvent event) {

        }

        @SubscribeEvent
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(MFUBlockEntities.GENESIS_CHAMBER_BE.get(), GenesisChamberBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(MFUBlockEntities.GIGA_TANK_BE.get(), GigaTankBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(MFUMenuTypes.COLLECTOR_MENU.get(), CollectorScreen::new);
            event.register(MFUMenuTypes.VOID_FILTER_MENU.get(), VoidFilterScreen::new);
            event.register(MFUMenuTypes.CONTROLLER_MENU.get(), ControllerScreen::new);
            event.register(MFUMenuTypes.GENESIS_CHAMBER_MENU.get(), GenesisChamberScreen::new);
            event.register(MFUMenuTypes.FAN_MENU.get(), FanScreen::new);
        }

        @SubscribeEvent
        public static void registerFluidModels(RegisterFluidModelsEvent event) {
            Material still = new Material(Identifier.fromNamespaceAndPath(MobFlowUtilities.MODID, "block/xp_still"));
            Material flowing = new Material(Identifier.fromNamespaceAndPath(MobFlowUtilities.MODID, "block/xp_flow"));
            Material overlay = new Material(Identifier.fromNamespaceAndPath(MobFlowUtilities.MODID, "block/xp_overlay"));

            FluidModel.Unbaked model = new FluidModel.Unbaked(still, flowing, overlay, FluidTintSources.constant(0xFF8ddcbb));

            event.register(model, MFUFluids.LIQUID_XP_SOURCE, MFUFluids.LIQUID_XP_FLOWING);
        }

        @SubscribeEvent
        public static void onRegisterConditionalItemModelProperties(RegisterConditionalItemModelPropertyEvent event) {
            event.register(
                    Identifier.fromNamespaceAndPath(MobFlowUtilities.MODID, "mob_catcher_occupied"),
                    MobCatcherOccupiedProperty.MAP_CODEC
            );
        }
    }
}
