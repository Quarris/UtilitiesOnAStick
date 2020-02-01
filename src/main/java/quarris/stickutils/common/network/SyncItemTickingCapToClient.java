package quarris.stickutils.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import quarris.stickutils.common.capabilities.ItemCapability;

import java.util.function.Supplier;

public class SyncItemTickingCapToClient {

    public int itemSlot;
    public CompoundNBT data;
    public Type type;

    public SyncItemTickingCapToClient(int itemSlot, CompoundNBT data, Type type) {
        this.itemSlot = itemSlot;
        this.data = data;
        this.type = type;
    }

    public static void encode(SyncItemTickingCapToClient msg, PacketBuffer buffer) {
        buffer.writeInt(msg.itemSlot);
        buffer.writeCompoundTag(msg.data);
        buffer.writeByte(msg.type.ordinal());
    }

    public static SyncItemTickingCapToClient decode(PacketBuffer buffer) {
        return new SyncItemTickingCapToClient(buffer.readInt(), buffer.readCompoundTag(), Type.values()[buffer.readByte()]);
    }

    public static void handle(SyncItemTickingCapToClient msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            ItemStack stack = mc.player.inventory.getStackInSlot(msg.itemSlot);
            if (msg.type == Type.TICKING) {
                stack.getCapability(ItemCapability.tickingCapability).ifPresent(cap -> cap.deserializeNBT(msg.data));
            } else if (msg.type == Type.INVENTORY) {
                stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> ((ItemStackHandler) cap).deserializeNBT(msg.data));

            }
        });
        ctx.get().setPacketHandled(true);
    }

    public enum Type {
        INVENTORY,
        TICKING
    }

}
