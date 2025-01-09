package com.monsoon.underutils.items;

import com.monsoon.underutils.Config;
import com.monsoon.underutils.api.IBatteryBasedItem;
import com.monsoon.underutils.capabilities.BatteryStorage;
import com.monsoon.underutils.capabilities.IBatteryStorage;
import com.monsoon.underutils.register.ComponentBatteryStorage;
import com.monsoon.underutils.register.PDataComponents;
import com.monsoon.underutils.register.PDataComponentsUtils;
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

public class BatteryPackItem extends Item implements IBatteryBasedItem {
    public IBatteryStorage batteryStorage;

    public BatteryPackItem(Properties props) {
        super(props
                .component(PDataComponents.ENABLED.get(), false)
                .component(PDataComponents.BATTERY.get(), new ComponentBatteryStorage(new BatteryStorage(6, true)))
                .stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Collections.addAll(tooltipComponents, getBatteryTooltip(stack));
        tooltipComponents.add(Component.literal("Shift + Right-click to toggle charging")
                .withStyle(ChatFormatting.GRAY));
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
        if (player.isShiftKeyDown()) {
            stack.set(PDataComponents.ENABLED.get(), !stack.getOrDefault(PDataComponents.ENABLED.get(), false));
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        } else {
            BatteryStorage batteryStorage = PDataComponentsUtils.getBatteryStorageComponent(stack);

            for (int i = 0; i < batteryStorage.getBatterySlots(); i++) {
                System.out.println("Battery " + i + ": " + batteryStorage.getBattery(i).getCharge());
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && entity instanceof Player player
                && level.getGameTime() % 10 == 0
                && stack.getOrDefault(PDataComponents.ENABLED.get(), false)) {

            BatteryStorage sourceBS = stack.get(PDataComponents.BATTERY).batteryStorage().copy();

            for (ItemStack targetStack : player.getInventory().items) {
                if (!isValidTarget(targetStack, stack)) continue;

                BatteryStorage targetBS = targetStack.get(PDataComponents.BATTERY).batteryStorage().copy();

                if (targetBS.getTotalCharge() == Config.batteryCapacity * targetBS.getBatterySlots()) continue;

                sourceBS.getBattery(targetBS.getMostCharged()).removeCharge(20);
                targetBS.getBattery(targetBS.getEmptyOrLowest()).addCharge(20);

                stack.set(PDataComponents.BATTERY, new ComponentBatteryStorage(sourceBS));
                targetStack.set(PDataComponents.BATTERY, new ComponentBatteryStorage(targetBS));
            }
        }
    }

    private boolean isValidTarget(ItemStack target, ItemStack source) {
        return target != source
                && target.getItem() instanceof IBatteryBasedItem
                && !(target.getItem() instanceof BatteryPackItem);
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
