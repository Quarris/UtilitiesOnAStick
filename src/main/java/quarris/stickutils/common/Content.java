package quarris.stickutils.common;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import quarris.qlib.api.registry.registry.ContainerRegistry;
import quarris.qlib.api.registry.registry.ItemRegistry;
import quarris.stickutils.StickUtils;
import quarris.stickutils.common.container.CraftingStickContainer;
import quarris.stickutils.common.items.CatStick;
import quarris.stickutils.common.items.CraftingStick;
import quarris.stickutils.common.items.CreeperStick;
import quarris.stickutils.common.items.UtilityStick;

@ItemRegistry(StickUtils.ID)
@ContainerRegistry(StickUtils.ID)
public class Content {

    /* ITEMS */
    public static final UtilityStick CAT_STICK = new CatStick();
    public static final UtilityStick CREEPER_STICK = new CreeperStick();
    public static final UtilityStick CRAFTING_STICK = new CraftingStick();

    /* CONTAINERS */
    public static final ContainerType<CraftingStickContainer> CRAFTING_STICK_CONTAINER = IForgeContainerType.create(CraftingStickContainer::new);
}
