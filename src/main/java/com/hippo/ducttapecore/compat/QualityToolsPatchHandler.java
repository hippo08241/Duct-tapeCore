package com.hippo.ducttapecore.compat;

import com.hippo.ducttapecore.config.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;

public class QualityToolsPatchHandler {

    private static final String QUALITY_TAG = "Quality";
    private static final int QUALITY_TICK_INTERVAL = 13;

    private static Set<String> blacklistCache = null;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();

        if (entity.world.isRemote) {
            return;
        }
        if (!(entity instanceof EntityPlayer)) {
            return;
        }
        if (entity.ticksExisted % QUALITY_TICK_INTERVAL != 0) {
            return;
        }
        if (!ModConfig.qualityToolsEnabled) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;
        boolean blockAll = ModConfig.qualityToolsBlockCreative && player.capabilities.isCreativeMode;
        Set<String> blacklist = getBlacklist();

        if (!blockAll && blacklist.isEmpty()) {
            return;
        }

        for (ItemStack stack : player.inventory.mainInventory) {
            tryBlock(stack, blockAll, blacklist);
        }
        for (ItemStack stack : player.inventory.armorInventory) {
            tryBlock(stack, blockAll, blacklist);
        }
        for (ItemStack stack : player.inventory.offHandInventory) {
            tryBlock(stack, blockAll, blacklist);
        }
    }

    private void tryBlock(ItemStack stack, boolean blockAll, Set<String> blacklist) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        if (hasQualityTag(stack)) {
            return;
        }

        boolean matchesBlacklist = false;
        if (!blacklist.isEmpty() && stack.getItem().getRegistryName() != null) {
            matchesBlacklist = blacklist.contains(stack.getItem().getRegistryName().toString());
        }

        if (blockAll || matchesBlacklist) {
            markAsNoQuality(stack);
        }
    }

    private boolean hasQualityTag(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(QUALITY_TAG, 10);
    }

    private void markAsNoQuality(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setTag(QUALITY_TAG, new NBTTagCompound());
    }

    private Set<String> getBlacklist() {
        if (blacklistCache == null) {
            blacklistCache = new HashSet<>();
            for (String entry : ModConfig.qualityToolsBlacklist) {
                if (entry != null && !entry.trim().isEmpty()) {
                    blacklistCache.add(entry.trim());
                }
            }
        }
        return blacklistCache;
    }

    public static void invalidateCache() {
        blacklistCache = null;
    }
}