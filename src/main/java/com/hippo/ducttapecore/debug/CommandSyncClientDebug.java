package com.hippo.ducttapecore.debug;

import com.hippo.ducttapecore.DuctTapeCore;
import me.ichun.mods.sync.common.Sync;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandSyncClientDebug extends CommandBase {

    @Override
    public String getName() {
        return "syncdebug";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/syncdebug";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Sync Client Debug ===\n");

        sb.append("zoom=").append(Sync.eventHandlerClient.zoom)
                .append(" zoomDeath=").append(Sync.eventHandlerClient.zoomDeath)
                .append(" zoomTimer=").append(Sync.eventHandlerClient.zoomTimer)
                .append(" zoomTimeout=").append(Sync.eventHandlerClient.zoomTimeout)
                .append(" zoomDimension=").append(Sync.eventHandlerClient.zoomDimension)
                .append('\n');

        sb.append("lockTime=").append(Sync.eventHandlerClient.lockTime)
                .append(" lockedStorage=").append(Sync.eventHandlerClient.lockedStorage)
                .append('\n');

        sb.append("forceRender=").append(Sync.eventHandlerClient.forceRender)
                .append(" refusePlayerRender=").append(Sync.eventHandlerClient.refusePlayerRender)
                .append('\n');

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null) {
            sb.append("player.isDead=").append(mc.player.isDead)
                    .append(" health=").append(mc.player.getHealth())
                    .append(" dimension=").append(mc.player.dimension)
                    .append(" pos=").append(mc.player.getPositionVector())
                    .append('\n');
            sb.append("currentScreen=").append(mc.currentScreen)
                    .append(" isGamePaused=").append(mc.isGamePaused())
                    .append('\n');
        }

        for (String line : sb.toString().split("\n")) {
            sender.sendMessage(new TextComponentString(line));
        }
        DuctTapeCore.LOGGER.info("[SyncDebug]\n{}", sb);
    }
}