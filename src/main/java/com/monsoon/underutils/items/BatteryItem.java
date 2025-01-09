package com.monsoon.underutils.items;

import com.monsoon.underutils.Config;
import com.monsoon.underutils.api.BatteryInstance;
import com.monsoon.underutils.api.IBatteryBasedItem;
import com.monsoon.underutils.api.ITimeBasedItem;
import com.monsoon.underutils.capabilities.BatteryStorage;
import com.monsoon.underutils.register.ComponentBatteryStorage;
import com.monsoon.underutils.register.PDataComponents;
import com.monsoon.underutils.util.PowerUtils;
import net.minecraft.ChatFormatting;
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

public class BatteryItem extends Item implements ITimeBasedItem {
    public BatteryItem(Properties props) {
        super(props
                .stacksTo(1)
                .component(PDataComponents.TIME.get(), Config.batteryCapacity)
        );
    }

    @Override
    public int getMaxTime() {
        return Config.batteryCapacity;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Collections.addAll(tooltipComponents, getTimeTooltip(stack));
        tooltipComponents.add(Component.literal("Shift + Right-click to toggle charging")
                .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return isTimeBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return getTimeBarWidth(stack, Config.batteryCapacity);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00FFFF;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack offhandStack = player.getOffhandItem();
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (offhandStack.getItem() instanceof IBatteryBasedItem) {
                BatteryStorage offHandBS = offhandStack.get(PDataComponents.BATTERY).batteryStorage().copy();
                if (offHandBS.getBattery(offHandBS.getEmptyOrLowest()) != BatteryInstance.EMPTY) {
                    BatteryInstance oldBattery = offHandBS.getBattery(offHandBS.getEmptyOrLowest());

                    offHandBS.setBattery(offHandBS.getEmptyOrLowest(), new BatteryInstance(stack.get(PDataComponents.TIME.get())));

                    offhandStack.set(PDataComponents.BATTERY, new ComponentBatteryStorage(offHandBS));
                    stack.set(PDataComponents.TIME, oldBattery.getCharge());
                } else {
                    offHandBS.setBattery(offHandBS.getEmptyOrLowest(), new BatteryInstance(stack.get(PDataComponents.TIME.get())));

                    offhandStack.set(PDataComponents.BATTERY, new ComponentBatteryStorage(offHandBS));
                    stack.setCount(0);
                }
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && entity instanceof Player player
                && level.getGameTime() % 10 == 0
                && stack.getOrDefault(PDataComponents.ENABLED.get(), false)) {

            int timeLeft = stack.getOrDefault(PDataComponents.TIME.get(), 0);
            if (timeLeft <= 0) return;

            for (ItemStack targetStack : player.getInventory().items) {
                if (!isValidTarget(targetStack, stack)) continue;

                int targetTime = targetStack.getOrDefault(PDataComponents.TIME.get(), 0);
                int maxTime = getMaxTimeForItem(targetStack.getItem());

                if (targetTime >= maxTime) continue;

                int transferAmount = Math.min(20, Math.min(timeLeft, maxTime - targetTime));
                targetStack.set(PDataComponents.TIME.get(), targetTime + transferAmount);
                timeLeft -= transferAmount;
                stack.set(PDataComponents.TIME.get(), timeLeft);

                if (timeLeft <= 0) break;
            }
        }
    }

    private boolean isValidTarget(ItemStack target, ItemStack source) {
        return target != source
                && target.getItem() instanceof IBatteryBasedItem;
    }

    private int getMaxTimeForItem(Item item) {
        if (item instanceof FlashlightItem) return PowerUtils.minutesToTicks(5);
        if (item instanceof NightVisionGogglesItem) return PowerUtils.minutesToTicks(15);
        return 0;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getOrDefault(PDataComponents.ENABLED.get(), false);
    }
}
