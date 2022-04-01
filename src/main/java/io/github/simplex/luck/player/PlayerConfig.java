package io.github.simplex.luck.player;

import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.SneakyWorker;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;

public class PlayerConfig {
    private final File configFile;
    private volatile YamlConfiguration config;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public PlayerConfig(FeelingLucky plugin, Player player) {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdirs();
        File file = new File(dataFolder, player.getUniqueId() + ".yml");
        if (!file.exists()) {
            String name = "username: " + player.getName();
            String luck = "luck: " + PlayerHandler.getLuckContainer(player).defaultValue();
            String multiplier = "multiplier: " + 1.0;

            SneakyWorker.sneakyTry(() -> {
                file.createNewFile();

                BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8));
                writer.write(name);
                writer.newLine();
                writer.write(luck);
                writer.newLine();
                writer.write(multiplier);
                writer.close();
            });
        }
        configFile = file;
        config = YamlConfiguration.loadConfiguration(configFile);

        String tempUsername = config.getString("username");

        if (tempUsername != null && tempUsername.equalsIgnoreCase("replace")) {
            config.set("username", player.getName());
            config.set("luck", PlayerHandler.getLuckContainer(player).defaultValue());
            config.set("multiplier", "1.0");
            save();
        }
    }

    protected PlayerConfig(File file) {
        this.configFile = file;
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    @Contract("_ -> new")
    public static PlayerConfig loadFrom(File file) {
        return new PlayerConfig(file);
    }

    public void save() {
        SneakyWorker.sneakyTry(() -> config.save(configFile));
    }

    public void load() {

        SneakyWorker.sneakyTry(() -> config = YamlConfiguration.loadConfiguration(configFile));
    }

    public void setLuck(double luck) {
        config.set("luck", luck);
        save();
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}
