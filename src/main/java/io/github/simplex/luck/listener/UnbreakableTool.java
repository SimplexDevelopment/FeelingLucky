package io.github.simplex.luck.listener;

import io.github.simplex.lib.ItemBuilder;
import io.github.simplex.lib.MiniComponent;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class UnbreakableTool extends AbstractListener {
    public UnbreakableTool(FeelingLucky plugin) {
        super(plugin);
    }

    @EventHandler
    public void unbreakableTool(CraftItemEvent event) {
        CraftingInventory inventory = event.getInventory();
        ItemStack stack = inventory.getResult();

        if (stack == null) return;
        ItemMeta meta = stack.getItemMeta();

        if (ItemBuilder.isTool(stack.getType())) {
            if (event.getWhoClicked() instanceof Player player) {
                Luck luck = getHandler().getLuckContainer(player);
                if (luck.quickRNG(luck.getPercentage()) && doesQualify("unbreakable", luck.getPercentage())) {
                    meta.setUnbreakable(true);
                    stack.setItemMeta(meta);
                    inventory.setResult(stack);
                    player.sendMessage(MiniComponent.info("By the grace of Luck you have crafted an unbreakable tool!"));
                }
            }
        }
    }
}
