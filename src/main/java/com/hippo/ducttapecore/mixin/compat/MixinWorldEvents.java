package com.hippo.ducttapecore.mixin.compat;

import com.hippo.ducttapecore.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import se.gory_moon.globalgamerules.WorldEvents;
import se.gory_moon.globalgamerules.config.GGRConfig;

import java.util.HashMap;
import java.util.function.BiConsumer;

@Mixin(value = WorldEvents.class, remap = false)
public class MixinWorldEvents {

    @Redirect(
            method = "onWorldUnLoad",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/HashMap;forEach(Ljava/util/function/BiConsumer;)V"
            )
    )
    private static void ducttapecore$preventCustomGameRuleConfigLeak(
            HashMap<String, Pair<String, GGRConfig.Value>> map,
            BiConsumer<String, Pair<String, GGRConfig.Value>> action
    ) {
        if (!ModConfig.globalGameRulesEnabled) {
            map.forEach(action);
        }
        // 켜져있으면 아무것도 하지 않는다 -> custom 맵이 커맨드로 바뀐 값으로 덮어써지지 않는다.
    }
}