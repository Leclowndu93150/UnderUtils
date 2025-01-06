package com.leclowndu93150.poweritems.register;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.leclowndu93150.poweritems.PowerItems.MODID;

public class PDataComponents {
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(MODID);

    public static final Supplier<DataComponentType<Integer>> TIME =
            COMPONENTS.registerComponentType("time",
                    builder -> builder.persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.INT));

    public static final Supplier<DataComponentType<Boolean>> ENABLED =
            COMPONENTS.registerComponentType("enabled",
                    builder -> builder.persistent(Codec.BOOL)
                            .networkSynchronized(ByteBufCodecs.BOOL));
}