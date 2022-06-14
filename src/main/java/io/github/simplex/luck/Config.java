package io.github.simplex.luck;

import io.github.simplex.luck.listener.AbstractListener;
import io.github.simplex.luck.util.SneakyWorker;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Config extends YamlConfiguration {
    private final Map<String, Object> configEntries = new HashMap<>() {{
        put("high_rarity_chance", 512.0);
        put("medium_rarity_chance", 128.0);
        put("low_rarity_chance", 64.0);
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
    private File configFile;

    public Config(FeelingLucky plugin) {
        File dataFolder = plugin.getDataFolder();
        if (dataFolder.mkdirs()) {
            plugin.getLogger().info("Created new data folder. Writing new configuration file...");
            plugin.saveResource("config.yml", true);
        }

        File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) {
            plugin.getLogger().info("No configuration file exists. Creating a new one...");
            plugin.saveResource("config.yml", true);
        }

        this.configFile = configFile;

        if (validateIntegrity(this.configFile)) {
            load();
        } else {
            configEntries.forEach(super::set);
            plugin.getLogger().warning("Your configuration file is missing keys. " +
                    "\nPlease use /rgc in the console to regenerate the config file. " +
                    "\nAlternatively, delete the config.yml and restart your server. " +
                    "\nIt is safe to ignore this, as default values will be used." +
                    "\nHowever, it is highly recommended to regenerate the configuration.");
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

    public boolean validateIntegrity(@NotNull File fromDisk) {
        YamlConfiguration disk = YamlConfiguration.loadConfiguration(fromDisk);
        if (disk.getKeys(true).size() <= 0) {
            return false;
        }

        boolean result = true;

        for (String key : configEntries.keySet()) {
            if (!disk.getKeys(false).contains(key)) {
                if (result) result = false;
            }
        }
        return result;
    }

    public AbstractListener.Rarity getRarity(String name) {
        return AbstractListener.Rarity.valueOf(getString(name));
    }

    public double getChance(String path) {
        return getDouble(path);
    }
}
