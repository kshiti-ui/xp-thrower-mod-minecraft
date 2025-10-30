package com.kanhaiya.xpbottle.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("xpbottle-autothrower.json");

    public boolean enabled = true;
    public int cps = 10;
    public boolean showHud = true;

    public static ModConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                return GSON.fromJson(json, ModConfig.class);
            } catch (IOException e) {
                System.err.println("Failed to load XP Bottle config: " + e.getMessage());
            }
        }
        
        ModConfig config = new ModConfig();
        config.save();
        return config;
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            String json = GSON.toJson(this);
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException e) {
            System.err.println("Failed to save XP Bottle config: " + e.getMessage());
        }
    }

    public void validate() {
        if (cps < 1) cps = 1;
        if (cps > 20) cps = 20;
    }
}