package com.kanhaiya.xpbottle;

import com.kanhaiya.xpbottle.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class XPBottleClient implements ClientModInitializer {
    private static KeyBinding toggleKey;
    private static boolean isAutoThrowActive = false;
    private static long lastThrowTime = 0;
    private static ModConfig config;

    @Override
    public void onInitializeClient() {
        config = ModConfig.load();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.xpbottle.toggle",
            InputUtil.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_MIDDLE,
            "XP Bottle Auto Thrower"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            while (toggleKey.wasPressed()) {
                handleToggle(client);
            }

            if (isAutoThrowActive && config.enabled) {
                processAutoThrow(client);
            }
        });

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (config.showHud && isAutoThrowActive) {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null) {
                    String status = isAutoThrowActive ? "ON" : "OFF";
                    String hudText = String.format("Auto XP: %s | CPS: %d", status, config.cps);
                    
                    int x = 10;
                    int y = 10;
                    
                    drawContext.fill(x - 2, y - 2, x + client.textRenderer.getWidth(hudText) + 2, y + 10, 0x80000000);
                    drawContext.drawText(client.textRenderer, hudText, x, y, 0x55FF55, true);
                }
            }
        });
    }

    private void handleToggle(MinecraftClient client) {
        if (!config.enabled) {
            if (client.player != null) {
                client.player.sendMessage(Text.literal("§cAuto XP Thrower is disabled in config!"), true);
            }
            return;
        }

        isAutoThrowActive = !isAutoThrowActive;

        if (client.player != null) {
            String messageKey = isAutoThrowActive ? "text.xpbottle.enabled" : "text.xpbottle.disabled";
            Text message = Text.translatable(messageKey);
            
            client.player.sendMessage(message, true);
            
            client.getToastManager().add(new net.minecraft.client.toast.SystemToast(
                net.minecraft.client.toast.SystemToast.Type.NARRATOR_TOGGLE,
                Text.literal("XP Auto Thrower"),
                message
            ));
        }

        if (isAutoThrowActive) {
            lastThrowTime = System.currentTimeMillis();
        }
    }

    private void processAutoThrow(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null) return;

        ItemStack mainHandStack = client.player.getMainHandStack();
        
        if (!mainHandStack.isOf(Items.EXPERIENCE_BOTTLE)) {
            if (isAutoThrowActive) {
                isAutoThrowActive = false;
                client.player.sendMessage(Text.literal("§eAuto XP: Stopped (no bottles)"), true);
            }
            return;
        }

        long currentTime = System.currentTimeMillis();
        long delayMs = 1000L / config.cps;

        if (currentTime - lastThrowTime >= delayMs) {
            client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
            lastThrowTime = currentTime;
        }
    }

    public static ModConfig getConfig() {
        return config;
    }

    public static void setConfig(ModConfig newConfig) {
        config = newConfig;
        config.save();
    }

    public static boolean isActive() {
        return isAutoThrowActive;
    }
}