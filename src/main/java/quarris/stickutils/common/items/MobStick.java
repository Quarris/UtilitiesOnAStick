package quarris.stickutils.common.items;

import net.minecraft.block.Blocks;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import quarris.stickutils.ModRef;
import quarris.stickutils.common.Content;
import quarris.stickutils.common.items.base.UtilityStick;

public class MobStick extends UtilityStick {

    public MobStick() {
        super("mob_stick");
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (player.world.isRemote) return false;
        CompoundNBT tag = stack.getOrCreateTag();
        System.out.println(tag);
        if (!tag.contains("HasMob") || !tag.getBoolean("HasMob")) {
            ResourceLocation type = entity.getType().getRegistryName();
            CompoundNBT nbt = new CompoundNBT();
            entity.writeUnlessRemoved(nbt);
            tag.putBoolean("HasMob", true);
            tag.putString("Type", type.toString());
            tag.put("Data", nbt);
            System.out.println(tag);
            stack.setTag(tag);
            entity.remove();
            return true;
        }
        return false;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        ItemStack stack = ctx.getItem();
        CompoundNBT tag = stack.getOrCreateTag();
        if (tag.contains("HasMob") && tag.getBoolean("HasMob")) {
            PlayerEntity player = ctx.getPlayer();
            ResourceLocation type = new ResourceLocation(tag.getString("Type"));
            CompoundNBT nbt = tag.getCompound("Data");
            tag.putBoolean("HasMob", false);

            EntityType entityType = Registry.ENTITY_TYPE.getOrDefault(type);
            Entity e = entityType.create(player.world);
            e.read(nbt);
            player.world.addEntity(e);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Mod.EventBusSubscriber(modid = ModRef.ID)
    public static class EventHandler {

        @SubscribeEvent
        public static void createMobStick(PlayerInteractEvent.RightClickBlock event) {
            World world = event.getWorld();
            BlockPos pos = event.getPos();
            ItemStack stack = event.getItemStack();
            PlayerEntity player = event.getPlayer();
            if (stack.getItem() == Items.STICK && world.getBlockState(pos).getBlock() instanceof SpawnerBlock) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                stack.shrink(1);
                player.addItemStackToInventory(new ItemStack(Content.MOB_STICK));
            }
        }
    }

}
