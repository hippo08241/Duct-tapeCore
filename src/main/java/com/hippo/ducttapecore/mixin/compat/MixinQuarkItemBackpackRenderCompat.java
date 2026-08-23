package com.hippo.ducttapecore.mixin.compat;

import baubles.api.model.ModelBauble;
import baubles.api.render.IRenderBauble;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "vazkii.quark.oddities.item.ItemBackpack", remap = false)
public abstract class MixinQuarkItemBackpackRenderCompat implements IRenderBauble {

    private static final ModelBauble DUCTTAPECORE$BACKPACK_MODEL_ADAPTER = new ModelBauble.NoTex() {
        @Override
        @SideOnly(Side.CLIENT)
        public void render(RenderPlayer renderPlayer, EntityLivingBase entity, ItemStack stack,
                           float limbSwing, float limbSwingAmount, float partialTicks,
                           float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            if (entity instanceof EntityPlayer && stack.getItem() instanceof IRenderBauble) {
                ((IRenderBauble) stack.getItem()).onPlayerBaubleRender(
                        stack, (EntityPlayer) entity, IRenderBauble.RenderType.BODY, partialTicks
                );
            }
        }
    };

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBauble getModel(ItemStack stack, EntityLivingBase entity, RenderPlayer renderPlayer) {
        return DUCTTAPECORE$BACKPACK_MODEL_ADAPTER;
    }
}