package io.github.simplex.api;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public interface LuckContainer {
    Attribute asAttribute();

    double getNumber();

    boolean isMatch(double number);

    boolean isClose(double number, int range);

    double multiplier();

    Player associatedPlayer();

    double baseValue();
}
