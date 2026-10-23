package com.hippo.ducttapecore;

import com.hippo.ducttapecore.compat.QualityToolsPatchHandler;
import com.hippo.ducttapecore.compat.architecturecraft.ArchitectureCraftParticleFixHandler;
import com.hippo.ducttapecore.compat.cyberware.CyberwareDefibrillatorHandler;
import com.hippo.ducttapecore.compat.hardcorerevival.HardcoreRevivalClientTickHandler;
import com.hippo.ducttapecore.compat.hardcorerevival.HardcoreRevivalDeathResetHandler;
import com.hippo.ducttapecore.compat.jei.QualityToolsJeiIntegration;
import com.hippo.ducttapecore.compat.movingelevators.MovingElevatorsCamoDropHandler;
import com.hippo.ducttapecore.compat.sync.SyncHardcoreRevivalPatchHandler;
import com.hippo.ducttapecore.config.ModConfig;
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
        dependencies = "after:qualitytools;after:jei;after:cyberware",
        guiFactory = "com.hippo.ducttapecore.config.ModConfigGuiFactory",
        acceptedMinecraftVersions = "[1.12.2]"
)
public class DuctTapeCore {

    public static final String MODID = "ducttapecore";
    public static final String NAME = "DuctTapeCore";
    public static final String VERSION = Reference.VERSION;

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Mod.Instance(MODID)
    public static DuctTapeCore INSTANCE;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("[{}] preInit - config 로드 중...", NAME);
        ModConfig.init(event.getSuggestedConfigurationFile());
        NetworkHandler.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // 블록 레지스트리 이벤트는 preInit 이후에 발생하므로, 다른 모드 블록까지 찾으려면 init에서 로드해야 한다.
        RestrictionManager.reload();

        LOGGER.info("[{}] init - 이벤트 핸들러 등록", NAME);
        MinecraftForge.EVENT_BUS.register(new PotionHandler());
        MinecraftForge.EVENT_BUS.register(new InteractionHandler());

        if (Loader.isModLoaded("qualitytools")) {
            LOGGER.info("[{}] Quality Tools 감지됨 - 패치 핸들러 등록", NAME);
            MinecraftForge.EVENT_BUS.register(new QualityToolsPatchHandler());
        }

        if (Loader.isModLoaded("architecturecraft")) {
            LOGGER.info("[{}] ArchitectureCraft 감지됨 - 파티클 텍스처 패치 핸들러 등록", NAME);
            if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
                MinecraftForge.EVENT_BUS.register(new ArchitectureCraftParticleFixHandler());
            }
        }

        if (Loader.isModLoaded("sync") && Loader.isModLoaded("hardcorerevival")) {
            LOGGER.info("[{}] Sync + HardcoreRevival 둘 다 감지됨 - 호환성 패치 핸들러 등록", NAME);
            MinecraftForge.EVENT_BUS.register(new SyncHardcoreRevivalPatchHandler());
        }

        if (Loader.isModLoaded("cyberware") && Loader.isModLoaded("hardcorerevival")) {
            LOGGER.info("[{}] Robotic Parts + HardcoreRevival 둘 다 감지됨 - 심장 재세동기 호환성 핸들러 등록", NAME);
            MinecraftForge.EVENT_BUS.register(new CyberwareDefibrillatorHandler());
        }

        if (Loader.isModLoaded("hardcorerevival")) {
            LOGGER.info("[{}] HardcoreRevival 감지됨 - 범용 사망 리셋 핸들러 등록", NAME);
            MinecraftForge.EVENT_BUS.register(new HardcoreRevivalDeathResetHandler());

            if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
                MinecraftForge.EVENT_BUS.register(new HardcoreRevivalClientTickHandler());
            }
        }

        if (Loader.isModLoaded("movingelevators")) {
            LOGGER.info("[{}] MovingElevators 감지됨 - 카모 블록 드랍 핸들러 등록", NAME);
            MinecraftForge.EVENT_BUS.register(new MovingElevatorsCamoDropHandler());
        }
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        if (Loader.isModLoaded("qualitytools") && Loader.isModLoaded("jei")) {
            QualityToolsJeiIntegration.syncRecipes();
        }
    }
}