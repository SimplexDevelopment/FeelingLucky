package io.github.simplex.api;

import java.io.Serializable;
import org.bukkit.entity.Player;

public interface LuckContainer extends Serializable
{

    boolean isVerbose();

    void setVerbose(boolean verbose);

    boolean isMatch(double number);

    boolean isClose(double number, int range);

    double multiplier();

    Player associatedPlayer();

    double getValue();
}
