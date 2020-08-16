package quarris.stickutils;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quarris.stickutils.proxy.ClientProxy;
import quarris.stickutils.proxy.IProxy;
import quarris.stickutils.proxy.ServerProxy;

@Mod(StickUtils.ID)
public class StickUtils {

    public static final String ID = "stickutils";
    public static final String NAME = "Utilities on a Stick";

	// Directly reference a log4j logger.
	private static final Logger LOGGER = LogManager.getLogger();

	public static final IProxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public static final ItemGroup GROUP = new ItemGroup(ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.STICK);
        }
    };

    public static ResourceLocation createRes(String name) {
        return new ResourceLocation(ID, name);
    }

	public StickUtils() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(PROXY::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(PROXY::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(PROXY::serverSetup);
	}
}
