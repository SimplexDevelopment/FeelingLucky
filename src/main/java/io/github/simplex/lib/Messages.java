package io.github.simplex.lib;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Messages {

    NOT_FROM_CONSOLE(builder("This command may only be used in game.", null, null)),
    NO_PERMISSION(builder("You do not have permission to use this command.", TextColor.color(255, 0, 0), TextDecoration.ITALIC));

    private final Component message;

    Messages(Component message) {
        this.message = message;
    }

    public Component get() {
        return message;
    }

    private static Component builder(@NotNull String message, @Nullable TextColor color, @Nullable TextDecoration decoration) {
        if (color == null) {
            if (decoration == null) return Component.empty().content(message);

            return Component.empty().content(message).decoration(decoration, TextDecoration.State.TRUE);
        }

        if (decoration == null) return Component.empty().content(message).color(color);

        return Component.empty().content(message).color(color).decoration(decoration, TextDecoration.State.TRUE);
    }
}
