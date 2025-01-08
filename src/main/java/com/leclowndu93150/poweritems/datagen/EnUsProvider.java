package com.leclowndu93150.poweritems.datagen;

import com.leclowndu93150.poweritems.PowerItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

import static com.leclowndu93150.poweritems.register.PItems.*;

public class EnUsProvider extends LanguageProvider {

    public EnUsProvider(PackOutput output, String locale) {
        super(output, PowerItems.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        addItem(WINDUP_FLASHLIGHT, "Wind-up Flashlight");
        addItem(FLASHLIGHT, "Flashlight");
        addItem(BATTERY, "Battery");
        addItem(BATTERY_PACK, "Battery Pack");
        addItem(NIGHT_VISION_GOGGLES, "Night Vision Goggles");

        //add(""
    }

    private void addItem(String key, String val) {
        add("item.poweritems." + key, val);
    }
}
