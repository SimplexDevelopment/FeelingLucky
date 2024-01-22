package io.github.simplex.luck.listener;

import io.github.simplex.lib.MiniComponent;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is currently unstable.
 */
@Deprecated
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
            getOresInArea(event.getBlock()).forEach(Block::breakNaturally);
            player.sendMessage(MiniComponent.info("Your luck has let you mine all the blocks with one swing."));
        }
    }

    public List<Block> getOresInArea(@NotNull Block block) {
        Stream.Builder<Block> streamBuilder = Stream.builder();
        Location start = block.getLocation();
        World world = block.getWorld();
        List<Tag<Material>> materialList = List.of(
                Tag.COAL_ORES, Tag.COPPER_ORES, Tag.DIAMOND_ORES,
                Tag.GOLD_ORES, Tag.IRON_ORES, Tag.EMERALD_ORES,
                Tag.LAPIS_ORES, Tag.REDSTONE_ORES
        );
        for (int x = start.getBlockX() - 15; x <= start.getBlockX() + 15; x++) {
            for (int y = start.getBlockY() - 15; y <= start.getBlockY() + 15; y++) {
                for (int z = start.getBlockZ() - 15; z <= start.getBlockZ() + 15; z++) {
                    Location location = new Location(world, x, y, z);
                    Material blockType = location.getBlock().getType();
                    if (materialList.stream().anyMatch(tag -> tag.isTagged(blockType))) {
                        streamBuilder.add(location.getBlock());
                    }
                }
            }
        }
        return streamBuilder.build().collect(Collectors.toList());
    }
}
