package com.hippo.ducttapecore.compat.architecturecraft;

import com.elytradev.architecture.common.block.BlockShape;
import com.elytradev.architecture.common.tile.TileShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

@SideOnly(Side.CLIENT)
class ParticleFixBakedModel implements IBakedModel {

    private final IBakedModel original;

    ParticleFixBakedModel(IBakedModel original) {
        this.original = original;
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
        TextureAtlasSprite fixed = this.resolveShapeParticleTexture();
        return fixed != null ? fixed : this.original.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return this.original.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.original.getOverrides();
    }

    @Nullable
    private TextureAtlasSprite resolveShapeParticleTexture() {
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc.world;
        EntityPlayer player = mc.player;
        if (world == null || player == null) {
            return null;
        }

        TextureAtlasSprite texture = this.tryPos(world, this.getLookedAtPos(mc));
        if (texture != null) {
            return texture;
        }

        return this.tryPos(world, this.getFeetPos(player));
    }

    @Nullable
    private BlockPos getLookedAtPos(Minecraft mc) {
        RayTraceResult mop = mc.objectMouseOver;
        if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK && mop.getBlockPos() != null) {
            return mop.getBlockPos();
        }
        return null;
    }

    private BlockPos getFeetPos(EntityPlayer player) {
        AxisAlignedBB box = player.getEntityBoundingBox();
        return new BlockPos(player.posX, box.minY - 0.2D, player.posZ);
    }

    @Nullable
    private TextureAtlasSprite tryPos(World world, @Nullable BlockPos pos) {
        if (pos == null || !world.isBlockLoaded(pos)) {
            return null;
        }
        if (!(world.getBlockState(pos).getBlock() instanceof BlockShape)) {
            return null;
        }

        TileShape te = TileShape.get(world, pos);
        if (te == null || !te.hasBaseBlockState()) {
            return null;
        }

        IBlockState baseState = te.getBaseBlockState();
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(baseState);
    }
}