package com.leclowndu93150.poweritems.items;

import com.leclowndu93150.poweritems.api.IBatteryBasedItem;
import com.leclowndu93150.poweritems.capabilities.BatteryStorage;
import com.leclowndu93150.poweritems.register.ComponentBatteryStorage;
import com.leclowndu93150.poweritems.register.PDataComponents;
import com.leclowndu93150.poweritems.register.PDataComponentsUtils;
import com.leclowndu93150.poweritems.shader.NightVisionManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class NightVisionGogglesItem extends Item implements IBatteryBasedItem {

    public NightVisionGogglesItem(Properties props) {
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
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && entity instanceof Player player) {
            BatteryStorage bs = stack.get(PDataComponents.BATTERY).batteryStorage().copy();
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            if (helmet != stack) {
                stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(0));
                NightVisionManager.getInstance().setNightVisionEnabled(false);
                return;
            }

            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1));
            boolean isEnabled = stack.getOrDefault(PDataComponents.ENABLED.get(), false);



            if (isEnabled && bs.getTotalCharge() > 0) {
                NightVisionManager.getInstance().setNightVisionEnabled(true);
                if (level.getGameTime() % 20 == 0) {
                    bs.removeCharge(20);
                    if (bs.getTotalCharge() <= 0) {
                        disableGoggles(stack, player);
                    }
                }
            } else {
                NightVisionManager.getInstance().setNightVisionEnabled(false);
            }
            stack.set(PDataComponents.BATTERY, new ComponentBatteryStorage(bs));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown() && PDataComponentsUtils.getBatteryStorageComponent(stack).getTotalCharge() > 0) {
            if (!level.isClientSide) {
                boolean current = stack.getOrDefault(PDataComponents.ENABLED.get(), false);
                stack.set(PDataComponents.ENABLED.get(), !current);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        return InteractionResultHolder.pass(stack);
    }

    private boolean isEquipped(Player player, ItemStack stack) {
        return player.getItemBySlot(EquipmentSlot.HEAD) == stack;
    }

    private void disableGoggles(ItemStack stack, Player player) {
        stack.set(PDataComponents.ENABLED.get(), false);
        stack.set(PDataComponents.TIME.get(), 0);
        NightVisionManager.getInstance().setNightVisionEnabled(false);
        player.displayClientMessage(
                Component.literal("Night Vision Goggles depleted!")
                        .withStyle(ChatFormatting.RED),
                true
        );
    }

    @Override
    public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.HEAD;
    }
}