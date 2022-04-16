package io.github.simplex.luck.listener;

import io.github.simplex.lib.ItemBuilder;
import io.github.simplex.lib.MiniComponent;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public record UnbreakableTool(FeelingLucky plugin) implements Listener {
    public UnbreakableTool {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void unbreakableTool(CraftItemEvent event) {
        CraftingInventory inventory = event.getInventory();
        ItemStack stack = inventory.getResult();

        if (stack == null) return;
        ItemMeta meta = stack.getItemMeta();

        if (ItemBuilder.isTool(stack.getType())) {
            if (event.getWhoClicked() instanceof Player player) {
                Luck luck = plugin.getHandler().getLuckContainer(player);
                if (luck.quickRNG(luck.getPercentage())) {
                    meta.setUnbreakable(true);
                    stack.setItemMeta(meta);
                    inventory.setResult(stack);
                    player.sendMessage(MiniComponent.info("By the grace of Luck you have crafted an unbreakable tool!"));
                }
            }
        }
    }
}
