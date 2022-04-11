package io.github.simplex.luck.listener;

import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.util.SneakyWorker;
import io.github.simplex.luck.player.Luck;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;

import java.util.List;

public record BlockDrops(FeelingLucky plugin) implements Listener {
    public BlockDrops {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void extraBlockDrops(BlockDropItemEvent event) {
        Player player = event.getPlayer();
        Luck luck = plugin.handler.getLuckContainer(player);
        List<Item> items = event.getItems();
        if (luck.quickRNG(luck.getPercentage())) {
            items.forEach(SneakyWorker::move);
        }
    }
}
