package io.github.simplex.luck;

import io.github.simplex.lib.MiniComponent;
import io.github.simplex.luck.listener.AbstractListener;
import io.github.simplex.luck.util.SneakyWorker;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Config extends YamlConfiguration {
    private final FeelingLucky plugin;
    private File configFile;

    public Config(FeelingLucky plugin) {
        this.plugin = plugin;

        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdirs();
        File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) {
            SneakyWorker.sneakyTry(configFile::createNewFile);
            plugin.saveResource("config.yml", true);
        }

        this.configFile = configFile;
        load();

        if (validateIntegrity()) {
            File newFile = new File(plugin.getDataFolder(), "config.yml");
            SneakyWorker.sneakyTry(() -> {
                Files.delete(Path.of(plugin.getDataFolder().getPath()));
                newFile.createNewFile();
                plugin.saveResource("config.yml", true);
            });
            this.configFile = newFile;
            load();
        }
    }

    public void save() {
        SneakyWorker.sneakyTry(() -> save(configFile));
    }

    public void load() {
        SneakyWorker.sneakyTry(() -> load(configFile));
    }

    public void reload() {
        save();
        load();
    }

    public boolean validateIntegrity() {
        for (String key : getKeys(false)) {
            if (!configEntries.contains(key)) {
                plugin.getLogger().severe("The contents of your configuration file is corrupted! Regenerating a new configuration file...");
                return true;
            }
        }
        return false;
    }

    public AbstractListener.Rarity getRarity(String name) {
        return AbstractListener.Rarity.valueOf(getString(name));
    }

    public double getChance(String path) {
        return getDouble(path);
    }

    private List<String> configEntries = new ArrayList<>() {{
        add("high_rarity_chance");
        add("medium_rarity_chance");
        add("low_rarity_chance");
        add("block_drops");
        add("bonemeal");
        add("cheat_death");
        add("enchanting");
        add("experience");
        add("item_drops");
        add("random_effect");
        add("restore_hunger");
        add("take_damage");
        add("unbreakable");
    }};
}
