package quarris.stickutils;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quarris.stickutils.proxy.ClientProxy;
import quarris.stickutils.proxy.IProxy;
import quarris.stickutils.proxy.ServerProxy;

@Mod(ModRef.ID)
public class StickUtils {
	// Directly reference a log4j logger.
	private static final Logger LOGGER = LogManager.getLogger();

	public static IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

	public StickUtils() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
	}

	private void setup(final FMLCommonSetupEvent event) {
		LOGGER.info("Setting up {} mod", ModRef.ID);
		proxy.setup(event);
	}
}
