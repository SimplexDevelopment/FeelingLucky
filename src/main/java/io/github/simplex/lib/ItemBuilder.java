package io.github.simplex.lib;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ItemBuilder {
    private final ItemStack stack;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this.stack = new ItemStack(material);
        this.meta = stack.getItemMeta();
    }

    @Contract("_ -> new")
    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material);
    }

    @Contract(pure = true)
    public static boolean isTool(Material m) {
        String name = m.getKey().getKey();

        return m.equals(Material.SHEARS)
                || name.endsWith("bow")
                || name.endsWith("pickaxe")
                || name.endsWith("axe")
                || name.endsWith("sword")
                || name.endsWith("shovel")
                || name.endsWith("hoe");
    }

    public ItemBuilder setName(String name) {
        meta.displayName(MiniComponent.of(name).send());
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        List<Component> components = new ArrayList<>();
        Arrays.stream(lore).forEach(entry -> components.add(MiniComponent.of(entry).send()));
        meta.lore(components);
        return this;
    }

    public ItemStack build() {
        stack.setItemMeta(meta);
        return stack;
    }

    private Component component(String content, TextColor color, TextDecoration decoration) {
        return Component.empty().content(content).decorate(decoration).color(color);
    }

    private Component component(String content, TextColor color) {
        return Component.empty().content(content).color(color);
    }

    private Component component(String content) {
        return Component.empty().content(content);
    }
}
