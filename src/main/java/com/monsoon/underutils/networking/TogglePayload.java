package com.monsoon.underutils.networking;

import com.monsoon.underutils.UnderUtils;
import com.monsoon.underutils.items.BatteryPackItem;
import com.monsoon.underutils.items.FlashlightItem;
import com.monsoon.underutils.items.NightVisionGogglesItem;
import com.monsoon.underutils.items.WindupFlashlightItem;
import com.monsoon.underutils.register.PDataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record TogglePayload(String equipment_slot) implements CustomPacketPayload {
    public static final Type<TogglePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(UnderUtils.MODID, "key_pressesd_paylad"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TogglePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            TogglePayload::equipment_slot,
            TogglePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void keyPressedAction(TogglePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            context.player().getServer().sendSystemMessage(Component.literal("Received payload for slot: " + payload.equipment_slot));
            Player player = context.player();
            ItemStack stack = player.getItemBySlot(EquipmentSlot.byName(payload.equipment_slot()));
            Item item = stack.getItem();
            if (item instanceof WindupFlashlightItem || item instanceof FlashlightItem || item instanceof NightVisionGogglesItem || item instanceof BatteryPackItem) {
                stack.set(PDataComponents.ENABLED.get(), !stack.get(PDataComponents.ENABLED.get()).booleanValue());

                context.player().level().playSound(null, context.player().getX(), context.player().getY(), context.player().getZ(),
                        SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS,
                        0.5F, 0.4F / (context.player().level().getRandom().nextFloat() * 0.4F + 0.8F));
            }
        }).exceptionally(e -> {
            context.disconnect(Component.literal("action failed:  " + e.getMessage()));
            return null;
        });

    }
}