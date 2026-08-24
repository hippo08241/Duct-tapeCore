package com.hippo.ducttapecore.mixin.compat;

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

    @Inject(method = "getBlockHardness", at = @At("HEAD"), cancellable = true)
    private void ducttapecore$camoHardness(IBlockState state, World worldIn, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (!(te instanceof CamoBlockEntity)) {
            return;
        }

        CamoBlockEntity camoEntity = (CamoBlockEntity) te;
        if (!camoEntity.hasCamoState()) {
            return;
        }

        IBlockState camoState = camoEntity.getCamoState();
        cir.setReturnValue(camoState.getBlockHardness(worldIn, pos));
    }
}