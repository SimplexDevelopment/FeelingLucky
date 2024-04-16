package io.github.simplex.luck.player;

import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.util.Logs;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PlayerConfig
{
    private final File configFile;
    private final OfflinePlayer player;
    private final FeelingLucky plugin;
    private YamlConfiguration config;


    public PlayerConfig(FeelingLucky plugin, Player player)
    {
        this.plugin = plugin;
        this.player = player;
        configFile = configFile(plugin, player);
        config = YamlConfiguration.loadConfiguration(configFile);

        String tempUsername = config.getString("username");

        if (tempUsername == null)
        {
            config.set("username", player.getName());
            config.set("luck", plugin.getHandler().getLuckContainer(player).getDefaultValue());
            config.set("multiplier", "1.0");
            config.set("verbose", plugin.getConfig().isVerboseGlobal());
            save();
        }
    }

    protected PlayerConfig(FeelingLucky plugin, File file)
    {
        this.plugin = plugin;
        this.configFile = file;
        this.player = Bukkit.getOfflinePlayer(UUID.fromString(file.getName().split("\\.")[0]));
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    protected PlayerConfig(FeelingLucky plugin, DynamicConfig user)
    {
        this.plugin = plugin;
        this.player = Bukkit.getOfflinePlayer(UUID.fromString(user.getPlayerUUID()));
        configFile = configFile(plugin, player);
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    @Contract("_, _ -> new")
    public static PlayerConfig initFromFile(FeelingLucky plugin, File file)
    {
        return new PlayerConfig(plugin, file);
    }

    public static PlayerConfig fromDynamicConfig(FeelingLucky plugin, DynamicConfig config)
    {
        PlayerConfig playerConfig = new PlayerConfig(plugin, config);
        playerConfig.setUsername(config.getPlayerUUID());
        playerConfig.setLuck(config.getLuckValue());
        playerConfig.setMultiplier(config.getMultiplier());
        playerConfig.setVerbose(config.isVerbose());
        return playerConfig;
    }

    public DynamicConfig toDynamicConfig()
    {
        DynamicConfig dynamicConfig = new DynamicConfig();
        dynamicConfig.setPlayerUUID(player.getUniqueId());
        dynamicConfig.setLuckValue(getLuck());
        dynamicConfig.setVerbose(isVerbose());
        dynamicConfig.setMultiplier(getMultiplier());
        return dynamicConfig;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @NotNull
    private File configFile(FeelingLucky plugin, OfflinePlayer player)
    {
        if (!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdirs();
        File dataFolder = new File(plugin.getDataFolder(), "players");
        if (!dataFolder.exists())
            dataFolder.mkdirs();
        File file = new File(dataFolder, player.getUniqueId() + ".yml");
        if (!file.exists())
        {
            try
            {
                file.createNewFile();
                final YamlConfiguration v0 = new YamlConfiguration();
                v0.set("username", player.getName());
                v0.set("luck", 0);
                v0.set("multiplier", 1.0);
                v0.set("verbose", plugin.getConfig().isVerboseGlobal());
                v0.save(file);
            }
            catch (IOException ex)
            {
                Logs.error(ex);
            }
        }
        return file;
    }

    public void save()
    {
        try
        {
            config.save(configFile);
        }
        catch (IOException ex)
        {
            Logs.error(ex);
        }
    }

    public void load()
    {
        try
        {
            config.load(configFile);
        }
        catch (IOException | InvalidConfigurationException ex)
        {
            Logs.error(ex);

            Logs.warn("Attempting to reinitialize variable... this is dangerous!");

            try
            {
                config = YamlConfiguration.loadConfiguration(configFile);
            }
            catch (IllegalArgumentException th)
            {
                Logs.error(th);
            }

        }
    }

    public void reload()
    {
        save();
        load();
    }

    public OfflinePlayer getPlayer()
    {
        return player;
    }

    public String getUsername() {
        return config.getString("username");
    }

    public double getLuck()
    {
        return config.getDouble("luck");
    }

    public void setLuck(double luck)
    {
        config.set("luck", luck);
        reload();
    }

    public double getMultiplier()
    {
        return config.getDouble("multiplier");
    }

    public void setMultiplier(double multiplier)
    {
        config.set("multiplier", multiplier);
        reload();
    }

    public boolean isVerbose()
    {
        return config.getBoolean("verbose");
    }

    public void setVerbose(final boolean verbose)
    {
        config.set("verbose", verbose);
        reload();
    }

    public void setUsername(String name)
    {
        config.set("username", name);
        reload();
    }

    public YamlConfiguration getConfig()
    {
        return config;
    }
}
