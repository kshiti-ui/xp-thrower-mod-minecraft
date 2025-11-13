package com.kanhaiya.xpbottle;

/**
 * XP Bottle Auto Thrower Mod
 * 
 * @author Kanhaiya
 * @version 1.0.0
 * @license MIT License
 * @repository https://github.com/kshiti-ui/xp-thrower-mod-minecraft
 * 
 * A Fabric mod for Minecraft that automatically throws XP bottles
 * with configurable CPS, HUD display, and smart auto-disable features.
 */

import com.kanhaiya.xpbottle.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class XPBottleClient implements ClientModInitializer {
    private static ModConfig config;
    private static KeyBinding toggleKey;
    private static boolean toggled = false;
    private static long lastThrow = 0;
    private static ItemStack lastHeldItem = ItemStack.EMPTY;

    @Override
    public void onInitializeClient() {
        config = ModConfig.load();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.xpbottle.toggle",
                InputUtil.Type.MOUSE,
                GLFW.GLFW_MOUSE_BUTTON_MIDDLE,
                "category.xpbottle"
        ));

        // Register HUD rendering - fixed signature for 1.21
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            renderHud(drawContext);
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            while (toggleKey.wasPressed()) {
                toggled = !toggled;
                client.player.sendMessage(
                        Text.literal("XP Bottle Auto Thrower " + (toggled ? "§aEnabled" : "§cDisabled")),
                        true
                );
            }

            // Check if player switched items or moved XP bottle
            if (toggled) {
                ItemStack currentItem = client.player.getMainHandStack();
                
                // If was holding XP bottle and now isn't, or switched to different item
                if (!lastHeldItem.isEmpty() && !ItemStack.areItemsEqual(lastHeldItem, currentItem)) {
                    if (lastHeldItem.isOf(Items.EXPERIENCE_BOTTLE)) {
                        toggled = false;
                        client.player.sendMessage(
                                Text.literal("XP Bottle Auto Thrower §cDisabled §7(item switched)"),
                                true
                        );
                    }
                }
                
                lastHeldItem = currentItem.copy();
            }

            if (toggled && config.enabled && client.player != null) {
                if (System.currentTimeMillis() - lastThrow >= (1000.0 / config.cps)) {
                    if (client.player.getMainHandStack().isOf(Items.EXPERIENCE_BOTTLE)) {
                        client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
                        lastThrow = System.currentTimeMillis();
                    }
                }
            }
        });
    }

    private void renderHud(DrawContext drawContext) {
        if (!config.showHud) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int x = config.hudX;
        int y = config.hudY;
        
        String status = toggled ? "§aON" : "§cOFF";
        String text = "XP Bottle: " + status;
        
        // Draw background
        drawContext.fill(x - 2, y - 2, x + 90, y + 12, 0x80000000);
        
        // Draw text - fixed signature for 1.21
        drawContext.drawText(
                client.textRenderer,
                Text.literal(text),
                x,
                y,
                0xFFFFFF,
                true
        );
    }

    public static ModConfig getConfig() {
        return config;
    }

    public static void setConfig(ModConfig newConfig) {
        config = newConfig;
        config.save();
    }
}
