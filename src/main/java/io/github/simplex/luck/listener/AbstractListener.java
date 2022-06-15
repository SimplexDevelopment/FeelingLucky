package io.github.simplex.luck.listener;

import io.github.simplex.luck.Config;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.PlayerHandler;
import org.bukkit.event.Listener;

public abstract class AbstractListener implements Listener {
    protected final FeelingLucky plugin;

    public AbstractListener(FeelingLucky plugin) {
        this.plugin = plugin;
    }

    protected PlayerHandler getHandler() {
        return plugin.getHandler();
    }

    public void register(AbstractListener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public boolean doesQualify(String name, double luck) {
        return switch (plugin.getConfig().getRarity(name)) {
            case HIGH -> luck > plugin.getConfig().getChance("high_rarity_chance");
            case MED -> luck > plugin.getConfig().getChance("medium_rarity_chance");
            case LOW -> luck > plugin.getConfig().getChance("low_rarity_chance");
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
