package io.github.simplex.lib;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Contract;

public class MiniComponent {
    private final String content;
    private TextDecoration decoration = null;
    private TextColor color = null;

    @Contract("_ -> new")
    public static MiniComponent of(String content) {
        return new MiniComponent(content);
    }

    public MiniComponent(String content) {
        this.content = content;
    }

    public MiniComponent decorate(TextDecoration decoration) {
        this.decoration = decoration;
        return this;
    }

    public MiniComponent color(TextColor color) {
        this.color = color;
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
