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

import java.util.stream.Stream;

public class OreVein extends AbstractListener {

    public OreVein(FeelingLucky plugin) {
        super(plugin);
    }

    @EventHandler
    public void playerMine(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Luck luck = plugin.getHandler().getLuckContainer(player);
        if (luck.quickRNG(luck.getPercentage()) && event.getBlock().isValidTool(player.getInventory().getItemInMainHand())) {
            getOresInArea(event.getBlock()).forEach(Block::breakNaturally);
            player.sendMessage(MiniComponent.info("Your luck has let you mine all the blocks with one swing."));
        }
    }

    public Stream<Block> getOresInArea(Block block) {
        Stream.Builder<Block> streamBuilder = Stream.builder();
        Location start = block.getLocation();
        World world = block.getWorld();
        Stream<Tag<Material>> materialStream = Stream.of(Tag.COAL_ORES, Tag.COPPER_ORES, Tag.DIAMOND_ORES, Tag.GOLD_ORES, Tag.IRON_ORES, Tag.EMERALD_ORES, Tag.LAPIS_ORES, Tag.REDSTONE_ORES);
        for (int x = start.getBlockX() - 15; x <= start.getBlockX() + 15; x++) {
            for (int y = start.getBlockY() - 15; y <= start.getBlockY() + 15; y++) {
                for (int z = start.getBlockZ() - 15; z <= start.getBlockZ() + 15; z++) {
                    Location location = new Location(world, x, y, z);
                    Material blockType = location.getBlock().getType();
                    if (materialStream.anyMatch(o -> o.isTagged(blockType))) {
                        streamBuilder.add(location.getBlock());
                    }
                }
            }
        }
        return streamBuilder.build();
    }
}
