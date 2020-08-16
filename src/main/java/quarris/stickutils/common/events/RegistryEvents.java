package quarris.stickutils.common.events;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quarris.stickutils.StickUtils;

@Mod.EventBusSubscriber(modid = StickUtils.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {

	private static final Logger LOGGER = LogManager.getLogger();

}
