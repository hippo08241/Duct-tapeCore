package com.hippo.ducttapecore.network;

import com.hippo.ducttapecore.DuctTapeCore;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {

    public static SimpleNetworkWrapper channel;

    public static void init() {
        channel = NetworkRegistry.INSTANCE.newSimpleChannel(DuctTapeCore.MODID);
        channel.registerMessage(HandlerResetDeathState.class, MessageResetDeathState.class, 0, Side.CLIENT);
    }
}