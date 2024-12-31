package com.leclowndu93150.poweritems.items;

import com.leclowndu93150.poweritems.RandomUtils;
import com.leclowndu93150.poweritems.register.PDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

public class BatteryItem extends Item implements IEnergyStorage {
    private static final int MAX_ENERGY = 10000;
    private int energy = MAX_ENERGY;

    public BatteryItem(Properties properties) {
        super(properties.component(PDataComponents.ENERGY.get(), MAX_ENERGY).component(PDataComponents.ENABLED.get(), false));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player && level.getGameTime() % 20 == 0 && stack.getOrDefault(PDataComponents.ENABLED.get(), false)) {
            int currentEnergy = stack.getOrDefault(PDataComponents.ENERGY.get(), 0);

            for (ItemStack targetStack : player.getInventory().items) {
                if (targetStack == stack || currentEnergy <= 0 || !targetStack.has(PDataComponents.ENERGY.get())) continue;

                IEnergyStorage targetItem = (IEnergyStorage) targetStack.getItem();
                int targetEnergy = targetStack.getOrDefault(PDataComponents.ENERGY.get(), 0);

                if (targetEnergy >= targetItem.getMaxEnergyStored()) continue;

                int energyToTransfer = Math.min(20, currentEnergy);
                int maxTransfer = targetItem.getMaxEnergyStored() - targetEnergy;
                int actualTransfer = Math.min(energyToTransfer, maxTransfer);

                targetStack.set(PDataComponents.ENERGY.get(), targetEnergy + actualTransfer);
                stack.set(PDataComponents.ENERGY.get(), currentEnergy - actualTransfer);
                currentEnergy -= actualTransfer;
            }
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getOrDefault(PDataComponents.ENABLED.get(), false);
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
    public int getMaxStackSize(ItemStack stack) {
        return 1;
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

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Right click to toggle power transfer").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.literal("Energy: " + stack.getOrDefault(PDataComponents.ENERGY.get(),0) + " / " + MAX_ENERGY).withStyle(ChatFormatting.GRAY));
    }
}
