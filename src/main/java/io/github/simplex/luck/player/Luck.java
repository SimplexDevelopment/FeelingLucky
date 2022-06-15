package io.github.simplex.luck.player;

import io.github.simplex.api.LuckContainer;
import io.github.simplex.luck.FeelingLucky;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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

    /**
     * This creates a new instance of a pseudorandom number generator based off entropy provided by the operating system.
     * This will allow for a much purer randomization, due to entropy being different for each call.
     *
     * @return A new instance of SecureRandom. Each time this method is called a new instance is created to provide maximum variation with entropic calculations.
     */
    @Contract(pure = true,
            value = "-> new")
    public static @NotNull SecureRandom RNG() {
        return new SecureRandom(SecureRandom.getSeed(20));
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

    public boolean playerHasLuckPE() {
        return player.hasPotionEffect(PotionEffectType.LUCK);
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

    /**
     * Quickly calculate whether or not the player has enough luck to trigger the condition.
     *
     * @param value The players luck value.
     * @return True if the player meets the criteria, false if they do not.
     */
    public boolean quickRNG(double value) {
        double rng;
        if (value >= 1024.0) {
            rng = 1024.0; // 100% chance to trigger, obviously;
        } else {
            rng = RNG().nextDouble(0.0, 1024.0);
        }

        AtomicReference<Double> multiplier = new AtomicReference<>(multiplier());
        double actual = Math.round((rng / 1024) * 100);
        double newVal = Math.round((value / 1024) * 100);

        if (playerHasLuckPE()) {
            player.getActivePotionEffects()
                    .stream()
                    .filter(p -> p.getType().equals(PotionEffectType.LUCK))
                    .findFirst()
                    .ifPresent(p -> multiplier.updateAndGet(v -> new Double((double) (v + p.getAmplifier()))));
        }

        return ((newVal * multiplier.get()) >= actual);
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
