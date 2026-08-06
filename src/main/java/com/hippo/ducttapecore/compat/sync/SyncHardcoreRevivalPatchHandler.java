package com.hippo.ducttapecore.compat.sync;

import com.hippo.ducttapecore.DuctTapeCore;
import com.hippo.ducttapecore.config.ModConfig;
import com.hippo.ducttapecore.network.MessageResetDeathState;
import com.hippo.ducttapecore.network.NetworkHandler;
import me.ichun.mods.sync.common.shell.ShellHandler;
import net.blay09.mods.hardcorerevival.HardcoreRevival;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SyncHardcoreRevivalPatchHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onFinalDeath(LivingDeathEvent event) {
        if (!ModConfig.syncHardcoreRevivalEnabled) {
            return;
        }
        if (event.getSource() != HardcoreRevival.notRescuedInTime) {
            return;
        }
        if (!(event.getEntityLiving() instanceof EntityPlayerMP)) {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();

        if (ShellHandler.syncInProgress.containsKey(player.getName())) {
            DuctTapeCore.LOGGER.info("[DuctTapeCore] {} - Sync가 이미 복제인간 동기화를 시작함, 바닐라 사망 처리 취소", player.getName());
            event.setCanceled(true);

            NetworkHandler.channel.sendTo(new MessageResetDeathState(), player);
        }
    }
}