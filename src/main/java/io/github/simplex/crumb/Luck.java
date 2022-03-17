package io.github.simplex.crumb;

import io.github.simplex.api.LuckContainer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.SplittableRandom;

public class Luck implements LuckContainer {
    private final Player player;
    private final double multiplier;
    private final double BASE_VALUE;

    public Luck(Player player) {
        this.player = player;
        multiplier = 1.0;
        BASE_VALUE = player.getAttribute(Attribute.GENERIC_LUCK).getDefaultValue();
    }

    public Luck(Player player, double multiplier) {
        this.player = player;
        this.multiplier = multiplier;
        BASE_VALUE = player.getAttribute(Attribute.GENERIC_LUCK).getDefaultValue();
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
            return (percentage >= (rng * multiplier()));
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
    }

    public void addTo(double value) {
        setValue(baseValue() + value);
    }

    public void takeFrom(double value) {
        setValue(baseValue() - value);
    }

    public double getPercentage() {
        return baseValue() - defaultValue();
    }

    public boolean isDefault() {
        return baseValue() == defaultValue();
    }

    @Override
    public String toString() {
        return "" + baseValue();
    }
}
