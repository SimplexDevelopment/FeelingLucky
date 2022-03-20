package io.github.simplex.luck;

import io.github.simplex.luck.player.PlayerConfig;
import io.github.simplex.luck.player.PlayerHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class FeelingLucky extends JavaPlugin {
    public PlayerHandler handler;
    private static final List<PlayerConfig> configList = new ArrayList<>();

    @Override
    public void onEnable() {
        handler = new PlayerHandler(this);
        configList.forEach(PlayerConfig::load);
        this.getServer().getPluginManager().registerEvents(handler, this);
    }

    @Override
    public void onDisable() {
        configList.forEach(PlayerConfig::save);
    }

    public static List<PlayerConfig> getConfigList() {
        return configList;
    }
}
