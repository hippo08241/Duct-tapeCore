package com.hippo.ducttapecore.mixin.compat;

import com.supermartijn642.movingelevators.blocks.CamoBlockEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.supermartijn642.movingelevators.blocks.CamoBlock", remap = false)
public abstract class MixinMovingElevatorsCamoConsume {

    @Inject(method = "onRightClick", at = @At("RETURN"))
    private void ducttapecore$consumeCamoItem(
            IBlockState state, World level, CamoBlockEntity blockEntity, BlockPos pos,
            EntityPlayer player, EnumHand hand, EnumFacing hitSide, Vec3d hitLocation,
            CallbackInfoReturnable<Boolean> cir) {

        if (level.isRemote) {
            return;
        }
        if (!Boolean.TRUE.equals(cir.getReturnValue())) {
            return;
        }
        if (player.isSneaking()) {
            return;
        }
        if (player.capabilities.isCreativeMode) {
            return;
        }

        ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty()) {
            stack.shrink(1);
        }
    }
}