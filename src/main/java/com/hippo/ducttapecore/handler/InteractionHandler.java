package com.hippo.ducttapecore.handler;

import com.hippo.ducttapecore.config.ModConfig;
import com.hippo.ducttapecore.restriction.RestrictionManager;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class InteractionHandler {

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        Block block = world.getBlockState(event.getPos()).getBlock();
        int dim = world.provider.getDimension();

        String message = RestrictionManager.getMessage(dim, block);
        if (message == null) {
            return; // 제한 대상 아님 -> 정상 진행
        }

        event.setCanceled(true);
        event.setUseBlock(Event.Result.DENY);
        event.setUseItem(Event.Result.DENY);

        if (!world.isRemote) {
            EntityPlayer player = event.getEntityPlayer();
            if (player != null) {
                player.sendStatusMessage(new TextComponentTranslation(message), ModConfig.useActionBar);
            }
        }
    }
}
