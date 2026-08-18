package com.hippo.ducttapecore.compat.hardcorerevival;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HardcoreRevivalClientTickHandler {

    private static final int SCHEDULE_DELAY_TICKS = 10;

    private static int ticksRemaining = -1;

    public static void scheduleScreenClose() {
        ticksRemaining = SCHEDULE_DELAY_TICKS;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ticksRemaining < 0) {
            return;
        }

        ticksRemaining--;
        if (ticksRemaining <= 0) {
            ticksRemaining = -1;
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }
}