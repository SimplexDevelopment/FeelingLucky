package io.github.simplex.luck.player;

import io.github.simplex.api.LuckContainer;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.SplittableRandom;

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
        event = new PlayerLuckChangeEvent(player);
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

    public SplittableRandom RNG() {
        return new SplittableRandom();
    }

    public boolean quickRNG(double percentage) {
        double rng;
        if (percentage > 99.0) {
            rng = RNG().nextDouble(100.0, 199.0);
        } else {
            rng = RNG().nextDouble(0.0, 99.0);
        }

        if (multiplier() > 0.0) {
            return ((percentage * multiplier()) >= rng);
        }

        return (percentage >= rng);
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
        Bukkit.getPluginManager().callEvent(event);
    }

    public void addTo(double value) {
        setValue(baseValue() + value);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void takeFrom(double value) {
        setValue(baseValue() - value);
        Bukkit.getPluginManager().callEvent(event);
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

    public boolean greaterThan (double value) {
        return baseValue() > value;
    }

    @Override
    public String toString() {
        return "" + baseValue();
    }
}
