package com.hippo.ducttapecore.compat.architecturecraft;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.List;

class StaticParticleTextureBakedModel implements IBakedModel {

    private final IBakedModel original;
    private final IBlockState referenceState;

    StaticParticleTextureBakedModel(IBakedModel original, IBlockState referenceState) {
        this.original = original;
        this.referenceState = referenceState;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return this.original.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return this.original.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.original.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return this.original.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        TextureAtlasSprite sprite = Minecraft.getMinecraft()
                .getBlockRendererDispatcher()
                .getBlockModelShapes()
                .getTexture(this.referenceState);
        return sprite != null ? sprite : this.original.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return this.original.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.original.getOverrides();
    }
}