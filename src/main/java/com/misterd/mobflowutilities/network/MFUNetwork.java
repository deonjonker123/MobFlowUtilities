package com.misterd.mobflowutilities.network;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class MFUNetwork {
    public static void register(IEventBus eventBus) {
        eventBus.addListener(MFUNetwork::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("mobflowutilities");

        registrar.playToServer(
                ConfigPacket.TYPE,
                ConfigPacket.STREAM_CODEC,
                ConfigPacket::handle
        );

        registrar.playToServer(
                CollectorXpPacket.TYPE,
                CollectorXpPacket.STREAM_CODEC,
                CollectorXpPacket::handle
        );

        registrar.playToServer(
                OpenFilterPacket.TYPE,
                OpenFilterPacket.STREAM_CODEC,
                OpenFilterPacket::handle
        );
    }
}