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

/**
 * ArchitectureCraft(https://github.com/elytra/ArchitectureCraft)의 BlockShape
 * ("architecturecraft:shape")는 실제 형태 렌더링을 ICustomRenderer로 직접 그리기
 * 때문에, 블록 자체에 등록된 베이크 모델(IBakedModel)은 텍스처가 없는
 * "오류 텍스처(missing texture, 보라/검정 체크무늬)" 더미 모델입니다.
 * <p>
 * 문제는 아래 두 가지 바닐라 파티클 로직이 ArchitectureCraft의 커스텀 렌더러를
 * 전혀 거치지 않고, {@code BlockModelShapes#getTexture(IBlockState)}를 통해
 * "블록 자체에 등록된" 베이크 모델의 getParticleTexture()를 직접 호출한다는
 * 점입니다.
 * <ul>
 *     <li>블록을 부술 때 튀는 파괴 파티클 (ParticleDigging)</li>
 *     <li>블록 위를 달리거나 점프할 때 발밑에서 튀는 파티클
 *     (EntityLivingBase#spawnRunningParticles)</li>
 * </ul>
 * BlockShape가 {@code getParticleState()}를 재정의해서 변환된 원재료 블록을
 * 돌려주도록 되어 있지만, 위 두 경로는 그 오버라이드를 거치지 않기 때문에
 * 항상 오류 텍스처가 나타납니다.
 * <p>
 * 이 핸들러는 {@link ModelBakeEvent}에서 "architecturecraft:shape"에 해당하는
 * 모든 베이크 모델을 {@link ParticleFixBakedModel}로 감싸서, getParticleTexture()
 * 호출을 가로채 실제 원재료 블록의 텍스처를 대신 돌려주도록 패치합니다.
 * (Mixin/ASM 없이 순수 Forge 이벤트만으로 동작합니다.)
 */
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