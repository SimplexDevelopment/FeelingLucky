package io.github.simplex.lib;

import net.kyori.adventure.text.Component;

public enum Messages {

    NOT_FROM_CONSOLE(MiniComponent.err("This command may only be used in game.")),
    NO_PERMISSION(MiniComponent.err("You do not have permission to use this command.")),
    NO_PLAYER(MiniComponent.warn("That player cannot be found.")),
    OUT_OF_BOUNDS(MiniComponent.err("Number must be between -1024.0 and 1024.0"));

    private final Component message;

    Messages(Component message) {
        this.message = message;
    }

    public Component get() {
        return message;
    }
}
