package quarris.stickutils.common.items;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.FurnaceContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import quarris.stickutils.ModRef;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class FurnaceStick extends UtilityStick {

    private static final BiFunction<Integer, PlayerEntity, FurnaceContainer> CONTAINER = (id, player) -> {
        final ItemStack held;
        if (player.getHeldItemMainhand().getItem() instanceof FurnaceStick) {
            held = player.getHeldItemMainhand();
        } else {
            held = player.getHeldItemOffhand();
        }

        IInventory inventory = new Inventory(3);
        NonNullList<ItemStack> items = loadItemsFromStack(held);

        for (int i = 0; i < 3; i++) {
            inventory.setInventorySlotContents(i, items.get(i));
        }

        IIntArray data = new IntArray(4);
        data.set(0, getStackBurnTime(held));
        data.set(1, 0);
        data.set(2, getStackCookTime(held));
        data.set(3, getStackCookTimeTotal(held));


        FurnaceContainer c = new FurnaceContainer(id, player.inventory, inventory, data) {
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
                IInventory slots = this.inventorySlots.get(0).inventory;
                ListNBT nbt = new ListNBT();
                for (int i = 0; i < 3; i++) {
                    nbt.add(slots.getStackInSlot(i).write(new CompoundNBT()));
                }
                held.getOrCreateTag().put("Items", nbt);
            }

            @Override
            public void onCraftMatrixChanged(IInventory inventoryIn) {
                super.onCraftMatrixChanged(inventoryIn);
            }

            @Override
            public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
                IInventory slots = this.inventorySlots.get(0).inventory;
                ListNBT nbt = new ListNBT();
                for (int i = 0; i < 3; i++) {
                    nbt.add(slots.getStackInSlot(i).write(new CompoundNBT()));
                }
                held.getOrCreateTag().put("Items", nbt);
                return super.transferStackInSlot(playerIn, index);
            }

            @Override
            public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
                if (slotId < 3) {
                    IInventory slots = this.inventorySlots.get(0).inventory;
                    ListNBT nbt = new ListNBT();
                    for (int i = 0; i < 3; i++) {
                        nbt.add(slots.getStackInSlot(i).write(new CompoundNBT()));
                    }
                    held.getOrCreateTag().put("Items", nbt);
                }
                return super.slotClick(slotId, dragType, clickTypeIn, player);
            }

            @Override
            public void putStackInSlot(int slotID, ItemStack stack) {
                System.out.println("Put stack in slot");
                super.putStackInSlot(slotID, stack);
            }
        };

        CompoundNBT tag = held.getTag();
        if (tag != null && tag.contains("Items")) {
            ListNBT nbt = tag.getList("Items", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < 3; i++) {
                c.inventorySlots.get(1).inventory.setInventorySlotContents(i, ItemStack.read(nbt.getCompound(i)));
            }
        }

        return c;
    };

    public FurnaceStick() {
        super("furnace_stick");
        this.addPropertyOverride(ModRef.createRes("on"), (itemStack, world, entity) -> {
            if (isBurning(itemStack)) {
                return 1;
            }
            return 0;
        });
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand hand) {
        player.openContainer(
                new SimpleNamedContainerProvider((id, playerInv, playerEntity) ->
                        CONTAINER.apply(id, playerEntity),
                        new TranslationTextComponent("container.furnace"))
        );
        return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        tick(worldIn, stack);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        tick(entity.world, stack);
        return false;
    }

    private static void tick(World world, ItemStack stack) {
        int burnTime = getStackBurnTime(stack);
        int cookTime = getStackCookTime(stack);
        int cookTimeTotal = getStackCookTimeTotal(stack);

        NonNullList<ItemStack> items = loadItemsFromStack(stack);
        System.out.println(items);
        boolean slotsChanged = false;

        if (isBurning(stack)) {
            burnTime--;
        }

        ItemStack itemstack = items.get(1);
        if (isBurning(stack) || !itemstack.isEmpty() && !items.get(0).isEmpty()) {  // If should burn
            IRecipe<?> irecipe = world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(itemstack), world).orElse(null);
            if (!isBurning(stack) && canSmelt(irecipe, items)) {
                burnTime = itemstack.getItem().getBurnTime(itemstack);
                if (isBurning(stack)) {
                    if (itemstack.hasContainerItem()) {
                        items.set(1, itemstack.getContainerItem());
                    } else if (!itemstack.isEmpty()) {
                        itemstack.shrink(1);
                        if (itemstack.isEmpty()) {
                            items.set(1, itemstack.getContainerItem());
                        }
                    }
                    slotsChanged = true;
                }
            }

            if (isBurning(stack) && canSmelt(irecipe, items)) {
                ++cookTime;
                if (cookTime == cookTimeTotal) {
                    cookTime = 0;
                    cookTimeTotal = getRecipeCookTimeTotal(world, itemstack);
                    outputRecipe(irecipe, items);
                    slotsChanged = true;
                }
            } else {
                cookTime = 0;
            }
        } else if (!isBurning(stack) && cookTime > 0) {
            cookTime = MathHelper.clamp(cookTime - 2, 0, cookTimeTotal);
        }

        setStackBurnTime(stack, burnTime);
        setStackCookTime(stack, cookTime);
        setStackCookTimeTotal(stack, cookTimeTotal);

        if (slotsChanged) {
            System.out.println("Slots changed");
            ListNBT nbt = new ListNBT();
            for (int i = 0; i < 3; i++) {
                nbt.add(items.get(i).write(new CompoundNBT()));
            }
            stack.getOrCreateTag().put("Items", nbt);
        }
    }

    public static boolean isBurning(ItemStack stack) {
        return getStackBurnTime(stack) > 0;
    }

    protected static boolean canSmelt(@Nullable IRecipe<?> recipeIn, NonNullList<ItemStack> items) {
        if (!items.get(0).isEmpty() && recipeIn != null) {
            ItemStack itemstack = recipeIn.getRecipeOutput();
            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack itemstack1 = items.get(2);
                if (itemstack1.isEmpty()) {
                    return true;
                } else if (!itemstack1.isItemEqual(itemstack)) {
                    return false;
                } else if (itemstack1.getCount() + itemstack.getCount() <= 64 && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) {
                    return true;
                } else {
                    return itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize();
                }
            }
        } else {
            return false;
        }
    }

    private static NonNullList<ItemStack> loadItemsFromStack(ItemStack stack) {
        NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("Items")) {
            ListNBT nbt = tag.getList("Items", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < 3; i++) {
                items.set(i, ItemStack.read(nbt.getCompound(i)));
            }
        }
        return items;
    }

    public static int getStackBurnTime(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("burn_time")) {
            return tag.getInt("burn_time");
        }
        return 0;
    }

    public static void setStackBurnTime(ItemStack stack, int value) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("burn_time")) {
            tag.putInt("burn_time", value);
        }
    }

    public static int getStackCookTime(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("cook_time")) {
            return tag.getInt("cook_time");
        }
        return 0;
    }

    public static void setStackCookTime(ItemStack stack, int value) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("cook_time")) {
            tag.putInt("cook_time", value);
        }
    }

    public static int getStackCookTimeTotal(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("cook_time_total")) {
            return tag.getInt("cook_time_total");
        }
        return 0;
    }

    public static void setStackCookTimeTotal(ItemStack stack, int value) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("cook_time_total")) {
            tag.putInt("cook_time_total", value);
        }
    }

    private static void outputRecipe(@Nullable IRecipe<?> recipe, NonNullList<ItemStack> items) {
        if (recipe != null && canSmelt(recipe, items)) {
            ItemStack itemstack = items.get(0);
            ItemStack itemstack1 = recipe.getRecipeOutput();
            ItemStack itemstack2 = items.get(2);
            if (itemstack2.isEmpty()) {
                items.set(2, itemstack1.copy());
            } else if (itemstack2.getItem() == itemstack1.getItem()) {
                itemstack2.grow(itemstack1.getCount());
            }

            if (itemstack.getItem() == Blocks.WET_SPONGE.asItem() && !items.get(1).isEmpty() && items.get(1).getItem() == Items.BUCKET) {
                items.set(1, new ItemStack(Items.WATER_BUCKET));
            }

            itemstack.shrink(1);
        }
    }

    protected static int getRecipeCookTimeTotal(World world, ItemStack input) {
        return world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(input), world).map(AbstractCookingRecipe::getCookTime).orElse(200);
    }
}
