package quarris.stickutils.common.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import quarris.stickutils.ModRef;
import quarris.stickutils.common.Content;
import quarris.stickutils.common.items.base.UtilityStick;

public class CreeperStick extends UtilityStick {

    public CreeperStick() {
        super("creeper_stick");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (!world.isRemote) {
            player.getHeldItem(hand).shrink(1);
            world.createExplosion(null, player.posX, player.posY, player.posZ, (float) 3, Explosion.Mode.BREAK);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
    }

    @Mod.EventBusSubscriber(modid = ModRef.ID)
    public static class EventHandler {

        @SubscribeEvent
        public static void poorCreeper(PlayerInteractEvent.EntityInteractSpecific event) {
            PlayerEntity player = event.getPlayer();
            Entity target = event.getTarget();
            ItemStack stack = event.getItemStack();
            if (stack.getItem() == Items.STICK) {
                if (target instanceof CreeperEntity) {
                    player.getHeldItem(event.getHand()).shrink(1);
                    player.addItemStackToInventory(new ItemStack(Content.CREEPER_STICK));
                    target.remove();
                    event.getWorld().playSound(target.posX, target.posY, target.posZ, SoundEvents.ENTITY_CREEPER_HURT, SoundCategory.PLAYERS, 20, 1.1f, true);
                    event.getWorld().playSound(target.posX, target.posY, target.posZ, SoundEvents.ENTITY_CREEPER_HURT, SoundCategory.PLAYERS, 10, 1.3f, true);
                    event.getWorld().playSound(target.posX, target.posY, target.posZ, SoundEvents.ENTITY_CREEPER_HURT, SoundCategory.PLAYERS, 5, 1.5f, true);
                }
            }
        }
    }
}
