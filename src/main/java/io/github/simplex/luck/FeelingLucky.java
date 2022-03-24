package io.github.simplex.luck;

import io.github.simplex.luck.player.PlayerConfig;
import io.github.simplex.luck.player.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public final class FeelingLucky extends JavaPlugin {
    private static final Map<UUID, PlayerConfig> configMap = new HashMap<>();
    public PlayerHandler handler;

    public static Map<UUID, PlayerConfig> getConfigMap() {
        return configMap;
    }

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Initializing the PlayerHandler...");
        handler = new PlayerHandler(this);
        Bukkit.getLogger().info("Initialization complete! Attempting to register the handler...");
        this.getServer().getPluginManager().registerEvents(handler, this);
        Bukkit.getLogger().info("Registration complete! Attempting to load all player configuration files...");

        File[] files = getDataFolder().listFiles();
        if (files != null) {
            Arrays.stream(files).forEach(file -> {
                UUID uuid = UUID.fromString(file.getName().split("\\.")[0]);
                configMap.put(uuid, PlayerConfig.loadFrom(file));
            });
            configMap.forEach((u, pc) -> pc.load());
            getLogger().info("Successfully loaded all configurations!");
        } else {
            getLogger().info("There are no player configurations to load.");
        }

        Bukkit.getLogger().info("Successfully initialized!");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Saving all player configurations...");
        configMap.forEach((u, pc) -> pc.save());
    }
}
