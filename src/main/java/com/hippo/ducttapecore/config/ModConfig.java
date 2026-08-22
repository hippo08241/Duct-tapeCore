package com.hippo.ducttapecore.config;

import com.hippo.ducttapecore.DuctTapeCore;
import com.hippo.ducttapecore.restriction.RestrictionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

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
    public static boolean qualityToolsJeiEnabled;

    // ===== compat.global game rules =====
    public static boolean globalGameRulesEnabled;

    // ===== compat.hardcore revival =====
    public static boolean hardcoreRevivalEnabled;
    public static boolean hardcoreRevivalResetOnExternalReviveEnabled;

    // ===== compat.sync =====
    public static String syncRespawnMode; // "OFF", "TIMEOUT", "INSTANT"
    public static boolean syncFixShellStorageReregistration;

    // ===== compat.architecture craft =====
    public static boolean architectureCraftFixParticlesEnabled;

    public static void init(File configFile) {
        config = new Configuration(configFile);
        syncConfig();
    }

    public static void syncConfig() {
        // ---- tweaks.restricted blocks ----
        String catRestricted = "tweaks.restricted blocks";

        restrictedBlocksEnabled = config.getBoolean(
                "[1] Restricted Blocks Toggle", catRestricted, true,
                "블록 제한 기능 on/off"
        );

        restrictedBlocksList = config.getStringList(
                "[2] Restricted Blocks", catRestricted,
                new String[]{"0;minecraft:diamond_ore;§c이 블록은 이 차원에서 사용할 수 없습니다."},
                "형식: <dimensionId|*>;<블록 registry name>;<메시지|언어 키>(생략 가능)"
        );

        restrictedBlocksUseActionBar = config.getBoolean(
                "[3] Use Action Bar", catRestricted, true,
                "true: 액션바로 표시, false: 채팅으로 표시"
        );

        // ---- tweaks.blind invisible players ----
        String catBlind = "tweaks.blind invisible players";

        blindInvisiblePlayersEnabled = config.getBoolean(
                "[1] Blind Invisible Players Toggle", catBlind, true,
                "투명화 상태인 플레이어에게 실명 효과 부여"
        );

        // ---- compat.quality tools ----
        String catQualityTools = "compat.quality tools";

        qualityToolsEnabled = config.getBoolean(
                "[1] Quality Tools Patch Toggle", catQualityTools, true,
                "Quality Tools 패치 전체 on/off"
        );

        qualityToolsBlacklist = config.getStringList(
                "[2] Blacklisted Items", catQualityTools, new String[]{},
                "품질 수식어가 안 붙을 아이템 (modid:item_name)"
        );

        qualityToolsBlockCreative = config.getBoolean(
                "[3] Block Creative Items", catQualityTools, true,
                "크리에이티브로 얻은 아이템엔 수식어 안 붙임"
        );

        qualityToolsJeiEnabled = config.getBoolean(
                "[4] JEI Integration Toggle", catQualityTools, true,
                "JEI 리포징 레시피 표시 on/off"
        );

        // ---- compat.global game rules ----
        String catGlobalGameRules = "compat.global game rules";

        globalGameRulesEnabled = config.getBoolean(
                "[1] Prevent Command Changes From Saving Toggle", catGlobalGameRules, true,
                "커맨드로 바뀐 게임룰이 config에 저장되는 것 방지"
        );

        // ---- compat.hardcore revival ----
        String catHardcoreRevival = "compat.hardcore revival";

        hardcoreRevivalEnabled = config.getBoolean(
                "[1] Fix Death Screen Not Appearing Toggle", catHardcoreRevival, true,
                "사망 수락 버튼 클릭 시 남은 채팅 화면 강제로 닫음"
        );

        hardcoreRevivalResetOnExternalReviveEnabled = config.getBoolean(
                "[2] Fix Frozen/Invisible After External Revival Toggle", catHardcoreRevival, true,
                "HardcoreRevival 필수(Sync는 불필요). 다른 모드가 대신 부활시켰을 때 투명/이동불가 버그 방지"
        );

        // ---- compat.sync ----
        String catSync = "compat.sync";

        syncRespawnMode = config.getString(
                "[1] Sync Respawn Mode", catSync, "TIMEOUT",
                "OFF/TIMEOUT/INSTANT - 복제인간 부활 시점 (HardcoreRevival 필요)",
                new String[]{"OFF", "TIMEOUT", "INSTANT"}
        );

        syncFixShellStorageReregistration = config.getBoolean(
                "[2] Fix Shell Storage Not Recognized After Restart Toggle", catSync, true,
                "재시작 후 저장고 인식 안 되는 Sync 버그 방지"
        );

        // ---- compat.architecture craft ----
        String catArchitectureCraft = "compat.architecture craft";

        architectureCraftFixParticlesEnabled = config.getBoolean(
                "[1] Fix Shape Block Particle Textures Toggle", catArchitectureCraft, true,
                "ArchitectureCraft로 변환된 블록을 부수거나 그 위에서 움직일 때 오류 텍스처 파티클이 나오는 문제 수정"
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
                com.hippo.ducttapecore.compat.jei.QualityToolsJeiIntegration.syncRecipes();
            }
        }
    }
}