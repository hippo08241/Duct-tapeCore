package com.hippo.ducttapecore.mixin.compat;

import com.hippo.ducttapecore.compat.cyberware.CyberwareDefibrillatorHandler;
import com.hippo.ducttapecore.config.ModConfig;
import net.blay09.mods.hardcorerevival.HardcoreRevival;
import net.blay09.mods.hardcorerevival.handler.DeathHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Robotic Parts 재세동기 INSTANT 모드: HardcoreRevival이 사망을 기절로 바꾸기 직전에 재세동기를 먼저 실행한다.
 * 작동하면 HardcoreRevival의 기절 처리(기절 화면, 발광, 사망 메시지)를 통째로 건너뛴다.
 * 작동하지 않으면(전력 부족 등) 평소대로 기절하고, 최종 사망 때 CyberwareDefibrillatorHandler가 다시 시도한다.
 */
@Mixin(value = DeathHandler.class, remap = false)
public abstract class MixinHardcoreRevivalDeathHandler {

    @Inject(method = "onPlayerDeath", at = @At("HEAD"), cancellable = true)
    private void ducttapecore$defibrillateBeforeKnockout(LivingDeathEvent event, CallbackInfo ci) {
        if (!"INSTANT".equals(ModConfig.cyberwareDefibrillatorMode)) {
            return;
        }
        if (!(event.getEntityLiving() instanceof EntityPlayerMP)) {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        // 최종 사망(기절 시간 종료/포기)과 낙사는 HardcoreRevival의 원래 흐름에 맡긴다.
        if (event.getSource() == HardcoreRevival.notRescuedInTime
                || event.getSource() == DamageSource.OUT_OF_WORLD
                || player.getEntityData().getBoolean(DeathHandler.IGNORE_REVIVAL_DEATH)) {
            return;
        }

        if (CyberwareDefibrillatorHandler.tryDefibrillate(event)) {
            ci.cancel();
        }
    }
}
