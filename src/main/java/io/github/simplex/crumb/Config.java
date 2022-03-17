package io.github.simplex.crumb;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config extends YamlConfiguration {
    private final String CONFIG_NAME = "config.yml";

    private final File dataFolder;
    private final Crumb plugin;
    private final File configFile;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Config(Crumb plugin, boolean copyResource) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdirs();
        File file = new File(dataFolder, CONFIG_NAME);
        if (!file.exists()) {
            SneakyWorker.sneakyTry(() -> {
                file.createNewFile();
                plugin.saveResource(CONFIG_NAME, true);
            });
        }
        if (copyResource) plugin.saveResource(CONFIG_NAME, true);
        configFile = file;
        loadConfiguration(configFile);
    }

    public void save() {
        SneakyWorker.sneakyTry(() -> super.save(configFile));
    }

    public void load() {
        SneakyWorker.sneakyTry(() -> {
            super.load(configFile);
        });
    }
}
