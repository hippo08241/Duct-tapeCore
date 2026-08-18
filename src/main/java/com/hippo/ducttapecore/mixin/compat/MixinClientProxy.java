package com.hippo.ducttapecore.mixin.compat;

import com.hippo.ducttapecore.compat.hardcorerevival.HardcoreRevivalClientTickHandler;
import com.hippo.ducttapecore.config.ModConfig;
import net.blay09.mods.hardcorerevival.client.ClientProxy;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.client.event.GuiScreenEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientProxy.class, remap = false)
public abstract class MixinClientProxy {

    @Shadow
    private GuiButton buttonDie;

    @Inject(method = "onActionPerformed", at = @At("TAIL"))
    private void ducttapecore$scheduleScreenCloseAfterAcceptingDeath(GuiScreenEvent.ActionPerformedEvent.Pre event, CallbackInfo ci) {
        if (!ModConfig.hardcoreRevivalEnabled) {
            return;
        }
        if (event.getButton() != null && event.getButton() == this.buttonDie) {
            HardcoreRevivalClientTickHandler.scheduleScreenClose();
        }
    }
}