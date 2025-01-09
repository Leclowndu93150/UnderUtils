package com.monsoon.underutils.register;

import com.mojang.serialization.Codec;
import com.monsoon.underutils.UnderUtils;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PDataComponents {
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(UnderUtils.MODID);

    public static final Supplier<DataComponentType<Integer>> TIME =
            COMPONENTS.registerComponentType("time",
                    builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT));

    public static final Supplier<DataComponentType<Boolean>> ENABLED =
            COMPONENTS.registerComponentType("enabled",
                    builder -> builder
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL));

    public static final Supplier<DataComponentType<ComponentBatteryStorage>> BATTERY =
            COMPONENTS.registerComponentType("battery",
            builder -> builder
                    .persistent(ComponentBatteryStorage.CODEC)
                    .networkSynchronized(ComponentBatteryStorage.STREAM_CODEC));
}