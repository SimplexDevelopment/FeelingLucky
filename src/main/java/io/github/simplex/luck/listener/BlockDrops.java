package io.github.simplex.luck.listener;

import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import io.github.simplex.luck.util.SneakyWorker;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDropItemEvent;

import java.util.List;

public final class BlockDrops extends AbstractListener {
    public BlockDrops(FeelingLucky plugin) {
        super(plugin);
        register(this);
    }

    @EventHandler
    public void extraBlockDrops(BlockDropItemEvent event) {
        Player player = event.getPlayer();
        Luck luck = getHandler().getLuckContainer(player);
        List<Item> items = event.getItems();
        if (luck.quickRNG(luck.getValue()) && doesQualify("block_drops", luck.getValue())) {
            event.getItems().addAll(items.stream().map(SneakyWorker::move).toList());
        }
    }
}
