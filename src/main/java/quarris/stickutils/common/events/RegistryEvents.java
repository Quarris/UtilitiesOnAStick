package quarris.stickutils.common.events;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quarris.stickutils.ModRef;
import quarris.stickutils.common.container.CraftingStickContainer;
import quarris.stickutils.common.container.furnace.BlastStickContainer;
import quarris.stickutils.common.container.furnace.FurnaceStickContainer;
import quarris.stickutils.common.container.furnace.SmokerStickContainer;
import quarris.stickutils.common.items.*;
import quarris.stickutils.common.items.FurnaceStick;

@Mod.EventBusSubscriber(modid = ModRef.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        LOGGER.info("Registering items for {}", ModRef.ID);
        event.getRegistry().registerAll(
                new CraftingStick(),
                new FurnaceStick(),
                new CatStick(),
                new CreeperStick(),
                new MobStick(),
                new BlastStick(),
                new SmokerStick()
        );
    }

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().registerAll(
                IForgeContainerType.create(CraftingStickContainer::new).setRegistryName(ModRef.createRes("crafting_container")),
                IForgeContainerType.create(FurnaceStickContainer::new).setRegistryName(ModRef.createRes("furnace_container")),
                IForgeContainerType.create(BlastStickContainer::new).setRegistryName(ModRef.createRes("blast_container")),
                IForgeContainerType.create(SmokerStickContainer::new).setRegistryName(ModRef.createRes("smoker_container"))
        );
    }
}
