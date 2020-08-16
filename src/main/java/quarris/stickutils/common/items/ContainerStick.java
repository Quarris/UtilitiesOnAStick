package quarris.stickutils.common.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class ContainerStick extends UtilityStick {

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote) {
            NetworkHooks.openGui((ServerPlayerEntity) playerIn, this.createContainer(playerIn));
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    public abstract INamedContainerProvider createContainer(PlayerEntity playerEntity);
}
