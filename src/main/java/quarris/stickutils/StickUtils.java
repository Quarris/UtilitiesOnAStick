package quarris.stickutils;

import net.minecraft.fluid.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quarris.stickutils.common.network.PacketHandler;
import quarris.stickutils.proxy.ClientProxy;
import quarris.stickutils.proxy.IProxy;
import quarris.stickutils.proxy.ServerProxy;

@Mod(ModRef.ID)
public class StickUtils {
	// Directly reference a log4j logger.
	private static final Logger LOGGER = LogManager.getLogger();

	public StickUtils() {
        ModRef.proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
	}

	private void setup(final FMLCommonSetupEvent event) {
		LOGGER.info("Setting up {} mod", ModRef.ID);
		ModRef.proxy.setup(event);
        PacketHandler.init();
	}
}
