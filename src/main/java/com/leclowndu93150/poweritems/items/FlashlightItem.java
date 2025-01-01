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
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.*;

public class FlashlightItem extends Item implements IEnergyStorage {
    private static final int MAX_ENERGY = 6000;
    private static final int LIGHT_RANGE = 15;
    private static final int ENERGY_USAGE = 1;
    private static final int LIGHT_FADE_DELAY = 5;
    private int energy = MAX_ENERGY;
    private BlockPos lastLightPos = null;
    private int ticksSinceLastLight = 0;
    private Map<BlockPos, Integer> fadingLights = new HashMap<>();
    private static final int POSITION_AVERAGE_COUNT = 5;
    private List<BlockPos> previousPositions;

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
                    int lightLevel = Math.max(1, (int)((float)ticksRemaining / LIGHT_FADE_DELAY * 15));
                    level.setBlock(pos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, lightLevel), 3);
                }
            }

            if (isEnabled && currentEnergy > 0 && (isSelected || player.getOffhandItem() == stack)) {
                HitResult hit = player.pick(LIGHT_RANGE, 0, false);
                if (hit.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockHit = (BlockHitResult) hit;
                    BlockPos targetPos = blockHit.getBlockPos();
                    BlockPos newLightPos = targetPos.relative(blockHit.getDirection());

                    if (level.getBlockState(newLightPos).isAir()) {
                        if (previousPositions == null) {
                            previousPositions = new ArrayList<>();
                        }

                        previousPositions.add(newLightPos);
                        if (previousPositions.size() > POSITION_AVERAGE_COUNT) {
                            previousPositions.remove(0);
                        }

                        BlockPos averagedPos = averagePositions(previousPositions);

                        if (lastLightPos != null && !lastLightPos.equals(averagedPos)) {
                            fadingLights.put(lastLightPos, LIGHT_FADE_DELAY);
                        }

                        if (level.getBlockState(averagedPos).isAir()) {
                            level.setBlock(averagedPos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15), 3);
                            lastLightPos = averagedPos;
                            ticksSinceLastLight = 0;

                            currentEnergy -= ENERGY_USAGE;
                            stack.set(PDataComponents.ENERGY.get(), currentEnergy);
                        }
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

    private BlockPos averagePositions(List<BlockPos> positions) {
        if (positions.isEmpty()) return null;

        double x = 0, y = 0, z = 0;
        for (BlockPos pos : positions) {
            x += pos.getX();
            y += pos.getY();
            z += pos.getZ();
        }

        return new BlockPos(
                (int) Math.round(x / positions.size()),
                (int) Math.round(y / positions.size()),
                (int) Math.round(z / positions.size())
        );
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