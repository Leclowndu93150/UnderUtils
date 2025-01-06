package com.leclowndu93150.poweritems.items;

import com.leclowndu93150.poweritems.RandomUtils;
import com.leclowndu93150.poweritems.register.PDataComponents;
import com.leclowndu93150.poweritems.shader.FlashlightManager;
import com.leclowndu93150.poweritems.shader.NightVisionManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

public class NightVisionGogglesItem extends ArmorItem implements IEnergyStorage {
    private static final int MAX_ENERGY = 6000;
    private int energy = MAX_ENERGY;
    private static final int ENERGY_USE_PER_TICK = 1;

    public NightVisionGogglesItem(Properties properties) {
        super(ArmorMaterials.IRON, Type.HELMET, properties
                .component(PDataComponents.ENERGY.get(), MAX_ENERGY)
                .component(PDataComponents.ENABLED.get(), false)
                .stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player) {
            ItemStack equipped = player.getItemBySlot(EquipmentSlot.HEAD);
            if (!equipped.is(this)) return;

            boolean isEnabled = stack.getOrDefault(PDataComponents.ENABLED.get(), false);
            int currentEnergy = stack.getOrDefault(PDataComponents.ENERGY.get(), 0);

            if (isEnabled) {
                NightVisionManager.getInstance().setNightVisionEnabled(isEnabled && currentEnergy > 0);
                if (currentEnergy <= 0) {
                    disableGoggles(stack, player, level);
                } else if (level.getGameTime() % 20 == 0) {
                    stack.set(PDataComponents.ENERGY.get(), currentEnergy - ENERGY_USE_PER_TICK);
                    if (currentEnergy - ENERGY_USE_PER_TICK <= 0) {
                        disableGoggles(stack, player, level);
                    }
                }
            }
        }
    }

    private void disableGoggles(ItemStack stack, Player player, Level level) {
        stack.set(PDataComponents.ENABLED.get(), false);
        NightVisionManager.getInstance().setNightVisionEnabled(false);
        stack.set(PDataComponents.ENERGY.get(), 0);
        player.displayClientMessage(Component.translatable("message.poweritems.goggles.no_power")
                .withStyle(ChatFormatting.RED), true);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.PLAYERS, 0.5f, 0.5f);
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.poweritems.night_vision_goggles.tooltip")
                .withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.literal("Energy: " + stack.getOrDefault(PDataComponents.ENERGY.get(), 0)
                + " / " + MAX_ENERGY).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("item.poweritems.night_vision_goggles.key_hint")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energyReceived = Math.min(MAX_ENERGY - energy, Math.min(1000, maxReceive));
        if (!simulate) {
            energy += energyReceived;
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(energy, Math.min(1000, maxExtract));
        if (!simulate) {
            energy -= energyExtracted;
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return MAX_ENERGY;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getOrDefault(PDataComponents.ENERGY.get(), 6000) != MAX_ENERGY;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return FastColor.ARGB32.color(94, 133, 164);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return RandomUtils.powerForDurabilityBar(stack);
    }
}