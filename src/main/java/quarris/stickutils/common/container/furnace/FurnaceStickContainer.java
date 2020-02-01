package quarris.stickutils.common.container.furnace;

import com.google.common.collect.Lists;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import quarris.stickutils.common.Content;

import java.util.List;

public class FurnaceStickContainer extends AbstractFurnaceStickContainer {

    public FurnaceStickContainer(int id, PlayerInventory playerInventory, PacketBuffer data) {
        this(id, playerInventory, data.readBoolean());
    }

    public FurnaceStickContainer(int id, PlayerInventory playerInventory, boolean isOffhand) {
        super(Content.FURNACE_CONTAINER, id, playerInventory, isOffhand, IRecipeType.SMELTING);
    }

    @Override
    public List<RecipeBookCategories> getRecipeBookCategories() {
        return Lists.newArrayList(RecipeBookCategories.FURNACE_SEARCH, RecipeBookCategories.FURNACE_FOOD, RecipeBookCategories.FURNACE_BLOCKS, RecipeBookCategories.FURNACE_MISC);
    }

}
