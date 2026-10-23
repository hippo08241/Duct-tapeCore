package com.hippo.ducttapecore.mixin.compat;

import com.supermartijn642.movingelevators.blocks.CamoBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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

    private static final ThreadLocal<IBlockState> DUCTTAPECORE$PREVIOUS_CAMO = new ThreadLocal<>();

    @Inject(method = "onRightClick", at = @At("HEAD"))
    private void ducttapecore$rememberCamo(
            IBlockState state, World level, CamoBlockEntity blockEntity, BlockPos pos,
            EntityPlayer player, EnumHand hand, EnumFacing hitSide, Vec3d hitLocation,
            CallbackInfoReturnable<Boolean> cir) {
        DUCTTAPECORE$PREVIOUS_CAMO.set(blockEntity.hasCamoState() ? blockEntity.getCamoState() : null);
    }

    @Inject(method = "onRightClick", at = @At("RETURN"))
    private void ducttapecore$consumeCamoItem(
            IBlockState state, World level, CamoBlockEntity blockEntity, BlockPos pos,
            EntityPlayer player, EnumHand hand, EnumFacing hitSide, Vec3d hitLocation,
            CallbackInfoReturnable<Boolean> cir) {

        IBlockState previous = DUCTTAPECORE$PREVIOUS_CAMO.get();
        DUCTTAPECORE$PREVIOUS_CAMO.remove();

        if (level.isRemote) {
            return;
        }
        if (player.capabilities.isCreativeMode) {
            return;
        }

        IBlockState current = blockEntity.hasCamoState() ? blockEntity.getCamoState() : null;
        if (current == previous) {
            return; // 카모가 바뀌지 않음 (같은 블록 재적용, 다른 상호작용 등)
        }

        // 교체되거나 제거된 이전 카모 블록은 돌려준다.
        if (previous != null) {
            ItemStack refund = ducttapecore$toItem(previous);
            if (!refund.isEmpty() && !player.addItemStackToInventory(refund)) {
                player.dropItem(refund, false);
            }
        }

        if (current != null) {
            ItemStack stack = player.getHeldItem(hand);
            if (!stack.isEmpty()) {
                stack.shrink(1);
            }
        }
    }

    private static ItemStack ducttapecore$toItem(IBlockState camoState) {
        Block block = camoState.getBlock();
        Item item = Item.getItemFromBlock(block);
        if (item == null || item == net.minecraft.init.Items.AIR) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(item, 1, block.damageDropped(camoState));
    }
}
