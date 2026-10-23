package com.hippo.ducttapecore.mixin.compat;

import net.blay09.mods.hardcorerevival.handler.RescueHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * HardcoreRevival은 다른 플레이어가 부활시킬 때 recreatePlayerEntity(..., conqueredEnd = false)로
 * 플레이어를 새로 만든다. 그래서 PlayerEvent.Clone이 wasDeath = true로 발생하고, 다른 모드들은 실제 사망으로 처리한다.
 * (예: Robotic Parts는 keepCyberware 게임룰이 꺼져 있으면 이식한 사이버웨어가 전부 사라지고,
 * Consciousness Transmitter가 경험치 캡슐을 떨궈 경험치가 복제된다.)
 * 부활은 사망이 아니므로 conqueredEnd = true로 바꿔 wasDeath = false인 Clone이 되게 한다.
 */
@Mixin(value = RescueHandler.class, remap = false)
public abstract class MixinRescueHandler {

    @ModifyArg(
            method = "finishRescue",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/management/PlayerList;recreatePlayerEntity(Lnet/minecraft/entity/player/EntityPlayerMP;IZ)Lnet/minecraft/entity/player/EntityPlayerMP;",
                    remap = true
            ),
            index = 2
    )
    private static boolean ducttapecore$rescueIsNotDeath(boolean conqueredEnd) {
        return true;
    }
}
