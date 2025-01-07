package com.leclowndu93150.poweritems.items;

import com.leclowndu93150.poweritems.api.ITimeBasedItem;
import com.leclowndu93150.poweritems.register.PDataComponents;
import com.leclowndu93150.poweritems.shader.FlashlightManager;
import com.leclowndu93150.poweritems.util.PowerUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;

public class WindupFlashlightItem extends Item implements ITimeBasedItem {
    private static final int MAX_MINUTES = 5;
    private static final int CHARGE_TICKS = 20;
    private final int maxTicks;

    public WindupFlashlightItem(Properties props) {
        super(props.component(PDataComponents.TIME.get(), 0)
                .component(PDataComponents.ENABLED.get(), false)
                .stacksTo(1));
        this.maxTicks = PowerUtils.minutesToTicks(MAX_MINUTES);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Collections.addAll(tooltipComponents, getTimeTooltip(stack));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return isTimeBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return getTimeBarWidth(stack, maxTicks);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00FFFF;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                boolean current = stack.getOrDefault(PDataComponents.ENABLED.get(), false);
                stack.set(PDataComponents.ENABLED.get(), !current);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player) {
            int chargeTime = this.getUseDuration(stack, entity) - timeLeft;
            float power = getPowerForTime(chargeTime);
            if (power > 0.1F) {
                int chargeAmount = (int)(power * PowerUtils.secondsToTicks(5));
                int currentTime = stack.getOrDefault(PDataComponents.TIME.get(), 0);
                stack.set(PDataComponents.TIME.get(), Math.min(maxTicks, currentTime + chargeAmount));
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS,
                        0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            }
        }
    }

    private static float getPowerForTime(int charge) {
        float f = (float)charge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        return Math.min(f, 1.0F);
    }


    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CROSSBOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }


    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && entity instanceof Player player) {
            boolean isEnabled = stack.getOrDefault(PDataComponents.ENABLED.get(), false);
            int timeLeft = stack.getOrDefault(PDataComponents.TIME.get(), 0);

            boolean isActive = (selected || isInOffhand(player, stack)) && isEnabled && timeLeft > 0;
            FlashlightManager.getInstance().setFlashlightEnabled(isActive,this);

            if (!selected && !isInOffhand(player, stack)) {
                FlashlightManager.getInstance().setFlashlightEnabled(false,this);
                return;
            }

            if (isActive) {
                timeLeft--;
                stack.set(PDataComponents.TIME.get(), timeLeft);
                if (timeLeft <= 0) {
                    stack.set(PDataComponents.ENABLED.get(), false);
                }
            }
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    private boolean isInOffhand(Player player, ItemStack stack) {
        return player.getOffhandItem() == stack;
    }
}
