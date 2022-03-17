package io.github.simplex.crumb;

import org.bukkit.Bukkit;

public class SneakyWorker {
    public static void sneakyTry(SneakyTry sneakyTry) {
        try {
            sneakyTry.tryThis();
        } catch (Exception ex) {
            Bukkit.getLogger().severe(ex.getMessage());
        }
    }

    interface SneakyTry {
        void tryThis() throws Exception;
    }
}
