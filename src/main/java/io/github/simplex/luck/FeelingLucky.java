package io.github.simplex.luck;

import io.github.simplex.luck.listener.*;
import io.github.simplex.luck.player.PlayerConfig;
import io.github.simplex.luck.player.PlayerHandler;
import io.github.simplex.luck.util.LuckCMD;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class FeelingLucky extends JavaPlugin {
    private final Map<UUID, PlayerConfig> configMap = new HashMap<>();

    private PlayerHandler handler;

    public Map<UUID, PlayerConfig> getConfigMap() {
        return configMap;
    }

    @Override
    public void onEnable() {
        getLogger().info("Initializing the PlayerHandler...");
        handler = new PlayerHandler(this);
        getLogger().info("Initialization complete! Attempting to register the Listeners...");
        registerListeners();
        getLogger().info("Registration complete! Attempting to load all player configuration files...");
        loadConfigurations();
        Bukkit.getLogger().info("Attempting to load the Luck command...");
        new LuckCMD(this);
        Bukkit.getLogger().info("Successfully loaded the Luck command!");

        Bukkit.getLogger().info("Successfully initialized!");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Saving all player configurations...");
        configMap.values().forEach(PlayerConfig::save);
    }

    private void loadConfigurations() {
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
    }

    private void registerListeners() {
        new BlockDrops(this);
        new BonemealFullCrop(this);
        new CheatDeath(this);
        new EnchantmentBoost(this);
        new ExpBoost(this);
        new IllOmen(this);
        new ItemDrops(this);
        new PlayerListener(this);
        new RestoreHunger(this);
        new TakeDamage(this);
        new UnbreakableTool(this);
    }

    public PlayerHandler getHandler() {
        return handler;
    }
}
