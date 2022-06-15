package io.github.simplex.lib;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;

public class MiniComponent {
    private final String content;
    private TextDecoration decoration = null;
    private TextColor color = null;

    public MiniComponent(String content) {
        this.content = content;
    }

    @Contract("_ -> new")
    public static MiniComponent of(String content) {
        return new MiniComponent(content);
    }

    @Contract("_ -> new")
    public static Component info(String content) {
        return new MiniComponent(content).color(ChatColor.GREEN).send();
    }

    @Contract("_ -> new")
    public static Component warn(String content) {
        return new MiniComponent(content).color(ChatColor.YELLOW).decorate(TextDecoration.ITALIC).send();
    }

    @Contract("_ -> new")
    public static Component err(String content) {
        return new MiniComponent(content).color(ChatColor.RED).decorate(TextDecoration.BOLD).send();
    }

    public MiniComponent decorate(TextDecoration decoration) {
        this.decoration = decoration;
        return this;
    }

    public MiniComponent color(ChatColor color) {
        this.color = TextColor.color(color.asBungee().getColor().getRGB());
        return this;
    }

    public Component send() {
        if (color == null) {
            if (decoration == null) return Component.empty().content(content);

            return Component.empty().content(content).decoration(decoration, TextDecoration.State.TRUE);
        }

        if (decoration == null) return Component.empty().content(content).color(color);

        return Component.empty().content(content).decorate(decoration).color(color);
    }
}
