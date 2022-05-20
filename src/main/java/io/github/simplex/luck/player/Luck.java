package io.github.simplex.luck.player;

import io.github.simplex.api.LuckContainer;
import io.github.simplex.luck.FeelingLucky;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("all")
public class Luck implements LuckContainer {
    private final Player player;
    private final PlayerLuckChangeEvent event;
    private final FeelingLucky plugin;
    private final List<Player> markedPlayers = new ArrayList<>();
    private final Map<Player, Double> cache = new HashMap<>();
    private double BASE_VALUE;
    private double multiplier;
    private double tempSave;

    public Luck(FeelingLucky plugin, Player player) {
        this(plugin, player, 1.0);
    }

    public Luck(FeelingLucky plugin, Player player, double multiplier) {
        this.player = player;
        this.multiplier = multiplier;
        this.plugin = plugin;
        BASE_VALUE = plugin.getConfigMap().get(player.getUniqueId()).getConfig().getDouble("luck");
        tempSave = BASE_VALUE;

        event = new PlayerLuckChangeEvent(this);
    }

    @Contract(pure = true,
            value = "-> new")
    public static @NotNull SplittableRandom RNG() {
        return new SplittableRandom();
    }

    public static boolean quickRNGnoMultiplier(double value) {
        double rng;
        if (value >= 1024.0) {
            rng = 1024.0; // 100% chance to trigger, obviously;
        } else {
            rng = RNG().nextDouble(0.0, 1024.0);
        }

        double actual = Math.round((rng / 1024.0) * 100);

        return (value >= actual);
    }

    public FeelingLucky getPlugin() {
        return plugin;
    }

    public void markPlayer(Player player) {
        markedPlayers.add(player);
    }

    public void unmarkPlayer(Player player) {
        markedPlayers.remove(player);
    }

    public boolean isMarked(Player player) {
        return markedPlayers.contains(player);
    }

    @Override
    public boolean isMatch(double number) {
        return getValue() == number;
    }

    @Override
    public boolean isClose(double number, int range) {
        return ((getValue() - range <= number) && (number <= getValue() + range));
    }

    @Override
    public double multiplier() {
        return multiplier;
    }

    @Override
    public Player associatedPlayer() {
        return player;
    }

    public boolean quickRNG(double value) {
        double rng;
        if (value >= 1024.0) {
            rng = 1024.0; // 100% chance to trigger, obviously;
        } else {
            rng = RNG().nextDouble(0.0, 1024.0);
        }

        double actual = Math.round((rng / 1024) * 100);

        if (multiplier() > 1.0) {
            return ((value * multiplier()) >= actual);
        }

        return (value >= actual);
    }

    public void reset() {
        setValue(getDefaultValue());
    }

    @Override
    public double getValue() {
        return BASE_VALUE;
    }

    public void setValue(double value) {
        BASE_VALUE = value;
        plugin.getConfigMap().get(associatedPlayer().getUniqueId()).setLuck(value);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
        plugin.getConfigMap().get(associatedPlayer().getUniqueId()).setMultiplier(multiplier);
    }

    public void cache() {
        if (cache.containsKey(player)) {
            cache.replace(player, getValue());
            return;
        }

        cache.put(player, getValue());
    }

    public boolean cached(Player player) {
        return cache.containsKey(player);
    }

    public void restore() {
        if (!cache.containsKey(player)) {
            plugin.getLogger().info("Nothing to restore!");
            return;
        }

        setValue(cache.get(player));
        cache.remove(player);
    }

    public double getDefaultValue() {
        return 0;
    }

    public void addTo(double value) {
        if (value >= 1024.0 || (getValue() + value) >= 1024.0) {
            setValue(1024.0);
            return;
        }
        setValue(getValue() + value);
    }

    public void takeFrom(double value) {
        if (value <= -1024.0 || (getValue() - value) <= -1024.0) {
            setValue(-1024.0);
            return;
        }
        setValue(getValue() + value);
    }

    public boolean notDefault() {
        return getValue() != getDefaultValue();
    }

    public boolean lessThan(double value) {
        return getValue() < value;
    }

    public boolean greaterThan(double value) {
        return getValue() > value;
    }

    @Override
    public String toString() {
        return "" + getValue();
    }
}
