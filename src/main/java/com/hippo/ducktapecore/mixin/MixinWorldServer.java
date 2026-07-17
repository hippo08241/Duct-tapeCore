package com.hippo.ducktapecore.mixin;

import com.hippo.ducktapecore.restriction.RestrictionManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer {

    @Redirect(
            method = "updateBlocks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;randomTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V"
            )
    )
    private void ducktapecore$onRandomTick(Block block, World world, BlockPos pos, IBlockState state, Random rand) {
        if (RestrictionManager.isRestricted(world.provider.getDimension(), block)) {
            return; // 틱 실행 스킵
        }
        block.randomTick(world, pos, state, rand);
    }

    @Inject(method = "scheduleBlockUpdate", at = @At("HEAD"), cancellable = true)
    private void ducktapecore$onScheduleUpdate(BlockPos pos, Block blockIn, int delay, int priority, CallbackInfo ci) {
        World self = (World) (Object) this;
        if (RestrictionManager.isRestricted(self.provider.getDimension(), blockIn)) {
            ci.cancel();
        }
    }
}
