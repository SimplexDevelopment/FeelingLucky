package io.github.simplex.crumb;

import org.bukkit.plugin.java.JavaPlugin;

public final class Crumb extends JavaPlugin {
    public Config config;
    public PlayerHandler handler;

    @Override
    public void onEnable() {
        config = new Config(this, false);
        handler = new PlayerHandler(this);
        this.getServer().getPluginManager().registerEvents(handler, this);
    }

    @Override
    public void onDisable() {
        config.save();
        // Plugin shutdown logic
    }
}
