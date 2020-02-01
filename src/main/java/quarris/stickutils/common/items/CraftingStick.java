package quarris.stickutils.common.items;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import quarris.stickutils.common.container.CraftingStickContainer;
import quarris.stickutils.common.items.base.ContainerStick;

public class CraftingStick extends ContainerStick {

    public CraftingStick() {
        super("crafting_stick", 9);
    }

    @Override
    protected Container getContainer(int id, PlayerInventory inventory, boolean isOffhand) {
        return new CraftingStickContainer(id, inventory, isOffhand);
    }

    @Override
    protected ITextComponent getContainerDisplayName() {
        return new TranslationTextComponent("container.crafting");
    }
}
