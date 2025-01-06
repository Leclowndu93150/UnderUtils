package com.leclowndu93150.poweritems.items;

import com.leclowndu93150.poweritems.api.ITimeBasedItem;
import com.leclowndu93150.poweritems.register.PDataComponents;
import com.leclowndu93150.poweritems.shader.NightVisionManager;
import com.leclowndu93150.poweritems.util.PowerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;

public class NightVisionGogglesItem extends ArmorItem implements ITimeBasedItem {
    private static final int MAX_MINUTES = 15;
    private final int maxTicks;

    public NightVisionGogglesItem(Properties props) {
        super(ArmorMaterials.IRON, Type.HELMET, props
                .component(PDataComponents.TIME.get(), PowerUtils.minutesToTicks(MAX_MINUTES))
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
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && entity instanceof Player player) {
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            if (helmet != stack) {
                NightVisionManager.getInstance().setNightVisionEnabled(false);
                return;
            }

            boolean isEnabled = stack.getOrDefault(PDataComponents.ENABLED.get(), false);
            int timeLeft = stack.getOrDefault(PDataComponents.TIME.get(), 0);

            if (isEnabled && timeLeft > 0) {
                NightVisionManager.getInstance().setNightVisionEnabled(true);
                if (level.getGameTime() % 20 == 0) {
                    timeLeft--;
                    stack.set(PDataComponents.TIME.get(), timeLeft);
                    if (timeLeft <= 0) {
                        disableGoggles(stack, player);
                    }
                }
            } else {
                NightVisionManager.getInstance().setNightVisionEnabled(false);
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown() && stack.getOrDefault(PDataComponents.TIME.get(), 0) > 0) {
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
}