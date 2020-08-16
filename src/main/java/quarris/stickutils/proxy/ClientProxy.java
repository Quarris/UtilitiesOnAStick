package quarris.stickutils.proxy;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import quarris.stickutils.client.screen.CraftingStickScreen;
import quarris.stickutils.common.Content;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements IProxy {

    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(Content.CRAFTING_STICK_CONTAINER, CraftingStickScreen::new);
    }

    @Override
	public void setup(FMLCommonSetupEvent event) {

	}
}
