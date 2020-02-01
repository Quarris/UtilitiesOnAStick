package quarris.stickutils.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import quarris.stickutils.common.network.PacketHandler;
import quarris.stickutils.common.network.SyncItemTickingCapToClient;

public class ServerProxy implements IProxy {
	@Override
	public void setup(FMLCommonSetupEvent event) {
        System.out.println("setup server");
	}

    @Override
    public void syncItem(PlayerEntity player, int itemSlot, CompoundNBT data, SyncItemTickingCapToClient.Type type) {
        System.out.println("Sync");
        PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new SyncItemTickingCapToClient(itemSlot, data, type));
    }
}
