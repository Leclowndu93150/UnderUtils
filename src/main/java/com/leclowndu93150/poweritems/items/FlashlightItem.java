package com.leclowndu93150.poweritems.items;

import com.leclowndu93150.poweritems.RandomUtils;
import com.leclowndu93150.poweritems.register.PDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.*;

public class FlashlightItem extends Item implements IEnergyStorage {
    private static final int MAX_ENERGY = 6000;
    private static final int LIGHT_RANGE = 15;
    private static final int ENERGY_USAGE = 1;
    private static final int LIGHT_FADE_DELAY = 2;
    private int energy = MAX_ENERGY;
    private BlockPos lastLightPos = null;
    private int ticksSinceLastLight = 0;
    private Map<BlockPos, Integer> fadingLights = new HashMap<>();

    public FlashlightItem(Properties properties) {
        super(properties.component(PDataComponents.ENERGY.get(), 0)
                .component(PDataComponents.ENABLED.get(), false)
                .stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player) {
            boolean isEnabled = stack.getOrDefault(PDataComponents.ENABLED.get(), false);
            int currentEnergy = stack.getOrDefault(PDataComponents.ENERGY.get(), 0);

            Iterator<Map.Entry<BlockPos, Integer>> iterator = fadingLights.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<BlockPos, Integer> entry = iterator.next();
                BlockPos pos = entry.getKey();
                int ticksRemaining = entry.getValue() - 1;

                if (ticksRemaining <= 0) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    iterator.remove();
                } else {
                    entry.setValue(ticksRemaining);
                }
            }

            if (isEnabled && currentEnergy > 0 && (isSelected || player.getOffhandItem() == stack)) {
                HitResult hit = player.pick(LIGHT_RANGE, 0, false);
                if (hit.getType() == HitResult.Type.BLOCK) {
                    BlockPos targetPos = ((BlockHitResult) hit).getBlockPos();
                    BlockPos lightPos = targetPos.relative(((BlockHitResult) hit).getDirection());

                    if (level.getBlockState(lightPos).isAir()) {
                        if (lastLightPos != null && !lastLightPos.equals(lightPos)) {
                            fadingLights.put(lastLightPos, LIGHT_FADE_DELAY);
                        }

                        level.setBlock(lightPos, Blocks.LIGHT.defaultBlockState(), 3);
                        lastLightPos = lightPos;
                        ticksSinceLastLight = 0;

                        currentEnergy -= ENERGY_USAGE;
                        stack.set(PDataComponents.ENERGY.get(), currentEnergy);
                    }
                }
            } else if (lastLightPos != null) {
                ticksSinceLastLight++;
                if (ticksSinceLastLight >= LIGHT_FADE_DELAY) {
                    fadingLights.put(lastLightPos, LIGHT_FADE_DELAY);
                    lastLightPos = null;
                }
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean isEnabled = stack.getOrDefault(PDataComponents.ENABLED.get(), false);
        stack.set(PDataComponents.ENABLED.get(), !isEnabled);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
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

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Right-click to toggle light").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.literal("Energy: " + stack.getOrDefault(PDataComponents.ENERGY.get(), 0) + " / " + MAX_ENERGY).withStyle(ChatFormatting.GRAY));
    }
}