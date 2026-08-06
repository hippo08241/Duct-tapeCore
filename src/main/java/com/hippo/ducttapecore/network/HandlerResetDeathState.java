package com.hippo.ducttapecore.network;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class HandlerResetDeathState implements IMessageHandler<MessageResetDeathState, IMessage> {

    @Override
    public IMessage onMessage(MessageResetDeathState message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(HandlerResetDeathState::resetClientState);
        return null;
    }

    @SideOnly(Side.CLIENT)
    private static void resetClientState() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null) {
            mc.player.isDead = false;
        }
        mc.displayGuiScreen(null);
    }
}