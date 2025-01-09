package com.monsoon.underutils.datagen;

import com.monsoon.underutils.UnderUtils;
import com.monsoon.underutils.register.PItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class EnUsProvider extends LanguageProvider {

    public EnUsProvider(PackOutput output, String locale) {
        super(output, UnderUtils.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        addItem(PItems.WINDUP_FLASHLIGHT, "Wind-up Flashlight");
        addItem(PItems.FLASHLIGHT, "Flashlight");
        addItem(PItems.BATTERY, "Battery");
        addItem(PItems.BATTERY_PACK, "Battery Pack");
        addItem(PItems.NIGHT_VISION_GOGGLES, "Night Vision Goggles");

    }

    private void addItem(String key, String val) {
        add("item.underutils." + key, val);
    }
}
