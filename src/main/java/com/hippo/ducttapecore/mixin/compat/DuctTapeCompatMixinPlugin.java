package com.hippo.ducttapecore.mixin.compat;

import com.hippo.ducttapecore.DuctTapeCore;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class DuctTapeCompatMixinPlugin implements IMixinConfigPlugin {

    private static final String QUARK_BACKPACKS_CLASS_DOTTED = "vazkii.quark.oddities.feature.Backpacks";
    private static final String QUARK_BACKPACKS_CLASS_INTERNAL = "vazkii/quark/oddities/feature/Backpacks";
    private static final String BACKPACK_FIELD_NAME = "backpack";
    private static final String BACKPACK_LEGACY_FIELD_DESC = "Lnet/minecraft/item/Item;";

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
            return classExists("se.gory_moon.globalgamerules.WorldEvents");
        }
        if (mixinClassName.endsWith("MixinClientProxy")) {
            return classExists("net.blay09.mods.hardcorerevival.client.ClientProxy");
        }
        if (mixinClassName.endsWith("MixinTileEntityShellStorage")) {
            return classExists("me.ichun.mods.sync.common.tileentity.TileEntityShellStorage");
        }
        if (mixinClassName.endsWith("MixinQuarkBackpacksFieldCompat")) {
            return classExists(QUARK_BACKPACKS_CLASS_DOTTED);
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