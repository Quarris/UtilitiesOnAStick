package quarris.stickutils.common.items;

import net.minecraft.item.Item;
import quarris.stickutils.ModRef;

public class UtilityStick extends Item {

	private static final Item.Properties UTIL_STICK_PROPERTIES = new Item.Properties()
			.group(ModRef.GROUP)
			.maxStackSize(1);

	public UtilityStick(String name) {
		super(UTIL_STICK_PROPERTIES);
		this.setRegistryName(ModRef.createRes(name));
	}
}
