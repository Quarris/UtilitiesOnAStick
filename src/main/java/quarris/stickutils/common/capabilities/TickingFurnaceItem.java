package quarris.stickutils.common.capabilities;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import quarris.stickutils.ModRef;
import quarris.stickutils.common.container.furnace.AbstractFurnaceStickContainer;
import quarris.stickutils.common.items.base.ContainerStick;
import quarris.stickutils.common.network.SyncItemTickingCapToClient;

import javax.annotation.Nullable;

public class TickingFurnaceItem implements ITickingItemHandler {

    private final ItemStack stack;

    private IInventory inputAsInv;

    private IRecipeType<? extends AbstractCookingRecipe> recipeType;
    private Class<? extends AbstractFurnaceStickContainer> clazz;

    private int burnTime;
    private int burnTimeTotal;
    private int cookTime;
    private int cookTimeTotal;

    public TickingFurnaceItem(ItemStack stack, IRecipeType<? extends AbstractCookingRecipe> recipeType, Class<? extends AbstractFurnaceStickContainer> clazz) {
        this.stack = stack;
        this.recipeType = recipeType;
        this.clazz = clazz;
    }

    @Override
    public ItemStack getStack() {
        return this.stack;
    }

    @Override
    public void tick(World world, boolean isEntity, Object... data) {
        stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
            this.inputAsInv = new Inventory(cap.getStackInSlot(0));
            if (this.isBurning()) {
                --this.burnTime;
            }

            if (!world.isRemote) {
                ItemStack fuel = cap.getStackInSlot(1);
                if (this.isBurning() || !fuel.isEmpty() && !cap.getStackInSlot(0).isEmpty()) {
                    IRecipe<?> irecipe = world.getRecipeManager().getRecipe(this.recipeType, this.inputAsInv, world).orElse(null);
                    if (!this.isBurning() && this.canSmelt(irecipe, (ItemStackHandler) cap)) {
                        this.burnTime = ForgeHooks.getBurnTime(fuel);
                        this.burnTimeTotal = this.burnTime;
                        this.cookTimeTotal = this.getCookTime(world);
                        if (this.isBurning()) {
                            if (fuel.hasContainerItem())
                                cap.insertItem(1, fuel.getContainerItem(), false);
                            else
                            if (!fuel.isEmpty()) {
                                fuel.shrink(1);
                                if (fuel.isEmpty()) {
                                    cap.insertItem(1, fuel.getContainerItem(), false);
                                }
                            }
                        }
                    }

                    if (this.isBurning() && this.canSmelt(irecipe, (ItemStackHandler) cap)) {
                        ++this.cookTime;
                        if (this.cookTime == this.cookTimeTotal) {
                            this.cookTime = 0;
                            this.cookTimeTotal = this.getCookTime(world);
                            this.outputRecipe(irecipe, world, (ItemStackHandler) cap);
                            if (!isEntity) {
                                ServerPlayerEntity player = (ServerPlayerEntity) data[1];
                                if (player.openContainer.getClass() == this.clazz) {
                                    ContainerStick.loadInventory(this.getStack(), ((AbstractFurnaceStickContainer) player.openContainer).furnaceInventory);
                                }
                                ModRef.proxy.syncItem(player, (int) data[0], ((ItemStackHandler) cap).serializeNBT(), SyncItemTickingCapToClient.Type.INVENTORY);
                            }
                        }
                    } else {
                        this.cookTime = 0;
                    }
                } else if (!this.isBurning() && this.cookTime > 0) {
                    this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
                }

                if (this.isBurning()) {
                    if (!isEntity) {
                        ModRef.proxy.syncItem((PlayerEntity) data[1], (int) data[0], this.serializeNBT(), SyncItemTickingCapToClient.Type.TICKING);
                    }
                }
            }
        });
    }

    public int getBurnTime() {
        return this.burnTime;
    }

    public int getBurnTimeTotal() {
        return this.burnTimeTotal;
    }

    public int getCookTime() {
        return this.cookTime;
    }

    public int getCookTimeTotal() {
        return this.cookTimeTotal;
    }

    @Override
    public boolean isActive() {
        return this.burnTime > 0;
    }

    private void outputRecipe(@Nullable IRecipe<?> p_214007_1_, World world, ItemStackHandler cap) {
        if (p_214007_1_ != null && this.canSmelt(p_214007_1_, cap)) {
            ItemStack input = cap.getStackInSlot(0);
            ItemStack recipeOutput = p_214007_1_.getRecipeOutput();
            ItemStack output = cap.getStackInSlot(2);
            if (output.isEmpty()) {
                cap.setStackInSlot(2, recipeOutput.copy());
            } else if (output.getItem() == recipeOutput.getItem()) {
                output.grow(recipeOutput.getCount());
            }

            if (!world.isRemote) {
                //this.setRecipeUsed(p_214007_1_);
            }

            if (input.getItem() == Blocks.WET_SPONGE.asItem() && !cap.getStackInSlot(1).isEmpty() && cap.getStackInSlot(1).getItem() == Items.BUCKET) {
                cap.setStackInSlot(1, new ItemStack(Items.WATER_BUCKET));
            }

            input.shrink(1);
        }
    }

    protected int getCookTime(World world) {
        return world.getRecipeManager().getRecipe(this.recipeType, this.inputAsInv, world).map(AbstractCookingRecipe::getCookTime).orElse(200);
    }

    protected boolean canSmelt(@Nullable IRecipe<?> recipeIn, ItemStackHandler cap) {
        if (!cap.getStackInSlot(0).isEmpty() && recipeIn != null) {
            ItemStack itemstack = recipeIn.getRecipeOutput();
            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack itemstack1 = cap.getStackInSlot(2);
                if (itemstack1.isEmpty()) {
                    return true;
                } else if (!itemstack1.isItemEqual(itemstack)) {
                    return false;
                } else if (itemstack1.getCount() + itemstack.getCount() <= cap.getSlotLimit(2) && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) { // Forge fix: make furnace respect stack sizes in furnace recipes
                    return true;
                } else {
                    return itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); // Forge fix: make furnace respect stack sizes in furnace recipes
                }
            }
        } else {
            return false;
        }
    }

    public boolean isBurning() {
        return this.burnTime > 0;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("BurnTime", this.burnTime);
        nbt.putInt("BurnTimeTotal", this.burnTimeTotal);
        nbt.putInt("CookTime", this.cookTime);
        nbt.putInt("CookTimeTotal", this.cookTimeTotal);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.burnTime = nbt.getInt("BurnTime");
        this.burnTimeTotal = nbt.getInt("BurnTimeTotal");
        this.cookTime = nbt.getInt("CookTime");
        this.cookTimeTotal = nbt.getInt("CookTimeTotal");
    }
}
