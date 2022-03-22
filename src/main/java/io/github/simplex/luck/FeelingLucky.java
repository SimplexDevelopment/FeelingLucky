package io.github.simplex.luck;

import io.github.simplex.luck.player.PlayerConfig;
import io.github.simplex.luck.player.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class FeelingLucky extends JavaPlugin {
    private static final List<PlayerConfig> configList = new ArrayList<>();
    public PlayerHandler handler;

    public static List<PlayerConfig> getConfigList() {
        return configList;
    }

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Initializing the PlayerHandler...");
        handler = new PlayerHandler(this);
        Bukkit.getLogger().info("Initialization complete! Attempting to register the handler...");
        this.getServer().getPluginManager().registerEvents(handler, this);
        Bukkit.getLogger().info("Registration complete! Attempting to load all player configuration files...");
        if (getDataFolder().listFiles() != null) {
            Arrays.stream(getDataFolder().listFiles()).forEach(file -> {
                configList.add(PlayerConfig.loadFrom(file));
            });
            configList.forEach(PlayerConfig::load);
            getLogger().info("Successfully loaded all configurations!");
        } else {
            getLogger().info("There are no player configurations to load.");
        }
        Bukkit.getLogger().info("Successfully initialized!");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Saving all player configurations...");
        configList.forEach(PlayerConfig::save);
    }
}
