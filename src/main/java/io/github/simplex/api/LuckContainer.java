package io.github.simplex.api;

import java.io.Serializable;
import org.bukkit.entity.Player;

/**
 * The LuckContainer interface represents a container for player luck.
 * It provides methods to get and set the verbosity of the luck container,
 * check if a number matches or is close to the luck value, get the multiplier,
 * get the associated player, and get the luck value.
 *
 * This interface is Serializable, which means it can be written to a stream
 * and restored.
 */
public interface LuckContainer extends Serializable {

    /**
     * Checks if the luck container is verbose.
     *
     * @return true if the luck container is verbose, false otherwise.
     */
    boolean isVerbose();

    /**
     * Sets the verbosity of the luck container.
     *
     * @param verbose the verbosity to set.
     */
    void setVerbose(boolean verbose);

    /**
     * Checks if a number matches the luck value.
     *
     * @param number the number to check.
     * @return true if the number matches the luck value, false otherwise.
     */
    boolean isMatch(double number);

    /**
     * Checks if a number is close to the luck value within a certain range.
     *
     * @param number the number to check.
     * @param range the range within which the number is considered close.
     * @return true if the number is close to the luck value, false otherwise.
     */
    boolean isClose(double number, int range);

    /**
     * Gets the multiplier of the luck container.
     *
     * @return the multiplier.
     */
    double multiplier();

    /**
     * Gets the player associated with the luck container.
     *
     * @return the associated player.
     */
    Player associatedPlayer();

    /**
     * Gets the luck value of the luck container.
     *
     * @return the luck value.
     */
    double getValue();
}