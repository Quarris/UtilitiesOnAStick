package quarris.stickutils.common.items;

import net.minecraft.item.Item;
import quarris.stickutils.StickUtils;

public class UtilityStick extends Item {

	private static final Item.Properties UTIL_STICK_PROPERTIES = new Item.Properties()
			.group(StickUtils.GROUP)
			.maxStackSize(1);

	// TODO Container opening sticks should not be able to be picked up from the slot when opened
	public UtilityStick() {
		super(UTIL_STICK_PROPERTIES);
	}
}
