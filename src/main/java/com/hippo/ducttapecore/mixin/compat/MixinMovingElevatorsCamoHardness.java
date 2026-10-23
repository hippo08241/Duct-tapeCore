package com.hippo.ducttapecore.mixin.compat;

import com.supermartijn642.movingelevators.blocks.CamoBlock;
import com.supermartijn642.movingelevators.blocks.CamoBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class MixinMovingElevatorsCamoHardness {

    private static final ThreadLocal<Boolean> DUCTTAPECORE$IN_PROGRESS = ThreadLocal.withInitial(() -> Boolean.FALSE);

    @Inject(method = "getBlockHardness", at = @At("HEAD"), cancellable = true)
    private void ducttapecore$camoHardness(IBlockState state, World worldIn, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        // 모든 블록의 강도 조회에 걸리는 mixin이라, 카모 블록이 아니면 TileEntity 조회 전에 바로 빠진다.
        if (!((Object) this instanceof CamoBlock)) {
            return;
        }
        if (worldIn == null || pos == null) {
            return;
        }
        if (Boolean.TRUE.equals(DUCTTAPECORE$IN_PROGRESS.get())) {
            return;
        }

        TileEntity te = worldIn.getTileEntity(pos);
        if (!(te instanceof CamoBlockEntity)) {
            return;
        }

        CamoBlockEntity camoEntity = (CamoBlockEntity) te;
        if (!camoEntity.hasCamoState()) {
            return;
        }

        IBlockState camoState = camoEntity.getCamoState();

        DUCTTAPECORE$IN_PROGRESS.set(Boolean.TRUE);
        try {
            cir.setReturnValue(camoState.getBlockHardness(worldIn, pos));
        } finally {
            DUCTTAPECORE$IN_PROGRESS.set(Boolean.FALSE);
        }
    }
}