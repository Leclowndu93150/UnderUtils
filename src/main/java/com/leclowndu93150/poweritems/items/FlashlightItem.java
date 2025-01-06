package com.leclowndu93150.poweritems.items;

import com.leclowndu93150.poweritems.register.PDataComponents;
import com.leclowndu93150.poweritems.shader.FlashlightManager;
import com.leclowndu93150.poweritems.util.PowerUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;

public class FlashlightItem extends Item implements ITimeBasedItem {
    private static final int MAX_MINUTES = 5;
    private final int maxTicks;

    public FlashlightItem(Properties props) {
        super(props.component(PDataComponents.TIME.get(), PowerUtils.minutesToTicks(MAX_MINUTES))
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
        if (stack.getOrDefault(PDataComponents.TIME.get(), 0) > 0) {
            if (!level.isClientSide) {
                boolean current = stack.getOrDefault(PDataComponents.ENABLED.get(), false);
                stack.set(PDataComponents.ENABLED.get(), !current);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && entity instanceof Player player) {
            boolean isEnabled = stack.getOrDefault(PDataComponents.ENABLED.get(), false);
            int timeLeft = stack.getOrDefault(PDataComponents.TIME.get(), 0);

            boolean isActive = (selected || isInOffhand(player, stack)) && isEnabled && timeLeft > 0;
            FlashlightManager.getInstance().setFlashlightEnabled(isActive);

            if (!selected && !isInOffhand(player, stack)) {
                FlashlightManager.getInstance().setFlashlightEnabled(false);
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

    private boolean isInOffhand(Player player, ItemStack stack) {
        return player.getOffhandItem() == stack;
    }
}