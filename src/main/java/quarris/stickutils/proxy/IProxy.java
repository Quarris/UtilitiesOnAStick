package quarris.stickutils.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import quarris.stickutils.common.network.SyncItemTickingCapToClient;

public interface IProxy {

	void setup(FMLCommonSetupEvent event);

	void syncItem(PlayerEntity player, int itemSlot, CompoundNBT data, SyncItemTickingCapToClient.Type type);
}
