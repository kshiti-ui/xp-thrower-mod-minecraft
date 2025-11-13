package com.kanhaiya.xpbottle.config;

/**
 * Configuration manager for XP Bottle Auto Thrower Mod
 * 
 * @author Kanhaiya
 * @license MIT License
 * @repository https://github.com/kshiti-ui/xp-thrower-mod-minecraft
 */

import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.*;
import java.io.*;
import com.google.gson.*;

public class ModConfig {
    public boolean enabled = true;
    public int cps = 10;
    public boolean showHud = true;
    public int hudX = 5;
    public int hudY = 5;
    
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("xpbottle.json");

    public static ModConfig load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                    return gson.fromJson(reader, ModConfig.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModConfig();
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                gson.toJson(this, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
