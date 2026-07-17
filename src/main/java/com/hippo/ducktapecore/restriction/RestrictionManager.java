package com.hippo.ducktapecore.restriction;

import com.hippo.ducktapecore.DuckTapeCore;
import com.hippo.ducktapecore.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class RestrictionManager {

    public static final int ALL_DIMENSIONS = Integer.MIN_VALUE;

    private static final String DEFAULT_MESSAGE = "§c이 블록은 이 차원에서 사용할 수 없습니다.";

    private static final Map<Integer, Map<Block, String>> RESTRICTIONS = new HashMap<>();

    private RestrictionManager() {
    }

    public static void reload() {
        RESTRICTIONS.clear();

        for (String entry : ModConfig.restrictedBlocks) {
            if (entry == null || entry.trim().isEmpty()) continue;

            try {
                String[] parts = entry.split(";", 3);
                if (parts.length < 2) {
                    DuckTapeCore.LOGGER.warn("[DuckTapeCore] 잘못된 config 항목(형식 오류): {}", entry);
                    continue;
                }

                String dimToken = parts[0].trim();
                int dim = dimToken.equals("*") ? ALL_DIMENSIONS : Integer.parseInt(dimToken);

                ResourceLocation blockId = new ResourceLocation(parts[1].trim());
                Block block = ForgeRegistries.BLOCKS.getValue(blockId);
                if (block == null || !ForgeRegistries.BLOCKS.containsKey(blockId)) {
                    DuckTapeCore.LOGGER.warn("[DuckTapeCore] 알 수 없는 블록 registry name: {}", blockId);
                    continue;
                }

                String message = (parts.length >= 3 && !parts[2].trim().isEmpty())
                        ? parts[2]
                        : DEFAULT_MESSAGE;

                RESTRICTIONS.computeIfAbsent(dim, k -> new HashMap<>()).put(block, message);

            } catch (NumberFormatException nfe) {
                DuckTapeCore.LOGGER.error("[DuckTapeCore] 차원 ID 파싱 실패 (숫자 또는 '*' 이어야 함): {}", entry);
            } catch (Exception e) {
                DuckTapeCore.LOGGER.error("[DuckTapeCore] config 파싱 중 오류 발생: {}", entry, e);
            }
        }

        DuckTapeCore.LOGGER.info("[DuckTapeCore] 제한 규칙 {}개 차원 그룹 로드 완료", RESTRICTIONS.size());
    }

    public static String getMessage(int dimension, Block block) {
        if (block == null) return null;

        Map<Block, String> dimMap = RESTRICTIONS.get(dimension);
        if (dimMap != null && dimMap.containsKey(block)) {
            return dimMap.get(block);
        }

        Map<Block, String> allMap = RESTRICTIONS.get(ALL_DIMENSIONS);
        if (allMap != null && allMap.containsKey(block)) {
            return allMap.get(block);
        }

        return null;
    }

    public static boolean isRestricted(int dimension, Block block) {
        if (block == null) return false;

        Map<Block, String> dimMap = RESTRICTIONS.get(dimension);
        if (dimMap != null && dimMap.containsKey(block)) return true;

        Map<Block, String> allMap = RESTRICTIONS.get(ALL_DIMENSIONS);
        return allMap != null && allMap.containsKey(block);
    }
}
