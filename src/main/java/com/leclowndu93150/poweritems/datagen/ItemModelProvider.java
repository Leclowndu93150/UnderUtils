package com.leclowndu93150.poweritems.datagen;

import com.leclowndu93150.poweritems.PowerItems;
import com.leclowndu93150.poweritems.register.PItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

public class ItemModelProvider extends net.neoforged.neoforge.client.model.generators.ItemModelProvider {
    public ItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, PowerItems.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(PItems.BATTERY.get());
        basicItem(PItems.BATTERY_PACK.get());
        basicItem(PItems.NIGHT_VISION_GOGGLES.get());
        poweredItem(PItems.FLASHLIGHT.get());
        poweredItem(PItems.WINDUP_FLASHLIGHT.get());
    }

    public void poweredItem(Item item) {
        ResourceLocation location = BuiltInRegistries.ITEM.getKey(item);
        ResourceLocation is_on = ResourceLocation.fromNamespaceAndPath(PowerItems.MODID, "enabled");
        getBuilder(location.toString())
                .parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .override()
                .model(handHeldItem(item, "_off")
                        .texture("layer0", itemTexture(item, "_off"))
                )
                .predicate(is_on, 0)
                .end()
                .override()
                .model(handHeldItem(item, "_on")
                        .texture("layer0", itemTexture(item, "_on")))
                .predicate(is_on, 1)
                .end();
    }

    private void bucket(Fluid f) {
        withExistingParent(key(f.getBucket()).getPath(), ResourceLocation.fromNamespaceAndPath("neoforge", "item/bucket"))
                .customLoader(DynamicFluidContainerModelBuilder::begin)
                .fluid(f);
    }

    private static @NotNull ResourceLocation key(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem());
    }

    public ItemModelBuilder handHeldItem(Item item) {
        return handHeldItem(item, "");
    }

    public ItemModelBuilder handHeldItem(Item item, String suffix) {
        ResourceLocation location = BuiltInRegistries.ITEM.getKey(item);
        return getBuilder(location + suffix)
                .parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "item/" + location.getPath() + suffix));
    }

    private ResourceLocation itemTexture(Item item, String suffx) {
        ResourceLocation location = BuiltInRegistries.ITEM.getKey(item);
        return ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "item/" + location.getPath() + suffx);
    }

    public ItemModelBuilder basicItem(Item item, String suffix) {
        ResourceLocation location = BuiltInRegistries.ITEM.getKey(item);
        return getBuilder(item.toString() + suffix)
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "item/" + location.getPath()));
    }

    private String name(ItemLike item) {
        return key(item).getPath();
    }

    private ResourceLocation extend(ResourceLocation rl, String suffix) {
        return ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), rl.getPath() + suffix);
    }
}
