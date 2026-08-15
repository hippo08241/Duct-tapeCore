package com.hippo.ducttapecore;

import com.hippo.ducttapecore.compat.QualityToolsPatchHandler;
import com.hippo.ducttapecore.compat.hardcorerevival.HardcoreRevivalDeathResetHandler;
import com.hippo.ducttapecore.compat.jei.QualityToolsJeiIntegration;
import com.hippo.ducttapecore.compat.sync.SyncHardcoreRevivalPatchHandler;
import com.hippo.ducttapecore.config.ModConfig;
import com.hippo.ducttapecore.debug.CommandSyncClientDebug;
import com.hippo.ducttapecore.handler.InteractionHandler;
import com.hippo.ducttapecore.handler.PotionHandler;
import com.hippo.ducttapecore.network.NetworkHandler;
import com.hippo.ducttapecore.restriction.RestrictionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = DuctTapeCore.MODID,
        name = DuctTapeCore.NAME,
        version = DuctTapeCore.VERSION,
        dependencies = "after:qualitytools;after:jei",
        acceptedMinecraftVersions = "[1.12.2]"
)
public class DuctTapeCore {

    public static final String MODID = "ducttapecore";
    public static final String NAME = "DuctTapeCore";
    public static final String VERSION = "1.1.0";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Mod.Instance(MODID)
    public static DuctTapeCore INSTANCE;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("[{}] preInit - config 로드 중...", NAME);
        ModConfig.init(event.getSuggestedConfigurationFile());
        RestrictionManager.reload();
        NetworkHandler.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("[{}] init - 이벤트 핸들러 등록", NAME);
        MinecraftForge.EVENT_BUS.register(new PotionHandler());
        MinecraftForge.EVENT_BUS.register(new InteractionHandler());

        if (Loader.isModLoaded("qualitytools")) {
            LOGGER.info("[{}] Quality Tools 감지됨 - 패치 핸들러 등록", NAME);
            MinecraftForge.EVENT_BUS.register(new QualityToolsPatchHandler());
        }

        if (Loader.isModLoaded("sync") && Loader.isModLoaded("hardcorerevival")) {
            LOGGER.info("[{}] Sync + HardcoreRevival 둘 다 감지됨 - 호환성 패치 핸들러 등록", NAME);
            MinecraftForge.EVENT_BUS.register(new SyncHardcoreRevivalPatchHandler());
        }

        if (Loader.isModLoaded("hardcorerevival")) {
            MinecraftForge.EVENT_BUS.register(new HardcoreRevivalDeathResetHandler());
        }

        if (Loader.isModLoaded("sync") && FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            LOGGER.info("[{}] Sync 감지됨 - /syncdebug 진단 커맨드 등록", NAME);
            net.minecraftforge.client.ClientCommandHandler.instance.registerCommand(new CommandSyncClientDebug());
        }
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        if (Loader.isModLoaded("qualitytools") && Loader.isModLoaded("jei")) {
            QualityToolsJeiIntegration.syncRecipes();
        }
    }
}