package io.github.simplex.luck.listener;

import io.github.simplex.lib.MiniComponent;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

@ApiStatus.Experimental
public class OreVein extends AbstractListener {

    public OreVein(FeelingLucky plugin) {
        super(plugin);
        register(this);
    }

    @EventHandler
    public void playerMine(@NotNull BlockBreakEvent event) {
        Player player = event.getPlayer();
        Luck luck = plugin.getHandler().getLuckContainer(player);
        if (luck.quickRNG(luck.getValue()) && doesQualify("ore_vein", luck.getValue()) && event.getBlock().isValidTool(player.getInventory().getItemInMainHand())) {
            Material minedBlockType = event.getBlock().getType();
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                getOresInArea(event.getBlock(), minedBlockType).forEach(Block::breakNaturally);
                player.sendMessage(MiniComponent.info("Your luck has let you mine all the blocks with one swing."));
            });
        }
    }

    public Set<Block> getOresInArea(@NotNull Block block, Material minedBlockType) {
        Set<Block> blocks = new HashSet<>();
        Queue<Block> queue = new LinkedList<>();
        Set<Location> visited = new HashSet<>();
        Location initialLocation = block.getLocation();

        queue.add(block);
        visited.add(initialLocation);

        while (!queue.isEmpty()) {
            Block currentBlock = queue.poll();
            Location currentLocation = currentBlock.getLocation();

            // Check if the current block is within the maximum radius from the initial block
            if (initialLocation.distance(currentLocation) > 16) {
                continue;
            }

            blocks.add(currentBlock);

            for (BlockFace face : BlockFace.values()) {
                Block neighbour = currentBlock.getRelative(face);
                Location neighbourLocation = neighbour.getLocation();

                if (!visited.contains(neighbourLocation) && neighbour.getType().equals(minedBlockType)) {
                    queue.add(neighbour);
                    visited.add(neighbourLocation);
                }
            }
        }

        return blocks;
    }
}