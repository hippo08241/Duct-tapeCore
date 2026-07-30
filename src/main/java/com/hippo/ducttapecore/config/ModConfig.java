package com.hippo.ducttapecore.config;

import com.hippo.ducttapecore.DuctTapeCore;
import com.hippo.ducttapecore.restriction.RestrictionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

/**
 * @Config 어노테이션 자동 처리 방식 대신, Configuration API를 직접 다루는 수동 방식.
 * Universal Tweaks와 동일한 카테고리/문법 구조를 위해 이 방식을 사용한다.
 */
public class ModConfig {

    private static Configuration config;

    // ===== tweaks.restricted blocks =====
    public static boolean restrictedBlocksEnabled;
    public static String[] restrictedBlocksList;
    public static boolean restrictedBlocksUseActionBar;

    // ===== tweaks.blind invisible players =====
    public static boolean blindInvisiblePlayersEnabled;

    // ===== compat.quality tools =====
    public static boolean qualityToolsEnabled;
    public static String[] qualityToolsBlacklist;
    public static boolean qualityToolsBlockCreative;

    public static void init(File configFile) {
        config = new Configuration(configFile);
        syncConfig();
    }

    public static void syncConfig() {
        // ---- tweaks.restricted blocks ----
        String catRestricted = "tweaks.restricted blocks";
        config.setCategoryComment(catRestricted, "특정 차원에서 특정 블록 사용을 제한하는 기능입니다.");

        restrictedBlocksEnabled = config.getBoolean(
                "[1] Restricted Blocks Toggle", catRestricted, true,
                "false로 설정하면 블록 제한 기능 자체가 완전히 꺼집니다."
        );

        restrictedBlocksList = config.getStringList(
                "[2] Restricted Blocks", catRestricted,
                new String[]{"0;minecraft:diamond_ore;§c이 블록은 이 차원에서 사용할 수 없습니다."},
                "형식: <dimensionId 또는 *>;<블록 registry name>;<메시지 또는 언어 키>\n" +
                        "예시:\n" +
                        "  0;minecraft:diamond_ore;§c이 블록은 이 차원에서 사용할 수 없습니다.\n" +
                        "  0;minecraft:diamond_ore;ducttapecore.restricted.diamond_ore\n" +
                        "  *;modid:special_block;§c이 블록은 어떤 차원에서도 사용할 수 없습니다.\n" +
                        "메시지는 생략 가능하며, 생략 시 기본 메시지가 사용됩니다."
        );

        restrictedBlocksUseActionBar = config.getBoolean(
                "[3] Use Action Bar", catRestricted, true,
                "true면 액션바(머리 위)로, false면 일반 채팅으로 제한 메시지를 표시합니다."
        );

        // ---- tweaks.blind invisible players ----
        String catBlind = "tweaks.blind invisible players";
        config.setCategoryComment(catBlind, "투명화 상태인 플레이어에게 실명 효과를 부여하는 기능입니다.");

        blindInvisiblePlayersEnabled = config.getBoolean(
                "[1] Blind Invisible Players Toggle", catBlind, true,
                "true면 투명화(Invisibility) 상태인 플레이어에게 자동으로 실명(Blindness) 효과를 부여합니다."
        );

        // ---- compat.quality tools ----
        String catQualityTools = "compat.quality tools";
        config.setCategoryComment(catQualityTools,
                "Quality Tools 모드 관련 패치 설정입니다. Quality Tools가 설치되어 있지 않으면 이 항목들은 아무 효과가 없습니다.");

        qualityToolsEnabled = config.getBoolean(
                "[1] Quality Tools Patch Toggle", catQualityTools, true,
                "false로 설정하면 DuctTapeCore의 Quality Tools 관련 패치 기능을 전부 비활성화합니다 (블랙리스트, 크리에이티브 차단 포함)."
        );

        qualityToolsBlacklist = config.getStringList(
                "[2] Blacklisted Items", catQualityTools, new String[]{},
                "Quality Tools 모드의 품질 수식어가 절대 붙지 않아야 하는 아이템 목록입니다.\n" +
                        "형식: modid:item_name (예: minecraft:diamond_sword)"
        );

        qualityToolsBlockCreative = config.getBoolean(
                "[3] Block Creative Items", catQualityTools, true,
                "true면 크리에이티브 모드 인벤토리에서 가져온 아이템에는 Quality Tools 수식어가 붙지 않습니다."
        );

        if (config.hasChanged()) {
            config.save();
        }
    }

    @Mod.EventBusSubscriber(modid = DuctTapeCore.MODID)
    public static class ConfigEventHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(DuctTapeCore.MODID)) {
                syncConfig();
                RestrictionManager.reload();
                com.hippo.ducttapecore.compat.QualityToolsPatchHandler.invalidateCache();
            }
        }
    }
}