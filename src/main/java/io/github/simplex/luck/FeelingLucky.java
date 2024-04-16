package io.github.simplex.luck;

import io.github.simplex.luck.listener.*;
import io.github.simplex.luck.player.PlayerConfig;
import io.github.simplex.luck.player.PlayerHandler;
import io.github.simplex.luck.util.Logs;
import io.github.simplex.luck.util.LuckCMD;
import io.github.simplex.luck.util.RegenerateConfigCMD;
import io.github.simplex.luck.util.SpecialFootItem;
import io.github.simplex.metrics.Metrics;

import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.github.simplex.sql.MySQL;
import io.github.simplex.sql.Redis;
import io.github.simplex.sql.SQLType;
import io.github.simplex.sql.SQLite;
import net.kyori.adventure.chat.ChatType;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class FeelingLucky extends JavaPlugin {
    private final Map<UUID, PlayerConfig> configMap = new HashMap<>();
    private final File playerDirectory = new File(getDataFolder(), "players");
    private final SpecialFootItem specialFootItem = new SpecialFootItem();
    private final ChatType.Bound bind = ChatType.CHAT.bind(Component.text(getName()));
    private PlayerHandler handler;
    private Config config;

    private MySQL mysql;
    private SQLite sqlite;
    private Redis Redis;

    private boolean shouldLoadPhysical = false;

    public Map<UUID, PlayerConfig> getConfigMap() {
        return configMap;
    }

    @Override
    public void onEnable() {
        getLogger().info("Initializing metrics...");
        new Metrics(this, 15054);
        getLogger().info("Metrics loaded. Initializing SQL...");
        initSQL();
        getLogger().info("Initialization complete! Attempting to register the Listeners...");
        registerListeners();
        getLogger().info("Registration complete! Attempting to load all saved player configurations...");
        loadPlayerConfigurations();
        getLogger().info("Player configurations loaded! Initializing PlayerHandler...");
        handler = new PlayerHandler(this);
        getLogger().info("Attempting to load the main configuration...");
        config = new Config(this);
        getLogger().info("Main Config loaded successfully! Loading commands...");
        new LuckCMD(this);
        new RegenerateConfigCMD(this);
        getLogger().info("Successfully loaded all commands!");

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

    private void initSQL() {
        switch (config.getSQLType()) {
            case MYSQL -> {
                try {
                    mysql = new MySQL(this);
                } catch (Exception e) {
                    getLogger().severe("Failed to initialize MySQL. Falling back to standard plugin configuration.");
                    Logs.error(e);
                    shouldLoadPhysical(true);
                }
            }
            case SQLITE -> {
                try {
                    sqlite = new SQLite(this);
                } catch (SQLException e) {
                    getLogger().severe("Failed to initialize SQLite. Falling back to standard plugin configuration.");
                    Logs.error(e);
                    shouldLoadPhysical(true);
                }
            }
            case REDIS -> {
                Redis = new Redis(this);
            }
        }
    }

    private void saveToSQL() {
        switch (config.getSQLType()) {
            case MYSQL -> {
                mysql.savePlayers();
            }
            case SQLITE -> {
                try {
                    sqlite.savePlayers();
                } catch (Exception e) {
                    Logs.error(e);
                }
            }
            case REDIS -> {
                Redis = new Redis(this);
                Redis.savePlayers();
            }
        }
    }

    private void loadPlayersFromSQL() {
        switch (config.getSQLType()) {
            case MYSQL -> {
                mysql.loadPlayers();
            }
            case SQLITE -> {
                try {
                    sqlite.loadPlayers();
                } catch (SQLException e) {
                    Logs.error(e);
                }
            }
            case REDIS -> {
                Redis.loadPlayers();
            }
        }
    }

    private void loadPlayerConfigurations() {
        if (!playerDirectory.exists()) {
            getLogger().info("No directory exists. Creating...");
            playerDirectory.mkdirs();
            getLogger().info("Created new directory \"FeelingLucky/players\".");
            return;
        }

        if (config.getSQLType() != SQLType.NONE && !shouldLoadPhysical()) {
            loadPlayersFromSQL();
            getLogger().info("Successfully loaded all configurations from SQL!");
            return;
        }

        File[] files = playerDirectory.listFiles();
        if (files != null) {
            Arrays.stream(files).forEach(file ->
            {
                UUID uuid = UUID.fromString(file.getName().split("\\.")[0]);
                configMap.put(uuid, PlayerConfig.initFromFile(this, file));
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
        new GiveDamage(this);
        new HideCheck(this);
        new IllOmen(this);
        new ItemDrops(this);
        new JumpBoost(this);
        // new OreVein(this); (Currently unstable & unsafe).
        new PlayerListener(this);
        new RandomEffect(this);
        new RestoreHunger(this);
        new TakeDamage(this);
        new UnbreakableTool(this);
        new VillagerInventory(this);
    }

    public PlayerHandler getHandler() {
        return handler;
    }

    @Override
    @NotNull
    public Config getConfig() {
        return config;
    }

    public SpecialFootItem getFoot() {
        return specialFootItem;
    }

    private void shouldLoadPhysical(boolean state) {
        shouldLoadPhysical = state;
    }

    private boolean shouldLoadPhysical() {
        return shouldLoadPhysical;
    }

    public CommandMap getCommandMap() {
        return getServer().getCommandMap();
    }

    public ChatType.Bound bind() {
        return bind;
    }
}
