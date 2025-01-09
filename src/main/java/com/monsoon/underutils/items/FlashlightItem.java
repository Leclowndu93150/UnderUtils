package com.monsoon.underutils.items;

import com.monsoon.underutils.api.IBatteryBasedItem;
import com.monsoon.underutils.capabilities.BatteryStorage;
import com.monsoon.underutils.capabilities.IBatteryStorage;
import com.monsoon.underutils.register.ComponentBatteryStorage;
import com.monsoon.underutils.register.PDataComponentsUtils;
import com.monsoon.underutils.register.PDataComponents;
import com.monsoon.underutils.shader.FlashlightManager;
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

public class FlashlightItem extends Item implements IBatteryBasedItem {
    public IBatteryStorage batteryStorage;

    public FlashlightItem(Properties props) {
        super(props
                .component(PDataComponents.ENABLED.get(), false)
                .component(PDataComponents.BATTERY.get(), new ComponentBatteryStorage(new BatteryStorage(1, true)))
                .stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Collections.addAll(tooltipComponents, getBatteryTooltip(stack));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return isTimeBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return getTimeBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00FFFF;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (PDataComponentsUtils.getBatteryStorageComponent(stack).getTotalCharge() > 0) {
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
            BatteryStorage bs = stack.get(PDataComponents.BATTERY).batteryStorage().copy();
            boolean isEnabled = stack.getOrDefault(PDataComponents.ENABLED.get(), false);

            boolean isActive = (selected || isInOffhand(player, stack)) && isEnabled && bs.getTotalCharge() > 0;
            FlashlightManager.getInstance().setFlashlightEnabled(isActive,this);

            if (!selected && !isInOffhand(player, stack)) {
                FlashlightManager.getInstance().setFlashlightEnabled(false,this);
                return;
            }

            if (isActive) {
                if (level.getGameTime() % 20 == 0) {
                    bs.removeCharge(20);
                    if (bs.getTotalCharge() <= 0) {
                        stack.set(PDataComponents.ENABLED.get(), false);
                    }
                }
            }
            stack.set(PDataComponents.BATTERY, new ComponentBatteryStorage(bs));
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