package quarris.stickutils.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import quarris.stickutils.common.items.base.ContainerStick;

public abstract class StickRecipeBookContainer<C extends IInventory> extends RecipeBookContainer<C> implements IStickContainer {

    protected final PlayerEntity player;
    protected final World world;
    protected final boolean isOffhand;

    public StickRecipeBookContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, boolean isOffhand) {
        super(type, id);
        this.player = playerInventory.player;
        this.world = this.player.world;
        this.isOffhand = isOffhand;
    }

    public StickRecipeBookContainer(ContainerType<?> type, int id, PlayerInventory playerInventory, PacketBuffer data) {
        this(type, id, playerInventory, data.readBoolean());
    }

    public void initContainer() {
        ItemStack held = isOffhand ? player.getHeldItemOffhand() : player.getHeldItemMainhand();
        if (held.getItem() instanceof ContainerStick) {
            ContainerStick.loadInventory(held, this.getStickInventory());
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        ItemStack held = isOffhand ? playerIn.getHeldItemOffhand() : playerIn.getHeldItemMainhand();
        if (held.getItem() instanceof ContainerStick) {
            ContainerStick.saveInventory(held, this.getStickInventory());
        }
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        // Make sure you cannot swap item with the container stick.
        if (!isOffhand && clickTypeIn == ClickType.SWAP && dragType == player.inventory.currentItem) {
            return ItemStack.EMPTY;
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 0) {
                itemstack1.getItem().onCreated(itemstack1, this.world, playerIn);
                if (!this.mergeItemStack(itemstack1, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index >= 10 && index < 37) {
                if (!this.mergeItemStack(itemstack1, 37, 46, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 37 && index < 46) {
                if (!this.mergeItemStack(itemstack1, 10, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 10, 46, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);
            if (index == 0) {
                playerIn.dropItem(itemstack2, false);
            }
        }

        return itemstack;
    }
}
