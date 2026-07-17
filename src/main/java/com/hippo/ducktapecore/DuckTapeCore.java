package com.hippo.ducktapecore;

import com.hippo.ducktapecore.handler.InteractionHandler;
import com.hippo.ducktapecore.handler.PotionHandler;
import com.hippo.ducktapecore.restriction.RestrictionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = DuckTapeCore.MODID,
        name = DuckTapeCore.NAME,
        version = DuckTapeCore.VERSION,
        acceptedMinecraftVersions = "[1.12.2]"
)
public class DuckTapeCore {

    public static final String MODID = "ducktapecore";
    public static final String NAME = "DuckTapeCore";
    public static final String VERSION = "1.0.0";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Mod.Instance(MODID)
    public static DuckTapeCore INSTANCE;

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
