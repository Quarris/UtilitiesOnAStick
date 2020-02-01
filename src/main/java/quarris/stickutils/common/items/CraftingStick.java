package quarris.stickutils.common.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.function.BiFunction;

public class CraftingStick extends UtilityStick {

    private static final BiFunction<Integer, PlayerEntity, WorkbenchContainer> CONTAINER = (id, player) -> {
        final ItemStack held;

        if (player.getHeldItemMainhand().getItem() instanceof CraftingStick) {
            held = player.getHeldItemMainhand();
        }
        else {
            held = player.getHeldItemOffhand();
        }

        WorkbenchContainer c = new WorkbenchContainer(id, player.inventory, IWorldPosCallable.of(player.world, player.getPosition())) {

            @Override
            public boolean canInteractWith(PlayerEntity playerIn) {
                return true;
            }

            @Override
            public void onContainerClosed(PlayerEntity playerIn) {
                PlayerInventory playerinventory = playerIn.inventory;
                if (!playerinventory.getItemStack().isEmpty()) {
                    playerIn.dropItem(playerinventory.getItemStack(), false);
                    playerinventory.setItemStack(ItemStack.EMPTY);
                }
                IInventory slots = this.inventorySlots.get(1).inventory;
                ListNBT nbt = new ListNBT();
                for (int i = 0; i < 9; i++) {
                    nbt.add(slots.getStackInSlot(i).write(new CompoundNBT()));
                }
                held.getOrCreateTag().put("Items", nbt);
            }

            @Override
            public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
                if (slotId > 9 && this.getSlot(slotId).getStack().getItem() instanceof CraftingStick) {
                    System.out.println("Stop!");
                    return ItemStack.EMPTY;
                }
                return super.slotClick(slotId, dragType, clickTypeIn, player);
            }




        };

        CompoundNBT tag = held.getTag();
        if (tag != null && tag.contains("Items")) {
            ListNBT nbt = tag.getList("Items", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < 9; i++) {
                c.inventorySlots.get(1).inventory.setInventorySlotContents(i, ItemStack.read(nbt.getCompound(i)));
            }
        }

        return c;
    };

    public CraftingStick() {
        super("crafting_stick");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        player.openContainer(
                new SimpleNamedContainerProvider((id, playerInv, playerEntity) ->
                        CONTAINER.apply(id, playerEntity),
                        new TranslationTextComponent("container.crafting"))
        );
        return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
    }
}
