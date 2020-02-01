package quarris.stickutils.common.items.base;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ContainerStick extends UtilityStick {

    public final int inventorySize;

    public ContainerStick(String name, int inventorySize) {
        super(name);
        this.inventorySize = inventorySize;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (!world.isRemote) {
            NetworkHooks.openGui((ServerPlayerEntity) player,
                    new SimpleNamedContainerProvider(
                            (id, inv, playerEntity) -> this.getContainer(id, inv, hand == Hand.OFF_HAND),
                            this.getContainerDisplayName()),
                    data -> data.writeBoolean(hand == Hand.OFF_HAND));
        }
        return ActionResult.newResult(ActionResultType.SUCCESS, player.getHeldItem(hand));
    }

    public static void saveInventory(ItemStack stack, IInventory inventory) {
        stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
            if (inventory.getSizeInventory() == cap.getSlots()) {
                for (int i = 0; i < cap.getSlots(); i++) {
                    ((ItemStackHandler) cap).setStackInSlot(i, inventory.getStackInSlot(i));
                }
            }
        });
    }

    public static void loadInventory(ItemStack stack, IInventory inventory) {
        stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
            if (inventory.getSizeInventory() == cap.getSlots()) {
                for (int i = 0; i < cap.getSlots(); i++) {
                    inventory.setInventorySlotContents(i, cap.getStackInSlot(i));
                }
            }
        });
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ICapabilitySerializable<CompoundNBT>() {
            private final ItemStackHandler inventory = new ItemStackHandler(ContainerStick.this.inventorySize);

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                    return LazyOptional.of(() -> (T) inventory);
                }
                return LazyOptional.empty();
            }

            @Override
            public CompoundNBT serializeNBT() {
                return this.inventory.serializeNBT();
            }

            @Override
            public void deserializeNBT(CompoundNBT nbt) {
                this.inventory.deserializeNBT(nbt);
            }
        };
    }

    protected abstract Container getContainer(int id, PlayerInventory inventory, boolean isOffhand);

    protected abstract ITextComponent getContainerDisplayName();
}
