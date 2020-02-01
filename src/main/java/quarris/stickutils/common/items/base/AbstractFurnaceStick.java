package quarris.stickutils.common.items.base;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import quarris.stickutils.ModRef;
import quarris.stickutils.common.capabilities.ItemCapability;
import quarris.stickutils.common.capabilities.TickingFurnaceItem;
import quarris.stickutils.common.container.furnace.AbstractFurnaceStickContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractFurnaceStick extends ContainerStick {

    private IRecipeType<? extends AbstractCookingRecipe> recipeType;

    public AbstractFurnaceStick(String name, IRecipeType<? extends AbstractCookingRecipe> recipeType) {
        super(name, 3);
        this.recipeType = recipeType;
        this.addPropertyOverride(ModRef.createRes("on"), (stack, world, entity) -> {
            if (stack.getCapability(ItemCapability.tickingCapability).isPresent()) {
                return stack.getCapability(ItemCapability.tickingCapability).orElse(null).isActive() ? 1 : 0;
            }
            return 0;
        });
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof PlayerEntity) {
            stack.getCapability(ItemCapability.tickingCapability).ifPresent(cap -> cap.tick(world, false, itemSlot, entity));
        }
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        stack.getCapability(ItemCapability.tickingCapability).ifPresent(cap -> cap.tick(entity.world, true, entity.getEntityId(), entity.getDataManager()));
        return false;
    }

    protected abstract Class<? extends AbstractFurnaceStickContainer> getContainerClass();

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ICapabilitySerializable<CompoundNBT>() {
            private final ItemStackHandler inventory = new ItemStackHandler(AbstractFurnaceStick.this.inventorySize);
            private final TickingFurnaceItem furnace = new TickingFurnaceItem(stack, AbstractFurnaceStick.this.recipeType, AbstractFurnaceStick.this.getContainerClass());

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                    return LazyOptional.of(() -> (T) inventory);
                } else if (cap == ItemCapability.tickingCapability) {
                    return LazyOptional.of(() -> (T) furnace);
                }
                return LazyOptional.empty();
            }

            @Override
            public CompoundNBT serializeNBT() {
                CompoundNBT nbt = new CompoundNBT();
                nbt.put("Inventory", this.inventory.serializeNBT());
                nbt.put("Furnace", this.furnace.serializeNBT());
                return nbt;
            }

            @Override
            public void deserializeNBT(CompoundNBT nbt) {
                this.inventory.deserializeNBT(nbt.getCompound("Inventory"));
                this.furnace.deserializeNBT(nbt.getCompound("Furnace"));
            }
        };
    }
}
