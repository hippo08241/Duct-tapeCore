package com.hippo.ducttapecore.compat.hardcorerevival;

import com.hippo.ducttapecore.DuctTapeCore;
import com.hippo.ducttapecore.config.ModConfig;
import com.hippo.ducttapecore.network.MessageResetDeathState;
import com.hippo.ducttapecore.network.NetworkHandler;
import net.blay09.mods.hardcorerevival.HardcoreRevival;
import net.blay09.mods.hardcorerevival.capability.CapabilityHardcoreRevival;
import net.blay09.mods.hardcorerevival.capability.IHardcoreRevival;
import net.blay09.mods.hardcorerevival.handler.DeathHandler;
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
        DuctTapeCore.LOGGER.info("[DuctTapeCore] {} - 사망이 취소되어 부활함, 서버/클라이언트 상태 리셋", player.getName());

        player.getEntityData().removeTag(DeathHandler.IGNORE_REVIVAL_DEATH);
        if (net.blay09.mods.hardcorerevival.ModConfig.glowOnDeath) {
            player.setGlowing(false);
        }
        IHardcoreRevival revival = player.getCapability(CapabilityHardcoreRevival.REVIVAL_CAPABILITY, null);
        if (revival != null) {
            revival.setDeathTime(0);
        }

        NetworkHandler.channel.sendTo(new MessageResetDeathState(), player);
    }
}