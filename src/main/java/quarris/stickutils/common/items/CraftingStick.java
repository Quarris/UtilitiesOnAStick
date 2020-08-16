package quarris.stickutils.common.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.text.TranslationTextComponent;
import quarris.stickutils.common.container.CraftingStickContainer;

public class CraftingStick extends ContainerStick {

    @Override
    public INamedContainerProvider createContainer(PlayerEntity playerEntity) {
        return new SimpleNamedContainerProvider((id, inv, player) -> new CraftingStickContainer(id, inv, null), new TranslationTextComponent("container.crafting"));
    }
}
