package quarris.stickutils.common.container.furnace;

import com.google.common.collect.Lists;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import quarris.stickutils.common.Content;

import java.util.List;

public class BlastStickContainer extends AbstractFurnaceStickContainer {

    public BlastStickContainer(int id, PlayerInventory playerInventory, PacketBuffer data) {
        this(id, playerInventory, data.readBoolean());
    }

    public BlastStickContainer(int id, PlayerInventory playerInventory, boolean isOffhand) {
        super(Content.BLAST_CONTAINER, id, playerInventory, isOffhand, IRecipeType.BLASTING);
    }

    @Override
    public List<RecipeBookCategories> getRecipeBookCategories() {
        return Lists.newArrayList(RecipeBookCategories.BLAST_FURNACE_SEARCH, RecipeBookCategories.BLAST_FURNACE_BLOCKS, RecipeBookCategories.BLAST_FURNACE_MISC);
    }

}
