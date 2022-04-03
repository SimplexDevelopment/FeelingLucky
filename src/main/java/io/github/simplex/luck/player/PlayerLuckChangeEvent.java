package io.github.simplex.luck.player;

import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerLuckChangeEvent extends PlayerEvent {
    public final HandlerList handlerList = new HandlerList();

    public PlayerLuckChangeEvent(@NotNull Luck luck) {
        super(luck.associatedPlayer());

        if (luck.lessThan(0.0) && !luck.isMarked(luck.associatedPlayer())) {
            luck.markPlayer(luck.associatedPlayer());
            return;
        }

        if (luck.greaterThan(0.0) && luck.isMarked(luck.associatedPlayer())) {
            luck.unmarkPlayer(luck.associatedPlayer());
        }
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
