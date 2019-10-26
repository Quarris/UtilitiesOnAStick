package quarris.stickutils.common.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class CraftingStick extends UtilityStick {

    public CraftingStick() {
        super("crafting_stick");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        player.openContainer(
                new SimpleNamedContainerProvider((id, playerInv, p_220270_4_) ->
                        new WorkbenchContainer(id, playerInv, IWorldPosCallable.of(world, player.getPosition())) {
                            @Override
                            public boolean canInteractWith(PlayerEntity playerIn) {
                                return true;
                            }
                        },
                        new TranslationTextComponent("container.crafting"))
        );
        return ActionResult.newResult(ActionResultType.SUCCESS, player.getHeldItem(hand));
    }
}
