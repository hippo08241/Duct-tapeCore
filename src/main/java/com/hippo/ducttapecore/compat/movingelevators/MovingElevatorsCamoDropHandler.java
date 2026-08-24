package com.hippo.ducttapecore.compat.movingelevators;

import com.hippo.ducttapecore.DuctTapeCore;
import com.hippo.ducttapecore.config.ModConfig;
import com.supermartijn642.movingelevators.blocks.CamoBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MovingElevatorsCamoDropHandler {

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!ModConfig.movingElevatorsCamoDropEnabled) {
            return;
        }
        if (event.isCanceled()) {
            return;
        }

        World world = event.getWorld();
        if (world.isRemote) {
            return;
        }

        BlockPos pos = event.getPos();
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof CamoBlockEntity)) {
            return;
        }

        CamoBlockEntity camoEntity = (CamoBlockEntity) te;
        if (!camoEntity.hasCamoState()) {
            return;
        }

        EntityPlayer player = event.getPlayer();
        if (player != null && player.capabilities.isCreativeMode) {
            return;
        }

        IBlockState camoState = camoEntity.getCamoState();
        Block camoBlock = camoState.getBlock();

        try {
            ItemStack tool = player != null ? player.getHeldItemMainhand() : ItemStack.EMPTY;
            boolean silkTouch = !tool.isEmpty() && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0;

            if (silkTouch && camoBlock.canSilkHarvest(world, pos, camoState, player)) {
                ItemStack silkStack = new ItemStack(camoBlock, 1, camoBlock.getMetaFromState(camoState));
                if (!silkStack.isEmpty()) {
                    Block.spawnAsEntity(world, pos, silkStack);
                }
            } else {
                int fortune = !tool.isEmpty() ? EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, tool) : 0;
                NonNullList<ItemStack> drops = NonNullList.create();
                camoBlock.getDrops(drops, world, pos, camoState, fortune);
                for (ItemStack drop : drops) {
                    if (!drop.isEmpty()) {
                        Block.spawnAsEntity(world, pos, drop);
                    }
                }
            }
        } catch (Throwable t) {
            DuctTapeCore.LOGGER.warn(
                    "[{}] MovingElevators 카모 블록 드랍 처리 중 오류가 발생했습니다 ({}, {})",
                    DuctTapeCore.NAME, pos, camoState, t
            );
        }
    }
}