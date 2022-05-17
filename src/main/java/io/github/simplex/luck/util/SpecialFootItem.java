package io.github.simplex.luck.util;

import io.github.simplex.lib.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpecialFootItem {
    public final ItemStack stack;

    public SpecialFootItem() {
        stack = ItemBuilder.of(Material.RABBIT_FOOT)
                .setName("Enhanced Rabbit Foot")
                .setAmount(1).setLore("A strange energy radiates from within.",
                        "This item will increase your luck multiplier by 0.1.")
                .build();
    }

    public ItemStack get() {
        return stack;
    }

    public ItemMeta meta() {
        return stack.getItemMeta();
    }
}
