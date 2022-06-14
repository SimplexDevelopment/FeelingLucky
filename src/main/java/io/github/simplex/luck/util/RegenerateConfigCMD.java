package io.github.simplex.luck.util;

import io.github.simplex.lib.MiniComponent;
import io.github.simplex.luck.FeelingLucky;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RegenerateConfigCMD extends Command implements TabCompleter, PluginIdentifiableCommand {
    private final FeelingLucky plugin;

    public RegenerateConfigCMD(FeelingLucky plugin) {
        super("rgc", "Regenerate this plugin's config file.", "/<command>", List.of());
        this.plugin = plugin;
        setPermission("luck.rgc");
        plugin.getCommandMap().register("rgc", "FeelingLucky", this);
        plugin.getLogger().info("Successfully registered command: RGC.");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(MiniComponent.err("This command can only be used through console access."));
            return true;
        }

        plugin.saveResource("config.yml", true);
        plugin.getConfig().load();
        plugin.getLogger().info("Configuration regenerated.");

        return true;
    }

    @Override
    public @NotNull FeelingLucky getPlugin() {
        return plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
