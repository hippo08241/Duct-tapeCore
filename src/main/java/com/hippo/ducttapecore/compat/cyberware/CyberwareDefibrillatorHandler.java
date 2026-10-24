package com.hippo.ducttapecore.compat.cyberware;

import com.hippo.ducttapecore.DuctTapeCore;
import com.hippo.ducttapecore.config.ModConfig;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.common.CyberwareContent;
import flaxbeard.cyberware.common.item.ItemHeartUpgrade;
import net.blay09.mods.hardcorerevival.HardcoreRevival;
import net.blay09.mods.hardcorerevival.PlayerKnockedOutEvent;
import net.blay09.mods.hardcorerevival.capability.CapabilityHardcoreRevival;
import net.blay09.mods.hardcorerevival.capability.IHardcoreRevival;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Robotic Parts(Cyberware) 심장 재세동기 + HardcoreRevival 호환.
 * <p>
 * HardcoreRevival이 첫 사망을 HIGHEST에서 취소(기절)하기 때문에 재세동기는 기절 중에는 작동하지 않고,
 * 기절 시간이 끝난 "최종 사망" 때 Sync 복제인간과 같은 NORMAL 우선순위에서 경쟁한다(순서는 모드 로드 순서에 따름).
 * 여기서는 최종 사망 시 재세동기를 HIGH에서 먼저 실행해 순서를 고정한다.
 * INSTANT 모드는 MixinHardcoreRevivalDeathHandler가 HardcoreRevival의 기절 처리 직전에 재세동기를 실행한다
 * (기절/발광/사망 메시지 없이 바로 부활). 그 mixin이 꺼져 있으면 onPlayerKnockedOut이 대신
 * 기절 즉시 최종 사망 처리로 넘겨 재세동기가 작동하게 한다.
 */
public class CyberwareDefibrillatorHandler {

    private static final int META_INTERNAL_DEFIBRILLATOR = ItemHeartUpgrade.META_INTERNAL_DEFIBRILLATOR;

    @SubscribeEvent
    public void onPlayerKnockedOut(PlayerKnockedOutEvent event) {
        if (!"INSTANT".equals(ModConfig.cyberwareDefibrillatorMode)) {
            return;
        }
        if (!(event.getPlayer() instanceof EntityPlayerMP)) {
            return;
        }

        EntityPlayer player = event.getPlayer();
        if (!hasUsableDefibrillator(player)) {
            return; // 작동 불가(전력 부족 등) -> 일반 기절 진행, 최종 사망 때 다시 시도됨
        }

        // 바로 onDeath를 부르면 HardcoreRevival의 사망 처리 도중에 재진입하게 되므로,
        // 기절 시간을 끝으로 돌려 다음 틱에 HardcoreRevival이 정상 경로로 최종 사망을 처리하게 한다.
        IHardcoreRevival revival = player.getCapability(CapabilityHardcoreRevival.REVIVAL_CAPABILITY, null);
        if (revival != null) {
            revival.setDeathTime(net.blay09.mods.hardcorerevival.ModConfig.maxDeathTicks);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onFinalDeath(LivingDeathEvent event) {
        if ("OFF".equals(ModConfig.cyberwareDefibrillatorMode)) {
            return;
        }
        if (event.getSource() != HardcoreRevival.notRescuedInTime) {
            return;
        }
        if (!(event.getEntityLiving() instanceof EntityPlayerMP)) {
            return;
        }

        // Cyberware의 원래 핸들러를 먼저 호출한다. 작동하면 이벤트가 취소되어
        // NORMAL의 Cyberware 핸들러와 Sync 핸들러는 호출되지 않는다.
        tryDefibrillate(event);
    }

    /**
     * Cyberware의 재세동기 처리를 실행한다. 작동하면 이벤트가 취소되고 true를 반환한다.
     * INSTANT 모드에서는 MixinHardcoreRevivalDeathHandler가 HardcoreRevival의 기절 처리 전에 이걸 호출한다.
     */
    public static boolean tryDefibrillate(LivingDeathEvent event) {
        if (event.isCanceled() || !(CyberwareContent.heartUpgrades instanceof ItemHeartUpgrade)) {
            return false;
        }

        ((ItemHeartUpgrade) CyberwareContent.heartUpgrades).handleDeath(event);
        if (!event.isCanceled()) {
            return false;
        }

        EntityLivingBase entity = event.getEntityLiving();
        DuctTapeCore.LOGGER.info("[DuctTapeCore] {} - 심장 재세동기 작동", entity.getName());

        // 체력 1/3로만 살아나서 공격받던 상대에게 곧바로 다시 쓰러지는 것을 막는다.
        int protectionTicks = ModConfig.cyberwareDefibrillatorProtectionTicks;
        if (protectionTicks > 0) {
            entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, protectionTicks, 4, false, true));
        }
        return true;
    }

    private static boolean hasUsableDefibrillator(EntityPlayer player) {
        if (!(CyberwareContent.heartUpgrades instanceof ItemHeartUpgrade)) {
            return false;
        }
        ICyberwareUserData data = CyberwareAPI.getCapabilityOrNull(player);
        if (data == null) {
            return false;
        }

        ItemHeartUpgrade heartUpgrades = (ItemHeartUpgrade) CyberwareContent.heartUpgrades;
        ItemStack defibrillator = data.getCyberware(heartUpgrades.getCachedStack(META_INTERNAL_DEFIBRILLATOR));
        if (defibrillator.isEmpty() || CyberwareAPI.getCyberwareNBT(defibrillator).hasKey("used")) {
            return false;
        }

        // usePower는 전력을 실제로 소모하므로 저장량으로만 판단한다(특수 배터리는 고려하지 않음 -> 보수적).
        return data.getStoredPower() >= heartUpgrades.getPowerConsumption(defibrillator);
    }
}
