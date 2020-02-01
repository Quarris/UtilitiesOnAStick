package quarris.stickutils.common.items.base;

import net.minecraft.item.Item;
import quarris.stickutils.ModRef;

public abstract class UtilityStick extends Item {

	protected static final Item.Properties UTIL_STICK_PROPERTIES = new Item.Properties()
			.group(ModRef.GROUP)
			.maxStackSize(1);

	// TODO Container opening sticks should not be able to be picked up from the slot when opened
	public UtilityStick(String name) {
		this(name, UTIL_STICK_PROPERTIES);
	}

	public UtilityStick(String name, Properties props) {
	    super(props);
	    this.setRegistryName(name);
    }
}
