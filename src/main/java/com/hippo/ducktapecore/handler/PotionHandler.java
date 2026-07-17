package com.hippo.ducktapecore.handler;

import com.hippo.ducktapecore.config.ModConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PotionHandler {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!ModConfig.blindInvisiblePlayers) return;

        EntityPlayer player = event.player;
        if (player.world.isRemote) return;

        if (player.isPotionActive(MobEffects.INVISIBILITY)) {
            player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 30, 0, false, false));
        }
    }
}