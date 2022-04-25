package io.github.simplex.luck;

import io.github.simplex.luck.listener.*;
import io.github.simplex.luck.player.PlayerConfig;
import io.github.simplex.luck.player.PlayerHandler;
import io.github.simplex.luck.util.LuckCMD;
import io.github.simplex.luck.util.SneakyWorker;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class FeelingLucky extends JavaPlugin {
    private final Map<UUID, PlayerConfig> configMap = new HashMap<>();

    private PlayerHandler handler;
    private Config config;

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
        loadPlayerConfigurations();
        getLogger().info("Attempting to load the main configuration...");
        config = new Config(this);
        getLogger().info("Main Config loaded successfully! Attempting to load the Luck command...");
        new LuckCMD(this);
        getLogger().info("Successfully loaded the Luck command!");

        getLogger().info("Successfully initialized!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Saving all player configurations...");
        configMap.values().forEach(PlayerConfig::save);
        getLogger().info("Complete! Saving the main config...");
        config.save();
        getLogger().info("Complete! Goodbye! :)");
    }

    private void loadPlayerConfigurations() {
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
        try {
            Class<?>[] listeners = SneakyWorker.getClasses("io.github.simplex.luck.listener");
            Arrays.stream(listeners).forEach(l -> {
                if (AbstractListener.class.isAssignableFrom(l)) {
                    SneakyWorker.sneakyTry(() -> l.getDeclaredConstructor(FeelingLucky.class).newInstance(this));
                }
            });
        } catch (IOException | ClassNotFoundException ex) {
            getLogger().severe(ex.getMessage());
        }
    }

    public PlayerHandler getHandler() {
        return handler;
    }

    @Override
    @NotNull
    public Config getConfig() {
        return config;
    }
}
