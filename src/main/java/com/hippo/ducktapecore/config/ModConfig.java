package com.hippo.ducktapecore.config;

import com.hippo.ducktapecore.DuckTapeCore;
import com.hippo.ducktapecore.restriction.RestrictionManager;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = DuckTapeCore.MODID)
public class ModConfig {

    @Config.Comment({
            "형식: <dimensionId 또는 *>;<블록 registry name>;<메시지 또는 언어 키>",
            "예시:",
            "  0;minecraft:diamond_ore;§c이 블록은 이 차원에서 사용할 수 없습니다.",
            "  0;minecraft:diamond_ore;ducktapecore.restricted.diamond_ore",
            "  *;modid:special_block;§c이 블록은 어떤 차원에서도 사용할 수 없습니다.",
            "메시지는 생략 가능하며, 생략 시 기본 메시지가 사용됩니다."
    })
    @Config.Name("Restricted Blocks")
    public static String[] restrictedBlocks = new String[]{
            "0;minecraft:diamond_ore;§c이 블록은 이 차원에서 사용할 수 없습니다."
    };

    @Config.Name("Use Action Bar")
    @Config.Comment("true면 액션바(머리 위)로, false면 일반 채팅으로 메시지를 표시합니다.")
    public static boolean useActionBar = true;

    @Mod.EventBusSubscriber(modid = DuckTapeCore.MODID)
    public static class ConfigEventHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(DuckTapeCore.MODID)) {
                ConfigManager.sync(DuckTapeCore.MODID, Config.Type.INSTANCE);
                RestrictionManager.reload();
            }
        }
    }
}
