package io.github.simplex.luck.player;

import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.util.SneakyWorker;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PlayerConfig {
    private final File configFile;
    private final OfflinePlayer player;
    private volatile YamlConfiguration config;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public PlayerConfig(FeelingLucky plugin, Player player) {
        this.player = player;
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        File dataFolder = new File(plugin.getDataFolder(), "players");
        if (!dataFolder.exists()) dataFolder.mkdirs();
        File file = new File(dataFolder, player.getUniqueId() + ".yml");
        if (!file.exists()) {
            String name = "username: " + player.getName();
            String luck = "luck: " + player.getAttribute(Attribute.GENERIC_LUCK).getDefaultValue();
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

        if (tempUsername == null) {
            config.set("username", player.getName());
            config.set("luck", plugin.getHandler().getLuckContainer(player).getDefaultValue());
            config.set("multiplier", "1.0");
            save();
        }
    }

    protected PlayerConfig(FeelingLucky plugin, File file) {
        this.configFile = file;
        this.player = Bukkit.getOfflinePlayer(UUID.fromString(file.getName().split("\\.")[0]));
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    @Contract("_, _ -> new")
    public static PlayerConfig loadFrom(FeelingLucky plugin, File file) {
        return new PlayerConfig(plugin, file);
    }

    public void save() {
        SneakyWorker.sneakyTry(() -> config.save(configFile));
    }

    public void load() {
        SneakyWorker.sneakyTry(() -> config = YamlConfiguration.loadConfiguration(configFile));
    }

    public void reload() {
        save();
        load();
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public void setUsername(String name) {
        config.set("username", name);
        save();
    }

    public void setLuck(double luck) {
        config.set("luck", luck);
        save();
    }

    public void setMultiplier(double multiplier) {
        config.set("multiplier", multiplier);
        save();
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}
