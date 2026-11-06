package me.atlas.bg2permfix;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(BG2PermFix.MODID)
public class BG2PermFix {
    public static final String MODID = "bg2permfix";
    private static final Logger LOGGER = LogUtils.getLogger();

    public BG2PermFix() {
        LOGGER.info("BG2 Permission Fix loaded; BuildingUtils permission checks will use real placement positions (BG2 issue #214)");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Setup {
        @SubscribeEvent
        public static void onCommonSetup(FMLCommonSetupEvent event) {
            // Force the target class to load now so a mixin application failure
            // (e.g. an incompatible BG2 version) crashes at startup with a clear
            // error instead of mid-game on first gadget use.
            try {
                Class.forName("com.direwolf20.buildinggadgets2.util.BuildingUtils", false,
                        BG2PermFix.class.getClassLoader());
                LOGGER.info("BG2 Permission Fix: BuildingUtils patched successfully");
            } catch (Throwable t) {
                throw new IllegalStateException("BG2 Permission Fix could not patch Building Gadgets 2 - incompatible BG2 version?", t);
            }
        }
    }
}
