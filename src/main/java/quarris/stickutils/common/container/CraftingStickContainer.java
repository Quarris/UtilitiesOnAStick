package quarris.stickutils.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import quarris.stickutils.common.Content;
import quarris.stickutils.common.container.slots.ImmovableSlot;

import java.util.Optional;

public class CraftingStickContainer extends StickRecipeBookContainer<CraftingInventory> {
    private final CraftingInventory craftingInventory = new CraftingInventory(this, 3, 3);
    private final CraftResultInventory craftingResult = new CraftResultInventory();
    private final PlayerEntity player;
    private final World world;

    public CraftingStickContainer(int id, PlayerInventory playerInventory, PacketBuffer data) {
        this(id, playerInventory, data.readBoolean());
    }

    public CraftingStickContainer(int id, PlayerInventory playerInventory, boolean isOffhand) {
        super(Content.CRAFTING_CONTAINER, id, playerInventory, isOffhand);
        this.player = playerInventory.player;
        this.world = this.player.world;
        // Crafting Result Slot
        this.addSlot(new CraftingResultSlot(this.player, this.craftingInventory, this.craftingResult, 0, 124, 35));

        // Crafting Table Slot
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.addSlot(new Slot(this.craftingInventory, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

        // Player Inventory
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Player Hotbar
        for (int i = 0; i < 9; ++i) {
            if (PlayerInventory.isHotbar(playerInventory.currentItem) && playerInventory.currentItem == i) {
                this.addSlot(new ImmovableSlot(playerInventory, i, 8 + i * 18, 142));
            } else {
                this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
            }
        }

        this.initContainer();
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        if (!this.world.isRemote) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) this.player;
            ItemStack itemstack = ItemStack.EMPTY;
            Optional<ICraftingRecipe> optional = this.world.getServer().getRecipeManager().getRecipe(IRecipeType.CRAFTING, this.craftingInventory, this.world);
            if (optional.isPresent()) {
                ICraftingRecipe icraftingrecipe = optional.get();
                if (this.craftingResult.canUseRecipe(this.world, serverplayerentity, icraftingrecipe)) {
                    itemstack = icraftingrecipe.getCraftingResult(craftingInventory);
                }
            }

            this.craftingResult.setInventorySlotContents(0, itemstack);
            serverplayerentity.connection.sendPacket(new SSetSlotPacket(this.windowId, 0, itemstack));
        }
    }

    // When a recipe is clicked in the recipe book...
    public void func_201771_a(RecipeItemHelper recipeBookHelper) {
        // Fill the contents of the crafting grid with the items.
        this.craftingInventory.fillStackedContents(recipeBookHelper);
    }

    public void clear() {
        this.craftingInventory.clear();
        this.craftingResult.clear();
    }

    public boolean matches(IRecipe<? super CraftingInventory> recipeIn) {
        return recipeIn.matches(this.craftingInventory, this.player.world);
    }
    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in is
     * null for the initial slot that was double-clicked.
     */
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return slotIn.inventory != this.craftingResult && super.canMergeSlot(stack, slotIn);
    }

    public int getOutputSlot() {
        return 0;
    }

    public int getWidth() {
        return this.craftingInventory.getWidth();
    }

    public int getHeight() {
        return this.craftingInventory.getHeight();
    }

    @OnlyIn(Dist.CLIENT)
    public int getSize() {
        return 10;
    }

    @Override
    public IInventory getStickInventory() {
        return this.craftingInventory;
    }
}
