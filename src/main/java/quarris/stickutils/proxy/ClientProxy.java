package quarris.stickutils.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import quarris.stickutils.client.container.CraftingStickScreen;
import quarris.stickutils.client.container.FurnaceStickScreen;
import quarris.stickutils.common.Content;
import quarris.stickutils.common.capabilities.ItemCapability;
import quarris.stickutils.common.network.SyncItemTickingCapToClient;

public class ClientProxy implements IProxy {

	@Override
	public void setup(FMLCommonSetupEvent event) {
        System.out.println("Setup Client");
        ScreenManager.registerFactory(Content.CRAFTING_CONTAINER, CraftingStickScreen::new);
        ScreenManager.registerFactory(Content.FURNACE_CONTAINER, FurnaceStickScreen::new);
        ScreenManager.registerFactory(Content.BLAST_CONTAINER, FurnaceStickScreen::new);
        ScreenManager.registerFactory(Content.SMOKER_CONTAINER, FurnaceStickScreen::new);
	}

    @Override
    public void syncItem(PlayerEntity player, int itemSlot, CompoundNBT data, SyncItemTickingCapToClient.Type type) {
        Minecraft mc = Minecraft.getInstance();
        ItemStack stack = mc.player.inventory.getStackInSlot(itemSlot);
        if (type == SyncItemTickingCapToClient.Type.TICKING) {
            stack.getCapability(ItemCapability.tickingCapability).ifPresent(cap -> cap.deserializeNBT(data));
        } else if (type == SyncItemTickingCapToClient.Type.INVENTORY) {
            stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> ((ItemStackHandler) cap).deserializeNBT(data));

        }
    }
}
