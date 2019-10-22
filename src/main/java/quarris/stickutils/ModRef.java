package quarris.stickutils;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

public class ModRef {

	public static final String ID = "stickutils";
	public static final String NAME = "Utilities on a Stick";

	public static final ItemGroup GROUP = new ItemGroup(ID) {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Items.STICK);
		}
	};

	public static ResourceLocation createRes(String name) {
		return new ResourceLocation(ID, name);
	}
}