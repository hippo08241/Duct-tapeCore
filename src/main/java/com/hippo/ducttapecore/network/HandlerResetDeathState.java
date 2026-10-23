package com.hippo.ducttapecore.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class HandlerResetDeathState implements IMessageHandler<MessageResetDeathState, IMessage> {

    @Override
    public IMessage onMessage(MessageResetDeathState message, MessageContext ctx) {
        float health = message.getHealth();
        Minecraft.getMinecraft().addScheduledTask(() -> resetClientState(health));
        return null;
    }

    @SideOnly(Side.CLIENT)
    private static void resetClientState(float health) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        if (player == null) {
            return;
        }

        // 체력 동기화 패킷보다 먼저 도착할 수 있으므로 직접 적용한다.
        // 체력이 0인 채로 화면을 닫으면 바닐라가 즉시 사망 화면을 다시 연다.
        if (health > 0.0F) {
            player.setHealth(health);
        }
        player.isDead = false;
        player.deathTime = 0;

        // HardcoreRevival의 MessageDie가 isDead = true로 만든 뒤 월드 틱이 지나가면
        // 플레이어가 클라이언트 월드에서 제거된다(3인칭 투명 + 이동 불가). 빠져 있으면 다시 넣는다.
        WorldClient world = mc.world;
        if (world != null && !world.loadedEntityList.contains(player)) {
            world.playerEntities.remove(player);
            world.spawnEntity(player);
        }

        if (mc.currentScreen instanceof GuiGameOver || mc.currentScreen instanceof GuiChat) {
            mc.displayGuiScreen(null);
        }
    }
}
