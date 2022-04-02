package io.github.simplex.luck.player;

import io.github.simplex.luck.FeelingLucky;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerLuckChangeEvent extends PlayerEvent {
    public final HandlerList handlerList = new HandlerList();

    public PlayerLuckChangeEvent(@NotNull Player who) {
        super(who);
        Luck luck = PlayerHandler.getLuckContainer(who);
        if (luck.lessThan(0.0) && !PlayerHandler.isMarked(who)) {
            PlayerHandler.markPlayer(who);
            return;
        }

        if (luck.greaterThan(0.0) && PlayerHandler.isMarked(who)) {
            PlayerHandler.unmarkPlayer(who);
        }
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
