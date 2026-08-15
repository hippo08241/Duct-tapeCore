package com.hippo.ducttapecore.compat.hardcorerevival;

import com.hippo.ducttapecore.DuctTapeCore;
import com.hippo.ducttapecore.config.ModConfig;
import com.hippo.ducttapecore.network.MessageResetDeathState;
import com.hippo.ducttapecore.network.NetworkHandler;
import net.blay09.mods.hardcorerevival.HardcoreRevival;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HardcoreRevivalDeathResetHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onFinalDeath(LivingDeathEvent event) {
        if (!ModConfig.hardcoreRevivalResetOnExternalReviveEnabled) {
            return;
        }
        if (event.getSource() != HardcoreRevival.notRescuedInTime) {
            return;
        }
        if (!event.isCanceled()) {
            return;
        }
        if (!(event.getEntityLiving() instanceof EntityPlayerMP)) {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        NetworkHandler.channel.sendTo(new MessageResetDeathState(), player);
    }
}