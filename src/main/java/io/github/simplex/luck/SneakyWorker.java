package io.github.simplex.luck;

import org.bukkit.Bukkit;

public class SneakyWorker {
    public static void sneakyTry(SneakyTry sneakyTry) {
        try {
            sneakyTry.tryThis();
        } catch (Exception ex) {
            String sb = "An error of type: "
                    + ex.getClass().getSimpleName()
                    + " has occurred. A cause will be printed. \n\n"
                    + ex.getCause();
            Bukkit.getLogger().severe(sb);
        }
    }

    public interface SneakyTry {
        void tryThis() throws Exception;
    }
}
