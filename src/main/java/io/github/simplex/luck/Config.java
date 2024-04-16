package io.github.simplex.luck;

import io.github.simplex.luck.listener.AbstractListener;
import io.github.simplex.luck.util.Logs;
import io.github.simplex.luck.util.SneakyWorker;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.github.simplex.sql.SQLType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class Config extends YamlConfiguration
{
    private final Map<String, Object> configEntries = new HashMap<>()
    {{
        put("high_rarity_chance", 512.0);
        put("medium_rarity_chance", 128.0);
        put("low_rarity_chance", 64.0);
        put("global_verbosity", true);
        put("block_drops", "LOW");
        put("bonemeal", "MED");
        put("cheat_death", "MED");
        put("enchanting", "HIGH");
        put("experience", "HIGH");
        put("give_damage", "LOW");
        put("hide_check", "MED");
        put("item_drops", "LOW");
        put("jump_boost", "MED");
        put("ore_vein", "HIGH");
        put("random_effect", "HIGH");
        put("restore_hunger", "NONE");
        put("take_damage", "MED");
        put("unbreakable", "HIGH");
    }};
    private final File configFile;

    public Config(FeelingLucky plugin)
    {
        File dataFolder = plugin.getDataFolder();
        if (dataFolder.mkdirs())
        {
            plugin.getLogger().info("Created new data folder. Writing new configuration file...");
            plugin.saveResource("config.yml", true);
        }

        File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists())
        {
            plugin.getLogger().info("No configuration file exists. Creating a new one...");
            plugin.saveResource("config.yml", true);
        }

        this.configFile = configFile;

        if (validateIntegrity(this.configFile))
        {
            load();
        }
        else
        {
            configEntries.forEach(super::set);
            Logs.warn("Your configuration file is missing keys. " +
                      "\nPlease use /rgc in the console to regenerate the config file. " +
                      "\nAlternatively, delete the config.yml and restart your server. " +
                      "\nIt is safe to ignore this, as default values will be used." +
                      "\nHowever, it is highly recommended to regenerate the configuration.");
        }
    }

    public void save()
    {
        SneakyWorker.sneakyTry(() -> save(configFile));
    }

    public void load()
    {
        SneakyWorker.sneakyTry(() -> load(configFile));
    }

    public void reload()
    {
        save();
        load();
    }

    public boolean validateIntegrity(@NotNull File fromDisk)
    {
        YamlConfiguration disk = YamlConfiguration.loadConfiguration(fromDisk);
        if (disk.getKeys(true).isEmpty())
        {
            return false;
        }

        boolean result = true;

        for (String key : configEntries.keySet())
        {
            if (!disk.getKeys(false).contains(key))
            {
                if (result)
                    result = false;
            }
        }
        return result;
    }

    public AbstractListener.Rarity getRarity(String name)
    {
        return AbstractListener.Rarity.valueOf(getString(name));
    }

    public double getChance(String path)
    {
        return getDouble(path);
    }

    public boolean isVerboseGlobal() {
        return getBoolean("global_verbosity");
    }

    public SQLType getSQLType() {
        return SQLType.fromString(Objects.requireNonNull(getString("database_type")));
    }

    public SQLiteWrapper getSQLite() {
        return new SQLiteWrapper();
    }

    public RedisWrapper getRedis() {
        return new RedisWrapper();
    }

    public MySQLWrapper getMySQL() {
        return new MySQLWrapper();
    }

    public final class SQLiteWrapper {
        private final String path;

        public SQLiteWrapper() {
            this.path = getString("sqlite.path");
        }

        public String getPath() {
            return path;
        }
    }

    public final class RedisWrapper {
        private final String host;
        private final String port;
        private final String password;
        private final int database;

        public RedisWrapper() {
            this.host = getString("redis.host");
            this.port = getString("redis.port");
            this.password = getString("redis.password");
            this.database = getInt("redis.database");
        }

        public String getHost() {
            return host;
        }

        public String getPort() {
            return port;
        }

        public String getPassword() {
            return password;
        }

        public int getDatabase() {
            return database;
        }
    }

    public final class MySQLWrapper {
        private final String host;
        private final int port;
        private final String database;
        private final String username;
        private final String password;

        public MySQLWrapper() {
            this.host = getString("mysql.host");
            this.port = getInt("mysql.port");
            this.database = getString("mysql.database");
            this.username = getString("mysql.username");
            this.password = getString("mysql.password");
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public String getDatabase() {
            return database;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
