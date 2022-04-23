package io.github.simplex.luck.listener;

import io.github.simplex.luck.Config;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.PlayerHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractListener implements Listener {
    protected final FeelingLucky plugin;
    protected final Map<AbstractListener, Rarity> listenerRarityMap = new HashMap<>();
    protected final Config config;

    public AbstractListener(FeelingLucky plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    protected PlayerHandler getHandler() {
        return plugin.getHandler();
    }

    public enum Rarity {
        HIGH,
        MED,
        LOW,
        NONE
    }

    public boolean doesQualify(String name, double luck) {
        switch (config.getRarity(name)) {
            case HIGH:
                if (luck < config.getChance("high_rarity_chance")) return false;
            case MED:
                if (luck < config.getChance("medium_rarity_chance")) return false;
            case LOW:
                if (luck < config.getChance("low_rarity_chance")) return false;
            case NONE:
                return true;
        }
        throw new IllegalArgumentException("The value for the listener rarity is not a recognized rarity value.");
    }
}
