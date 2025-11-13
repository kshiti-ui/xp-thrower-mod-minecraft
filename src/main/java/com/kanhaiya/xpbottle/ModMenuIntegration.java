package com.kanhaiya.xpbottle.config;

/**
 * Mod Menu integration for XP Bottle Auto Thrower configuration screen
 * 
 * @author Kanhaiya
 * @license MIT License
 * @repository https://github.com/kshiti-ui/xp-thrower-mod-minecraft
 */

import com.kanhaiya.xpbottle.XPBottleClient;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ModConfig config = XPBottleClient.getConfig();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("XP Bottle Auto Thrower"))
                    .setSavingRunnable(config::save);

            var general = builder.getOrCreateCategory(Text.literal("General Settings"));
            var entryBuilder = builder.entryBuilder();

            // Enabled toggle
            general.addEntry(entryBuilder
                    .startBooleanToggle(Text.literal("Enabled by Default"), config.enabled)
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Whether the mod is enabled by default on startup"))
                    .setSaveConsumer(val -> config.enabled = val)
                    .build());

            // CPS slider
            general.addEntry(entryBuilder
                    .startIntSlider(Text.literal("Clicks per Second (CPS)"), config.cps, 1, 100)
                    .setDefaultValue(10)
                    .setTooltip(Text.literal("How many XP bottles per second are thrown"))
                    .setSaveConsumer(val -> config.cps = val)
                    .build());

            // HUD Settings Category
            var hudCategory = builder.getOrCreateCategory(Text.literal("HUD Settings"));

            // Show HUD toggle
            hudCategory.addEntry(entryBuilder
                    .startBooleanToggle(Text.literal("Show HUD"), config.showHud)
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Display the status HUD on screen"))
                    .setSaveConsumer(val -> config.showHud = val)
                    .build());

            // HUD X position
            hudCategory.addEntry(entryBuilder
                    .startIntField(Text.literal("HUD X Position"), config.hudX)
                    .setDefaultValue(5)
                    .setMin(0)
                    .setMax(2000)
                    .setTooltip(Text.literal("Horizontal position of the HUD (pixels from left)"))
                    .setSaveConsumer(val -> config.hudX = val)
                    .build());

            // HUD Y position
            hudCategory.addEntry(entryBuilder
                    .startIntField(Text.literal("HUD Y Position"), config.hudY)
                    .setDefaultValue(5)
                    .setMin(0)
                    .setMax(2000)
                    .setTooltip(Text.literal("Vertical position of the HUD (pixels from top)"))
                    .setSaveConsumer(val -> config.hudY = val)
                    .build());

            return builder.build();
        };
    }
}