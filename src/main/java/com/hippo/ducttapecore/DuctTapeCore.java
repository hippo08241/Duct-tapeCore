package com.hippo.ducttapecore;

import com.hippo.ducttapecore.handler.InteractionHandler;
import com.hippo.ducttapecore.handler.PotionHandler;
import com.hippo.ducttapecore.restriction.RestrictionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = DuctTapeCore.MODID,
        name = DuctTapeCore.NAME,
        version = DuctTapeCore.VERSION,
        acceptedMinecraftVersions = "[1.12.2]"
)
public class DuctTapeCore {

    public static final String MODID = "ducttapecore";
    public static final String NAME = "DuctTapeCore";
    public static final String VERSION = "1.0.0";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Mod.Instance(MODID)
    public static DuctTapeCore INSTANCE;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("[{}] preInit - config 로드 중...", NAME);
        RestrictionManager.reload();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("[{}] init - 이벤트 핸들러 등록", NAME);
        MinecraftForge.EVENT_BUS.register(new PotionHandler());
        MinecraftForge.EVENT_BUS.register(new InteractionHandler());
    }
}
