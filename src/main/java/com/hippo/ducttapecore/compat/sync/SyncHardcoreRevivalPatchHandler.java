package com.hippo.ducttapecore.compat.sync;

import com.hippo.ducttapecore.DuctTapeCore;
import com.hippo.ducttapecore.config.ModConfig;
import me.ichun.mods.sync.common.core.EventHandlerServer;
import me.ichun.mods.sync.common.shell.ShellHandler;
import me.ichun.mods.sync.common.tileentity.TileEntityDualVertical;
import net.blay09.mods.hardcorerevival.HardcoreRevival;
import net.blay09.mods.hardcorerevival.PlayerKnockedOutEvent;
import net.blay09.mods.hardcorerevival.capability.CapabilityHardcoreRevival;
import net.blay09.mods.hardcorerevival.capability.IHardcoreRevival;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SyncHardcoreRevivalPatchHandler {

    @SubscribeEvent
    public void onPlayerKnockedOut(PlayerKnockedOutEvent event) {
        if (!"INSTANT".equals(ModConfig.syncRespawnMode)) {
            return;
        }
        if (!(event.getPlayer() instanceof EntityPlayerMP)) {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();

        if (ShellHandler.syncInProgress.containsKey(player.getName())) {
            return;
        }

        TileEntityDualVertical tpPosition = EventHandlerServer.getClosestRespawnShell(player);
        if (tpPosition == null) {
            return;
        }

        // onDeath를 직접 부르면 HardcoreRevival의 사망 처리 도중에 재진입해서, 부활 후에
        // HardcoreRevival이 발광/사망 메시지를 다시 적용한다. 기절 시간을 끝으로 돌려
        // 다음 틱에 HardcoreRevival이 정상 경로로 최종 사망을 처리하게 한다.
        IHardcoreRevival revival = player.getCapability(CapabilityHardcoreRevival.REVIVAL_CAPABILITY, null);
        if (revival != null) {
            revival.setDeathTime(net.blay09.mods.hardcorerevival.ModConfig.maxDeathTicks);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onFinalDeath(LivingDeathEvent event) {
        if ("OFF".equals(ModConfig.syncRespawnMode)) {
            return;
        }
        if (event.getSource() != HardcoreRevival.notRescuedInTime) {
            return;
        }
        if (!(event.getEntityLiving() instanceof EntityPlayerMP)) {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        boolean syncWasPrepared = ShellHandler.syncInProgress.containsKey(player.getName());

        if (event.isCanceled()) {
            if (syncWasPrepared) {
                cancelSyncPreparation(player);
            }
        } else if (syncWasPrepared) {
            event.setCanceled(true);
        }
    }

    private void cancelSyncPreparation(EntityPlayerMP player) {
        TileEntityDualVertical tpPosition = ShellHandler.syncInProgress.get(player.getName());
        if (tpPosition != null) {
            tpPosition.resyncPlayer = 0;
            tpPosition.wasDead = false;
        }
        ShellHandler.syncInProgress.remove(player.getName());
        player.getEntityData().setBoolean("isDeathSyncing", false);
    }
}