package io.github.simplex.luck.listener;

import io.github.simplex.luck.Config;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.PlayerHandler;
import org.bukkit.event.Listener;

public abstract class AbstractListener implements Listener {
    protected final FeelingLucky plugin;
    protected final Config config;

    public AbstractListener(FeelingLucky plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    protected PlayerHandler getHandler() {
        return plugin.getHandler();
    }

    public boolean doesQualify(String name, double luck) {
        return switch (config.getRarity(name)) {
            case HIGH -> luck > config.getChance("high_rarity_chance");
            case MED -> luck > config.getChance("medium_rarity_chance");
            case LOW -> luck > config.getChance("low_rarity_chance");
            case NONE -> true;
        };
    }

    public enum Rarity {
        HIGH,
        MED,
        LOW,
        NONE
    }
}
