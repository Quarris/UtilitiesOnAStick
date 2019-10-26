package quarris.stickutils.common;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quarris.stickutils.ModRef;
import quarris.stickutils.common.items.CraftingStick;
import quarris.stickutils.common.items.UtilityStick;

@Mod.EventBusSubscriber(modid = ModRef.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {

	private static final Logger LOGGER = LogManager.getLogger();

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		LOGGER.info("Registering items for {}", ModRef.ID);
		event.getRegistry().registerAll(
				new CraftingStick()
		);
	}
}
