package quarris.stickutils.common.container;

import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;

import java.util.List;

public abstract class RecipeBookStickContainer<C extends IInventory> extends RecipeBookContainer<C> {

    public RecipeBookStickContainer(ContainerType<?> type, int windowId) {
        super(type, windowId);
    }

    @Override
    protected Slot addSlot(Slot slotIn) {
        // TODO Change the slot that the stick is held is to a slot where the item cannot be removed from it
        return super.addSlot(slotIn);
    }

    @Override
    public abstract List<RecipeBookCategories> getRecipeBookCategories();
}
