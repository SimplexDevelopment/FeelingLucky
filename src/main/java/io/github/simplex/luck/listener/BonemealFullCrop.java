package io.github.simplex.luck.listener;

import io.github.simplex.lib.ItemBuilder;
import io.github.simplex.lib.MiniComponent;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public record BonemealFullCrop(FeelingLucky plugin) implements Listener {
    public BonemealFullCrop {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void bonemealFullCrop(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack bonemeal = ItemBuilder.of(Material.BONE_MEAL).build();
        Luck luck = plugin.getHandler().getLuckContainer(player);

        ItemStack handItem = event.getItem();
        if (handItem == null) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        BlockData data = block.getBlockData();

        if (action.isRightClick()
                && handItem.isSimilar(bonemeal)
                && (data instanceof Ageable crop)
                && luck.quickRNG(luck.getPercentage())) {
            crop.setAge(crop.getMaximumAge());
            data.merge(crop);
            block.setBlockData(data);
            player.sendMessage(MiniComponent.info("You got lucky and your crops grew to maturity."));
        }
    }

}
