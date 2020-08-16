package quarris.stickutils.proxy;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;

public interface IProxy {

    default void clientSetup(FMLClientSetupEvent event) {}
    default void serverSetup(FMLDedicatedServerSetupEvent event) {}
	void setup(FMLCommonSetupEvent event);
}
