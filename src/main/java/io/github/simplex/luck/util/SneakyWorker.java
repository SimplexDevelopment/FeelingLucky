package io.github.simplex.luck.util;

import io.github.simplex.luck.listener.AbstractListener;
import io.github.simplex.luck.player.Luck;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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

    public static Class<?>[] getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace(".", "/");
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class<?>[0]);
    }

    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName().substring(0, file.getName().length() - 6)));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + "." + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    public static Item move(Item item) {
        ItemStack stack = item.getItemStack();
        int rng = (Luck.RNG().nextInt(2, 5)) + stack.getAmount();
        stack.setAmount(rng);
        item.setItemStack(stack);
        return item;
    }

    public interface SneakyTry {
        void tryThis() throws Exception;
    }
}
