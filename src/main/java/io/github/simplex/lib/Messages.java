package io.github.simplex.lib;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum Messages {

    NOT_FROM_CONSOLE(MiniComponent.of("This command may only be used in game.").send()),
    NO_PERMISSION(MiniComponent
            .of("You do not have permission to use this command.")
            .color(TextColor.color(255, 0, 0))
            .decorate(TextDecoration.ITALIC)
            .send());

    private final Component message;

    Messages(Component message) {
        this.message = message;
    }

    public Component get() {
        return message;
    }
}
