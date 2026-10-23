package com.hippo.ducttapecore.mixin.compat;

import com.hippo.ducttapecore.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * ElevatorGroup.canIncreaseCageDepthOffset()의 하드코딩된 최대치(2)를 config 값으로 바꾼다.
 * 서버(패킷 처리)와 클라이언트(GUI 버튼 활성화) 모두 이 메서드를 쓴다.
 */
@Mixin(targets = "com.supermartijn642.movingelevators.elevator.ElevatorGroup", remap = false)
public abstract class MixinMovingElevatorsCabinDepthOffsetLimit {

    @ModifyConstant(method = "canIncreaseCageDepthOffset", constant = @Constant(intValue = 2))
    private int ducttapecore$maxCabinDepthOffset(int original) {
        return ModConfig.movingElevatorsMaxCabinDepthOffset;
    }
}
