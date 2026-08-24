package com.hippo.ducttapecore.mixin.compat;

import com.hippo.ducttapecore.DuctTapeCore;
import net.minecraftforge.common.config.Configuration;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.File;
import java.util.List;
import java.util.Set;

public class DuctTapeCompatMixinPlugin implements IMixinConfigPlugin {

    private static final String QUARK_BACKPACKS_CLASS_DOTTED = "vazkii.quark.oddities.feature.Backpacks";
    private static final String QUARK_BACKPACKS_CLASS_INTERNAL = "vazkii/quark/oddities/feature/Backpacks";
    private static final String BACKPACK_FIELD_NAME = "backpack";
    private static final String BACKPACK_LEGACY_FIELD_DESC = "Lnet/minecraft/item/Item;";

    private static final String QUARK_ITEM_BACKPACK_CLASS_DOTTED = "vazkii.quark.oddities.item.ItemBackpack";

    private static final String MOVINGELEVATORS_CAMOBLOCK_CLASS_DOTTED = "com.supermartijn642.movingelevators.blocks.CamoBlock";
    private static final String MOVINGELEVATORS_CAMOBLOCKENTITY_CLASS_DOTTED = "com.supermartijn642.movingelevators.blocks.CamoBlockEntity";

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
        if (mixinClassName.endsWith("MixinTileEntityShellStorage")) {
            return classExists("me.ichun.mods.sync.common.tileentity.TileEntityShellStorage")
                    && isPatchEnabled("MixinTileEntityShellStorage", "Sync 저장고 호환 패치 on/off");
        }
        if (mixinClassName.endsWith("MixinQuarkBackpacksFieldCompat")) {
            return classExists(QUARK_BACKPACKS_CLASS_DOTTED)
                    && isPatchEnabled("MixinQuarkBackpacksFieldCompat", "QuarkRoN Backpacks 필드 호환 패치 on/off (BaublesEX NoSuchFieldError 방지)");
        }
        if (mixinClassName.endsWith("MixinQuarkItemBackpackRenderCompat")) {
            return classExists(QUARK_ITEM_BACKPACK_CLASS_DOTTED)
                    && isPatchEnabled("MixinQuarkItemBackpackRenderCompat", "QuarkRoN 배낭 등짝 렌더링 호환 패치 on/off (BaublesEX getModel 어댑터)");
        }
        if (mixinClassName.endsWith("MixinMovingElevatorsCamoConsume")) {
            return classExists(MOVINGELEVATORS_CAMOBLOCK_CLASS_DOTTED)
                    && isPatchEnabled("MixinMovingElevatorsCamoConsume", "MovingElevators 카모 적용 시 아이템 소모 패치 on/off");
        }
        if (mixinClassName.endsWith("MixinMovingElevatorsCamoHardness")) {
            return classExists(MOVINGELEVATORS_CAMOBLOCKENTITY_CLASS_DOTTED)
                    && isPatchEnabled("MixinMovingElevatorsCamoHardness", "MovingElevators 카모 블록 강도 반영 패치 on/off");
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
        if (mixinClassName.endsWith("MixinQuarkBackpacksFieldCompat")
                && QUARK_BACKPACKS_CLASS_INTERNAL.equals(targetClass.name)) {
            patchLegacyBackpackField(targetClass);
        }
    }

    private void patchLegacyBackpackField(ClassNode classNode) {
        FieldNode modernField = null;

        for (FieldNode fn : classNode.fields) {
            if (!BACKPACK_FIELD_NAME.equals(fn.name)) {
                continue;
            }
            if (BACKPACK_LEGACY_FIELD_DESC.equals(fn.desc)) {
                // 이미 호환 필드가 존재함 (중복 적용 방지)
                return;
            }
            if ((fn.access & Opcodes.ACC_STATIC) != 0) {
                modernField = fn;
            }
        }

        if (modernField == null) {
            DuctTapeCore.LOGGER.warn(
                    "[{}] QuarkRoN Backpacks 클래스에서 backpack 필드를 찾지 못해 호환 패치를 건너뜁니다",
                    DuctTapeCore.NAME
            );
            return;
        }

        FieldNode legacyField = new FieldNode(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                BACKPACK_FIELD_NAME,
                BACKPACK_LEGACY_FIELD_DESC,
                null,
                null
        );
        classNode.fields.add(legacyField);

        MethodNode clinit = null;
        for (MethodNode mn : classNode.methods) {
            if ("<clinit>".equals(mn.name)) {
                clinit = mn;
                break;
            }
        }

        if (clinit == null) {
            DuctTapeCore.LOGGER.warn(
                    "[{}] QuarkRoN Backpacks 클래스에서 <clinit>을 찾지 못해 호환 패치를 건너뜁니다",
                    DuctTapeCore.NAME
            );
            return;
        }

        InsnList toInsert = new InsnList();
        toInsert.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, BACKPACK_FIELD_NAME, modernField.desc));
        toInsert.add(new FieldInsnNode(Opcodes.PUTSTATIC, classNode.name, BACKPACK_FIELD_NAME, BACKPACK_LEGACY_FIELD_DESC));

        AbstractInsnNode returnNode = null;
        for (AbstractInsnNode insn = clinit.instructions.getLast(); insn != null; insn = insn.getPrevious()) {
            if (insn.getOpcode() == Opcodes.RETURN) {
                returnNode = insn;
                break;
            }
        }

        if (returnNode != null) {
            clinit.instructions.insertBefore(returnNode, toInsert);
        } else {
            clinit.instructions.add(toInsert);
        }

        DuctTapeCore.LOGGER.info(
                "[{}] QuarkRoN Backpacks 클래스에 구버전 호환용 backpack(Item) 필드를 추가했습니다 (BaublesEX 등 호환 패치)",
                DuctTapeCore.NAME
        );
    }
}