package quarris.stickutils.common.container.furnace;

import com.google.common.collect.Lists;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import quarris.stickutils.common.Content;

import java.util.List;

public class SmokerStickContainer extends AbstractFurnaceStickContainer {

    public SmokerStickContainer(int id, PlayerInventory playerInventory, PacketBuffer data) {
        this(id, playerInventory, data.readBoolean());
    }

    public SmokerStickContainer(int id, PlayerInventory playerInventory, boolean isOffhand) {
        super(Content.SMOKER_CONTAINER, id, playerInventory, isOffhand, IRecipeType.SMOKING);
    }

    @Override
    public List<RecipeBookCategories> getRecipeBookCategories() {
        return Lists.newArrayList(RecipeBookCategories.SMOKER_SEARCH, RecipeBookCategories.SMOKER_FOOD);
    }

}
