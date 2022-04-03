package io.github.simplex.luck.player;

import io.github.simplex.api.LuckContainer;
import io.github.simplex.luck.FeelingLucky;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

@SuppressWarnings("all")
public class Luck implements LuckContainer {
    private final Player player;
    private final double multiplier;
    private final double BASE_VALUE;
    private final PlayerLuckChangeEvent event;

    public Luck(Player player) {
        this(player, 1.0);
    }

    public Luck(Player player, double multiplier) {
        this.player = player;
        this.multiplier = multiplier;
        BASE_VALUE = player.getAttribute(Attribute.GENERIC_LUCK).getDefaultValue();
        event = new PlayerLuckChangeEvent(this);
    }

    private final List<Player> markedPlayers = new ArrayList<>();

    public void markPlayer(Player player) {
        markedPlayers.add(player);
    }

    public void unmarkPlayer(Player player) {
        markedPlayers.remove(player);
    }

    public boolean isMarked(Player player) {
        return markedPlayers.contains(player);
    }

    @Contract(pure = true,
            value = "-> new")
    public static @NotNull SplittableRandom RNG() {
        return new SplittableRandom();
    }

    public static boolean quickRNG2(double percentage) {
        double rng;
        if (percentage >= 100.0) {
            rng = 1024.0; // 100% chance to trigger, obviously;
        } else {
            rng = RNG().nextDouble(0.0, 1024.0);
        }

        double actual = (rng / 1024.0) * 100;

        return (percentage >= actual);
    }

    @Override
    public Attribute asAttribute() {
        return Attribute.GENERIC_LUCK;
    }

    @Override
    public double getNumber() {
        return associatedPlayer().getAttribute(asAttribute()).getValue();
    }

    @Override
    public boolean isMatch(double number) {
        return getNumber() == number;
    }

    @Override
    public boolean isClose(double number, int range) {
        return ((getNumber() - range <= number) && (number <= getNumber() + range));
    }

    @Override
    public double multiplier() {
        return multiplier;
    }

    @Override
    public Player associatedPlayer() {
        return player;
    }

    public boolean quickRNG(double percentage) {
        double rng;
        if (percentage >= 100.0) {
            rng = 1024.0; // 100% chance to trigger, obviously;
        } else {
            rng = RNG().nextDouble(0.0, 1024.0);
        }

        double actual = (rng / 1024) * 100;

        if (multiplier() > 1.0) {
            return ((percentage * multiplier()) >= actual);
        }

        return (percentage >= actual);
    }

    public void reset() {
        setValue(defaultValue());
    }

    @Override
    public double baseValue() {
        return BASE_VALUE;
    }

    public double defaultValue() {
        return player.getAttribute(Attribute.GENERIC_LUCK).getDefaultValue();
    }

    public void setValue(double value) {
        player.getAttribute(Attribute.GENERIC_LUCK).setBaseValue(value);
        FeelingLucky.getConfigMap().get(associatedPlayer().getUniqueId()).setLuck(value);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void addTo(double value) {
        if (value >= 1024.0 || (baseValue() + value) >= 1024.0) {
            setValue(1024.0);
            return;
        }
        setValue(baseValue() + value);
    }

    public void takeFrom(double value) {
        if (value <= -1024.0 || (baseValue() - value) <= -1024.0) {
            setValue(-1024.0);
            return;
        }
        setValue(baseValue() + value);
    }

    public double getPercentage() {
        return baseValue() - defaultValue();
    }

    public boolean notDefault() {
        return baseValue() != defaultValue();
    }

    public boolean lessThan(double value) {
        return baseValue() < value;
    }

    public boolean greaterThan(double value) {
        return baseValue() > value;
    }

    @Override
    public String toString() {
        return "" + baseValue();
    }
}
