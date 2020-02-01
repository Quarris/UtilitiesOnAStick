package quarris.stickutils.common;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.registries.ObjectHolder;
import quarris.stickutils.common.container.CraftingStickContainer;
import quarris.stickutils.common.container.furnace.AbstractFurnaceStickContainer;
import quarris.stickutils.common.container.furnace.BlastStickContainer;
import quarris.stickutils.common.container.furnace.FurnaceStickContainer;
import quarris.stickutils.common.container.furnace.SmokerStickContainer;
import quarris.stickutils.common.items.base.UtilityStick;

public class Content {

    /*
        ITEMS
     */

    @ObjectHolder("stickutils:crafting_stick")
    public static UtilityStick CRAFTING_STICK;

    @ObjectHolder("stickutils:furnace_stick")
    public static UtilityStick FURNACE_STICK;

    @ObjectHolder("stickutils:blast_stick")
    public static UtilityStick BLASTING_STICK;

    @ObjectHolder("stickutils:smoker_stick")
    public static UtilityStick SMOKING_STICK;

    @ObjectHolder("stickutils:cat_stick")
    public static UtilityStick CAT_STICK;

    @ObjectHolder("stickutils:creeper_stick")
    public static UtilityStick CREEPER_STICK;

    @ObjectHolder("stickutils:mob_stick")
    public static UtilityStick MOB_STICK;

    /*
        CONTAINERS
     */

    @ObjectHolder("stickutils:crafting_container")
    public static ContainerType<CraftingStickContainer> CRAFTING_CONTAINER;

    @ObjectHolder("stickutils:furnace_container")
    public static ContainerType<FurnaceStickContainer> FURNACE_CONTAINER;

    @ObjectHolder("stickutils:blast_container")
    public static ContainerType<BlastStickContainer> BLAST_CONTAINER;

    @ObjectHolder("stickutils:smoker_container")
    public static ContainerType<SmokerStickContainer> SMOKER_CONTAINER;
}
