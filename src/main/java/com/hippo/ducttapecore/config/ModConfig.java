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

        qualityToolsJeiEnabled = config.getBoolean(
                "[4] JEI Integration Toggle", catQualityTools, true,
                "false로 설정하면 JEI에서 Quality Tools 리포징 레시피(어떤 재료가 필요한지)를 표시하지 않습니다."
        );

        // ---- compat.global game rules ----
        String catGlobalGameRules = "compat.global game rules";
        config.setCategoryComment(catGlobalGameRules,
                "GlobalGameRules 모드 관련 패치 설정입니다. GlobalGameRules가 설치되어 있지 않으면 이 항목은 아무 효과가 없습니다.");

        globalGameRulesEnabled = config.getBoolean(
                "[1] Prevent Command Changes From Saving Toggle", catGlobalGameRules, true,
                "true면 서버에서 /gamerule 커맨드로 값을 바꿔도 GlobalGameRules의 config 파일에는 반영되지 않고,\n" +
                        "월드를 불러올 때 사용했던(=config 파일의) 값으로 항상 되돌립니다."
        );

        // ---- compat.hardcore revival ----
        String catHardcoreRevival = "compat.hardcore revival";
        config.setCategoryComment(catHardcoreRevival,
                "HardcoreRevival 모드 관련 패치 설정입니다. HardcoreRevival이 설치되어 있지 않으면 이 항목은 아무 효과가 없습니다.");

        hardcoreRevivalEnabled = config.getBoolean(
                "[1] Fix Death Screen Not Appearing Toggle", catHardcoreRevival, true,
                "true면 '사망 수락(Die)' 버튼을 눌렀을 때 남아있는 채팅 화면을 강제로 닫아,\n" +
                        "ESC를 눌러야만 사망 화면이 뜨는 문제를 방지합니다."
        );

        hardcoreRevivalResetOnExternalReviveEnabled = config.getBoolean(
                "[2] Fix Frozen/Invisible After External Revival Toggle", catHardcoreRevival, true,
                "true면 Sync의 복제인간, Cyberware의 Internal Defibrillator처럼 HardcoreRevival의\n" +
                        "사망 처리를 취소하고 그 자리에서 되살리는 다른 모드와 같이 쓸 때, 부활 후 3인칭에서\n" +
                        "투명해지고 움직이지 못하는 문제를 방지합니다."
        );

        // ---- compat.sync ----
        String catSync = "compat.sync";
        config.setCategoryComment(catSync,
                "Sync 모드 관련 패치 설정입니다. [1]은 HardcoreRevival도 같이 설치되어 있어야 동작하고,\n" +
                        "[2]는 Sync만 설치되어 있어도 동작합니다. Sync가 설치되어 있지 않으면 이 항목들은 전부 아무 효과가 없습니다.");

        syncRespawnMode = config.getString(
                "[1] Sync Respawn Mode", catSync, "TIMEOUT",
                "Sync 복제인간으로 부활하는 방식을 선택합니다.\n" +
                        "  OFF: 이 기능을 끕니다. HardcoreRevival 원래 흐름만 사용합니다 (스폰포인트로 부활하는 버그가 재발할 수 있음).\n" +
                        "  TIMEOUT: HardcoreRevival이 사망을 확정지을 때(구조 실패 또는 즉시부활 확정)만 복제인간으로 부활합니다.\n" +
                        "  INSTANT: 다운되는 즉시(구조를 기다리지 않고) 사용 가능한 복제인간이 있으면 곧바로 부활합니다.",
                new String[]{"OFF", "TIMEOUT", "INSTANT"}
        );

        syncFixShellStorageReregistration = config.getBoolean(
                "[2] Fix Shell Storage Not Recognized After Restart Toggle", catSync, true,
                "true면 Sync의 Shell Storage(복제인간 저장고)가 게임 재시작 후 인식이 안 되는 버그(전력이\n" +
                        "계속 켜져 있었을 때 재등록이 안 되는 Sync 자체의 결함)를 고칩니다. HardcoreRevival 없이\n" +
                        "Sync만 설치되어 있어도 동작합니다."
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