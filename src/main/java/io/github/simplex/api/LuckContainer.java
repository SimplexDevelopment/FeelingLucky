package io.github.simplex.api;

import org.bukkit.entity.Player;

import java.io.Serializable;

public interface LuckContainer extends Serializable {

    boolean isMatch(double number);

    boolean isClose(double number, int range);

    double multiplier();

    Player associatedPlayer();

    double getValue();
}
