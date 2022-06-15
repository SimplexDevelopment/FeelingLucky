package io.github.simplex.luck.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CancellablePlayerDeathEvent extends PlayerDeathEvent implements Cancellable {
    private boolean cancelled = false;

    public CancellablePlayerDeathEvent(@NotNull Player player, @NotNull List<ItemStack> drops, int droppedExp, @Nullable String deathMessage) {
        super(player, drops, droppedExp, deathMessage);
    }

    public CancellablePlayerDeathEvent(@NotNull Player player, @NotNull List<ItemStack> drops, int droppedExp, int newExp, @Nullable String deathMessage) {
        super(player, drops, droppedExp, newExp, deathMessage);
    }

    public CancellablePlayerDeathEvent(@NotNull Player player, @NotNull List<ItemStack> drops, int droppedExp, int newExp, int newTotalExp, int newLevel, @Nullable String deathMessage) {
        super(player, drops, droppedExp, newExp, newTotalExp, newLevel, deathMessage);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
