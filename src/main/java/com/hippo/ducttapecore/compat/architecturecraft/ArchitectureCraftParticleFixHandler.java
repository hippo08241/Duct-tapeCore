package com.hippo.ducttapecore.compat.architecturecraft;

import com.hippo.ducttapecore.DuctTapeCore;
import com.hippo.ducttapecore.config.ModConfig;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Set;

@SideOnly(Side.CLIENT)
public class ArchitectureCraftParticleFixHandler {

    private static final String AC_MODID = "architecturecraft";
    private static final String SHAPE_BLOCK_PATH = "shape";

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {
        if (!ModConfig.architectureCraftFixParticlesEnabled) {
            return;
        }

        IRegistry<ModelResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
        Set<ModelResourceLocation> keys = modelRegistry.getKeys();

        int patched = 0;
        for (ModelResourceLocation location : keys) {
            if (!AC_MODID.equals(location.getNamespace())) {
                continue;
            }
            if (!SHAPE_BLOCK_PATH.equals(location.getPath())) {
                continue;
            }

            IBakedModel original = modelRegistry.getObject(location);
            if (original == null || original instanceof ParticleFixBakedModel) {
                continue;
            }

            modelRegistry.putObject(location, new ParticleFixBakedModel(original));
            patched++;
        }

        if (patched > 0) {
            DuctTapeCore.LOGGER.info(
                    "[{}] ArchitectureCraft shape 블록 파티클 텍스처 패치 완료 ({}개 변형)",
                    DuctTapeCore.NAME, patched
            );
        }
    }
}