package quarris.stickutils.client.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import quarris.stickutils.common.container.CraftingStickContainer;

@OnlyIn(Dist.CLIENT)
public class CraftingStickScreen extends RecipeBookStickScreen<CraftingInventory, CraftingStickContainer> {

    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/crafting_table.png");

    public CraftingStickScreen(CraftingStickContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected ResourceLocation getBackgroundTexture() {
        return CRAFTING_TABLE_GUI_TEXTURES;
    }


}
