package quarris.stickutils.common.items;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import quarris.stickutils.common.container.furnace.AbstractFurnaceStickContainer;
import quarris.stickutils.common.container.furnace.FurnaceStickContainer;
import quarris.stickutils.common.items.base.AbstractFurnaceStick;

public class FurnaceStick extends AbstractFurnaceStick {

    public FurnaceStick() {
        super("furnace_stick", IRecipeType.SMELTING);
    }

    @Override
    protected Class<? extends AbstractFurnaceStickContainer> getContainerClass() {
        return FurnaceStickContainer.class;
    }

    @Override
    protected Container getContainer(int id, PlayerInventory inventory, boolean isOffhand) {
        return new FurnaceStickContainer(id, inventory, isOffhand);
    }

    @Override
    protected ITextComponent getContainerDisplayName() {
        return new TranslationTextComponent("container.furnace");
    }
}
