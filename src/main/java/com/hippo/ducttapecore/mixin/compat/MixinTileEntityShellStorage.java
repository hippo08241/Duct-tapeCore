package com.hippo.ducttapecore.mixin.compat;

import com.hippo.ducttapecore.config.ModConfig;
import me.ichun.mods.sync.common.Sync;
import me.ichun.mods.sync.common.shell.ShellHandler;
import me.ichun.mods.sync.common.tileentity.TileEntityDualVertical;
import me.ichun.mods.sync.common.tileentity.TileEntityShellStorage;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Sync의 Shell Storage(복제인간 저장고) 버그 수정.
 * <p>
 * 1. 월드/청크를 불러온 직후 첫 틱에는 아직 전력이 들어오지 않아 powReceived가 0이다. Sync는 이를 전력 끊김으로 보고
 * removeShell -> reset()을 호출해 저장된 복제인간을 지워 버린다 (재접속하면 복제인간이 사라지는 원인).
 * 로드 직후 잠시 동안은 전력이 부족해도 유지되게 한다. 그 뒤에도 전력이 없으면 Sync 원래 동작대로 처리된다.
 * 2. 재시작 후 전력이 계속 켜져 있던 저장고가 ShellHandler에 다시 등록되지 않는 문제 -> 등록되어 있지 않으면 등록한다.
 * <p>
 * 부모 클래스(TileEntityDualVertical)를 상속해서 그 필드에 직접 접근한다. update는 바닐라 메서드(ITickable)라 remap이 필요하다.
 */
@Mixin(value = TileEntityShellStorage.class, remap = false)
public abstract class MixinTileEntityShellStorage extends TileEntityDualVertical<TileEntityShellStorage> {

    @Unique
    private static final int DUCTTAPECORE$LOAD_GRACE_TICKS = 100;

    @Unique
    private int ducttapecore$ticksSinceLoad;

    @Inject(method = "update", at = @At("HEAD"), remap = true)
    private void ducttapecore$keepShellWhilePowerWarmsUp(CallbackInfo ci) {
        if (!ModConfig.syncFixShellStorageReregistration) {
            return;
        }
        if (this.ducttapecore$ticksSinceLoad >= DUCTTAPECORE$LOAD_GRACE_TICKS) {
            return;
        }
        World world = this.getWorld();
        if (world == null || world.isRemote || this.top) {
            return;
        }
        this.ducttapecore$ticksSinceLoad++;

        TileEntityShellStorage self = (TileEntityShellStorage) (Object) this;
        int required = Sync.config.shellStoragePowerRequirement;
        if (self.hasPower && this.powerAmount() < required) {
            // 이번 틱의 전력 부족 판정만 통과시킨다. Sync가 update 끝에서 powReceived를 0으로 되돌린다.
            this.powReceived = required;
        }
    }

    @Inject(method = "update", at = @At("TAIL"), remap = true)
    private void ducttapecore$reregisterShellIfMissing(CallbackInfo ci) {
        if (!ModConfig.syncFixShellStorageReregistration) {
            return;
        }

        World world = this.getWorld();
        if (world == null || world.isRemote || this.top) {
            return;
        }

        TileEntityShellStorage self = (TileEntityShellStorage) (Object) this;
        String name = this.getPlayerName();
        if (!self.hasPower || name == null || name.isEmpty()) {
            return;
        }

        if (!ShellHandler.isShellAlreadyRegistered(self)) {
            ShellHandler.addShell(name, self, true);
        }
    }
}
