package quarris.stickutils.common.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import quarris.stickutils.ModRef;
import quarris.stickutils.common.Content;
import quarris.stickutils.common.items.base.UtilityStick;

public class CatStick extends UtilityStick {

    public CatStick() {
        super("cat_stick");
    }

    @Mod.EventBusSubscriber(modid = ModRef.ID)
    public static class EventHandler {

        @SubscribeEvent
        public static void poorKitty(PlayerInteractEvent.EntityInteractSpecific event) {
            PlayerEntity player = event.getPlayer();
            Entity target = event.getTarget();
            ItemStack stack = event.getItemStack();
            if (stack.getItem() == Items.STICK) {
                if ((target instanceof OcelotEntity && !((OcelotEntity) target).isChild()) || (target instanceof CatEntity && !((CatEntity) target).isTamed() && !((CatEntity) target).isChild())) {
                    player.getHeldItem(event.getHand()).shrink(1);
                    player.addItemStackToInventory(new ItemStack(Content.CAT_STICK));
                    target.remove();
                    event.getWorld().playSound(target.posX, target.posY, target.posZ, SoundEvents.ENTITY_CAT_HURT, SoundCategory.PLAYERS, 20, 1.1f, true);
                    event.getWorld().playSound(target.posX, target.posY, target.posZ, SoundEvents.ENTITY_CAT_HURT, SoundCategory.PLAYERS, 10, 1.3f, true);
                    event.getWorld().playSound(target.posX, target.posY, target.posZ, SoundEvents.ENTITY_CAT_HURT, SoundCategory.PLAYERS, 5, 1.5f, true);
                }
            }
        }

        @SubscribeEvent
        public static void creepersBegoneThot(EntityJoinWorldEvent event) {
            if (!event.getEntity().world.isRemote) {
                if (event.getEntity() instanceof CreeperEntity) {
                    CreeperEntity creeper = (CreeperEntity) event.getEntity();
                    creeper.goalSelector.addGoal(3, new AvoidEntityGoal<PlayerEntity>(creeper, PlayerEntity.class, 8, 1.0D, 1.2D, (player) -> player.getHeldItemMainhand().getItem() instanceof CatStick || player.getHeldItemOffhand().getItem() instanceof CatStick) {
                        @Override
                        public void startExecuting() {
                            creeper.world.playSound(null, this.entity.getPosition(), SoundEvents.ENTITY_CAT_AMBIENT, SoundCategory.NEUTRAL, 1, 1.1f);
                            super.startExecuting();
                        }
                    });
                }
            }
        }
    }
}
