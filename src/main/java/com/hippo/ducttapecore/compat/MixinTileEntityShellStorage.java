package com.hippo.ducttapecore.mixin.compat;

import com.hippo.ducttapecore.config.ModConfig;
import me.ichun.mods.sync.common.shell.ShellHandler;
import me.ichun.mods.sync.common.tileentity.TileEntityShellStorage;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityShellStorage.class, remap = false)
public abstract class MixinTileEntityShellStorage {

    @Shadow
    protected String playerName;

    @Shadow
    public boolean hasPower;

    @Shadow
    public abstract World getWorld();

    @Inject(method = "update", at = @At("TAIL"))
    private void ducttapecore$reregisterShellIfMissing(CallbackInfo ci) {
        if (!ModConfig.syncFixShellStorageReregistration) {
            return;
        }

        World world = getWorld();
        if (world == null || world.isRemote) {
            return;
        }
        if (!hasPower || playerName == null || playerName.equalsIgnoreCase("")) {
            return;
        }

        TileEntityShellStorage self = (TileEntityShellStorage) (Object) this;
        if (!ShellHandler.isShellAlreadyRegistered(self)) {
            ShellHandler.addShell(playerName, self, true);
        }
    }
}