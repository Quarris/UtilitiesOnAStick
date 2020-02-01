package quarris.stickutils.common.container.furnace;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import quarris.stickutils.common.capabilities.ItemCapability;
import quarris.stickutils.common.capabilities.TickingFurnaceItem;
import quarris.stickutils.common.container.StickRecipeBookContainer;
import quarris.stickutils.common.container.slots.FuelSlot;
import quarris.stickutils.common.container.slots.ImmovableSlot;
import quarris.stickutils.common.container.slots.ResultSlot;

public class AbstractFurnaceStickContainer extends StickRecipeBookContainer<IInventory> {
    public final IInventory furnaceInventory = new Inventory(3);
    protected final World world;
    private final IRecipeType<AbstractCookingRecipe> recipeType;
    private int stickSlot;

    public AbstractFurnaceStickContainer(ContainerType type, int id, PlayerInventory playerInventory, PacketBuffer data, IRecipeType recipeType) {
        this(type, id, playerInventory, data.readBoolean(), recipeType);
    }

    public AbstractFurnaceStickContainer(ContainerType type, int id, PlayerInventory playerInventory, boolean isOffhand, IRecipeType recipeType) {
        super(type, id, playerInventory, isOffhand);
        this.world = playerInventory.player.world;
        this.recipeType = recipeType;
        if (PlayerInventory.isHotbar(playerInventory.currentItem)) {
            this.stickSlot = playerInventory.currentItem;
        }

        // Furnace Slots
        this.addSlot(new Slot(this.furnaceInventory, 0, 56, 17));
        this.addSlot(new FuelSlot(this.furnaceInventory, 1, 56, 53));
        this.addSlot(new ResultSlot(playerInventory.player, this.furnaceInventory, 2, 116, 35));

        // Player Inventory
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Player Hotbar
        for (int i = 0; i < 9; ++i) {
            if (this.stickSlot == i) {
                this.addSlot(new ImmovableSlot(playerInventory, i, 8 + i * 18, 142));
            } else {
                this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
            }
        }

        this.initContainer();
    }

    @Override
    public void func_201771_a(RecipeItemHelper recipeBookHelper) {
        for (int i = 0; i < this.furnaceInventory.getSizeInventory(); i++) {
            recipeBookHelper.accountStack(this.furnaceInventory.getStackInSlot(i));
        }
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.furnaceInventory.getSizeInventory(); i++) {
            this.furnaceInventory.setInventorySlotContents(i, ItemStack.EMPTY);
        }
    }

    @Override
    public void func_217056_a(boolean placeAll, IRecipe<?> recipe, ServerPlayerEntity player) {
        new ServerRecipePlacerFurnace<>(this).place(player, (IRecipe<IInventory>)recipe, placeAll);
    }

    @Override
    public boolean matches(IRecipe<? super IInventory> recipeIn) {
        return recipeIn.matches(this.furnaceInventory, this.world);
    }

    @Override
    public int getOutputSlot() {
        return 2;
    }

    @Override
    public int getWidth() {
        return 1;
    }

    @Override
    public int getHeight() {
        return 1;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public int getSize() {
        return 3;
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 2) {
                if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index != 1 && index != 0) {
                if (this.isValidRecipe(itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.isFuel(itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 3 && index < 30) {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
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

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    protected boolean isValidRecipe(ItemStack stack) {
        return this.world.getRecipeManager().getRecipe(this.recipeType, new Inventory(stack), this.world).isPresent();
    }

    protected boolean isFuel(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack) > 0;
    }

    @OnlyIn(Dist.CLIENT)
    public int getCookProgressionScaled() {
        TickingFurnaceItem furnace = (TickingFurnaceItem) this.player.inventory.getStackInSlot(this.stickSlot).getCapability(ItemCapability.tickingCapability).orElse(null);

        int i = furnace.getCookTime();
        int j = furnace.getCookTimeTotal();
        return j != 0 && i != 0 ? i * 24 / j : 0;
    }

    @OnlyIn(Dist.CLIENT)
    public int getBurnLeftScaled() {
        TickingFurnaceItem furnace = (TickingFurnaceItem) this.player.inventory.getStackInSlot(this.stickSlot).getCapability(ItemCapability.tickingCapability).orElse(null);

        int i = furnace.getBurnTimeTotal();
        if (i == 0) {
            i = 200;
        }

        return furnace.getBurnTime() * 13 / i;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isActive() {
        TickingFurnaceItem furnace = (TickingFurnaceItem) this.player.inventory.getStackInSlot(this.stickSlot).getCapability(ItemCapability.tickingCapability).orElse(null);

        return furnace.isActive();
    }

    @Override
    public IInventory getStickInventory() {
        return this.furnaceInventory;
    }
}
