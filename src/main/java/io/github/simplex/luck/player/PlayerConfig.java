package io.github.simplex.luck.player;

import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.SneakyWorker;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PlayerConfig extends YamlConfiguration {
    private final File configFile;
    private volatile YamlConfiguration config;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public PlayerConfig(FeelingLucky plugin, Player player) {
        File dataFolder = new File(plugin.getDataFolder(), "players");
        if (!dataFolder.exists()) dataFolder.mkdirs();
        File file = new File(dataFolder, player.getUniqueId() + ".yml");
        if (!file.exists()) {
            SneakyWorker.sneakyTry(() -> {
                file.createNewFile();
                InputStreamReader reader = new InputStreamReader(plugin.getResource("default_player.yml"));
                loadConfiguration(reader).save(file);
            });
        }
        configFile = file;
        config = loadConfiguration(configFile);

        if (config.getString("username").equalsIgnoreCase("replace")) {
            config.set("username", player.getName());
            config.set("luck", new Luck(player).defaultValue());
            config.set("multiplier", "1.0");
            save();
        }
    }

    public void save() {
        SneakyWorker.sneakyTry(() -> config.save(configFile));
    }

    public void load() {
        SneakyWorker.sneakyTry(() -> config = loadConfiguration(configFile));
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}
