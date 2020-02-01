package quarris.stickutils.common.capabilities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public interface ITickingItemHandler extends INBTSerializable<CompoundNBT> {

    ItemStack getStack();

    void tick(World world, boolean isEntity, Object... data);

    boolean isActive();
}
