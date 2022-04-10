package io.github.simplex.luck;

import io.github.simplex.luck.listener.PlayerListener;
import io.github.simplex.luck.player.Luck;
import io.github.simplex.luck.player.PlayerConfig;
import io.github.simplex.luck.player.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public final class FeelingLucky extends JavaPlugin {
    private final Map<UUID, PlayerConfig> configMap = new HashMap<>();
    private final Map<OfflinePlayer, Luck> offlinePlayerLuckMap = new HashMap<>();
    public LuckCMD cmd;
    public PlayerHandler handler;
    public PlayerListener playerListener;

    public static FeelingLucky plugin = getPlugin(FeelingLucky.class);

    public Map<UUID, PlayerConfig> getConfigMap() {
        return configMap;
    }

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Initializing the PlayerHandler...");
        handler = new PlayerHandler(this);
        Bukkit.getLogger().info("Initialization complete! Attempting to register the Listeners...");
        playerListener = new PlayerListener(this);
        Bukkit.getLogger().info("Registration complete! Attempting to load all player configuration files...");

        File[] files = getDataFolder().listFiles();
        if (files != null) {
            Arrays.stream(files).forEach(file -> {
                UUID uuid = UUID.fromString(file.getName().split("\\.")[0]);
                configMap.put(uuid, PlayerConfig.loadFrom(this, file));
            });
            configMap.forEach((u, pc) -> pc.load());
            getLogger().info("Successfully loaded all configurations!");
        } else {
            getLogger().info("There are no player configurations to load.");
        }

        Bukkit.getLogger().info("Attempting to load the Luck command...");
        cmd = new LuckCMD(this);
        Bukkit.getLogger().info("Successfully loaded the Luck command!");

        Bukkit.getLogger().info("Successfully initialized!");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Saving all player configurations...");
        configMap.values().forEach(PlayerConfig::save);
    }
}
