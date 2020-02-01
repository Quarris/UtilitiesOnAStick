package quarris.stickutils.common.capabilities;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class ItemCapability {

    @CapabilityInject(ITickingItemHandler.class)
    public static Capability<ITickingItemHandler> tickingCapability = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(ITickingItemHandler.class, new Capability.IStorage<ITickingItemHandler>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<ITickingItemHandler> capability, ITickingItemHandler instance, Direction side) {
                return null;
            }

            @Override
            public void readNBT(Capability<ITickingItemHandler> capability, ITickingItemHandler instance, Direction side, INBT nbt) {

            }
        }, () -> null);
    }
}
