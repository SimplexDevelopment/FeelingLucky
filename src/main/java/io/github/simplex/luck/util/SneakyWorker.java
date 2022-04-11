package io.github.simplex.luck.util;

import io.github.simplex.luck.player.Luck;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

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

    public static void quietTry(SneakyTry sneakyTry) {
        try {
            sneakyTry.tryThis();
        } catch (Exception ignored) {
        }
    }

    public static void move(Item item) {
        ItemStack stack = item.getItemStack();
        int rng = (Luck.RNG().nextInt(2, 5)) + stack.getAmount();
        stack.setAmount(rng);
        item.setItemStack(stack);
    }

    public interface SneakyTry {
        void tryThis() throws Exception;
    }
}
