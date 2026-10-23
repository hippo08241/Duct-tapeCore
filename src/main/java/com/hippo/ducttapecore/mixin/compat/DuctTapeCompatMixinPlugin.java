package com.hippo.ducttapecore.mixin.compat;

import com.hippo.ducttapecore.DuctTapeCore;
import net.minecraftforge.common.config.Configuration;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.util.List;
import java.util.Set;

public class DuctTapeCompatMixinPlugin implements IMixinConfigPlugin {

    private static final String MOVINGELEVATORS_CAMOBLOCK_CLASS_DOTTED = "com.supermartijn642.movingelevators.blocks.CamoBlock";
    private static final String MOVINGELEVATORS_CAMOBLOCKENTITY_CLASS_DOTTED = "com.supermartijn642.movingelevators.blocks.CamoBlockEntity";

    private static final String MOVINGELEVATORS_ELEVATORGROUP_CLASS_DOTTED = "com.supermartijn642.movingelevators.elevator.ElevatorGroup";

    private static final String COMPAT_CONFIG_CATEGORY = "compat.mixin patches";
    private static Configuration compatConfig;

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.endsWith("MixinWorldEvents")) {
            return classExists("se.gory_moon.globalgamerules.WorldEvents")
                    && isPatchEnabled("MixinWorldEvents", "GlobalGameRules 호환 패치 (WorldEvents) on/off");
        }
        if (mixinClassName.endsWith("MixinClientProxy")) {
            return classExists("net.blay09.mods.hardcorerevival.client.ClientProxy")
                    && isPatchEnabled("MixinClientProxy", "HardcoreRevival 클라이언트 호환 패치 on/off");
        }
        if (mixinClassName.endsWith("MixinRescueHandler")) {
            return classExists("net.blay09.mods.hardcorerevival.handler.RescueHandler")
                    && isPatchEnabled("MixinRescueHandler", "HardcoreRevival 다른 플레이어 부활을 사망으로 처리하지 않는 패치 (사이버웨어 소실/경험치 복제 방지) on/off");
        }
        if (mixinClassName.endsWith("MixinTileEntityShellStorage")) {
            return classExists("me.ichun.mods.sync.common.tileentity.TileEntityShellStorage")
                    && isPatchEnabled("MixinTileEntityShellStorage", "Sync 저장고 호환 패치 on/off");
        }
        if (mixinClassName.endsWith("MixinMovingElevatorsCamoConsume")) {
            return classExists(MOVINGELEVATORS_CAMOBLOCK_CLASS_DOTTED)
                    && isPatchEnabled("MixinMovingElevatorsCamoConsume", "MovingElevators 카모 적용 시 아이템 소모 패치 on/off");
        }
        if (mixinClassName.endsWith("MixinMovingElevatorsCamoHardness")) {
            return classExists(MOVINGELEVATORS_CAMOBLOCKENTITY_CLASS_DOTTED)
                    && isPatchEnabled("MixinMovingElevatorsCamoHardness", "MovingElevators 카모 블록 강도 반영 패치 on/off");
        }
        if (mixinClassName.endsWith("MixinMovingElevatorsCabinDepthOffsetLimit")) {
            return classExists(MOVINGELEVATORS_ELEVATORGROUP_CLASS_DOTTED)
                    && isPatchEnabled("MixinMovingElevatorsCabinDepthOffsetLimit", "MovingElevators 캐빈 Depth Offset 최대치 확장 패치 on/off");
        }
        return true;
    }

    private boolean classExists(String className) {
        String resourcePath = className.replace('.', '/') + ".class";
        ClassLoader[] loaders = new ClassLoader[]{
                this.getClass().getClassLoader(),
                Thread.currentThread().getContextClassLoader(),
                ClassLoader.getSystemClassLoader()
        };
        for (ClassLoader loader : loaders) {
            if (loader == null) {
                continue;
            }
            if (loader.getResource(resourcePath) != null) {
                return true;
            }
        }
        return false;
    }

    private boolean isPatchEnabled(String mixinSimpleName, String description) {
        try {
            Configuration config = getCompatConfig();
            boolean enabled = config.getBoolean(
                    mixinSimpleName, COMPAT_CONFIG_CATEGORY, true, description
            );
            if (config.hasChanged()) {
                config.save();
            }
            return enabled;
        } catch (Throwable t) {
            logWarnIfPossible("[DuctTapeCore] compat mixin config를 읽지 못했습니다 (" + mixinSimpleName + "), 패치를 기본값(켬)으로 적용합니다: " + t);
            return true;
        }
    }

    private Configuration getCompatConfig() {
        if (compatConfig == null) {
            File configFile = new File("config", "ducttapecore.cfg");
            compatConfig = new Configuration(configFile);
        }
        return compatConfig;
    }

    private void logWarnIfPossible(String message) {
        try {
            DuctTapeCore.LOGGER.warn(message);
        } catch (Throwable ignored) {
            System.out.println(message);
        }
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}