package io.github.simplex.luck.util;

import io.github.simplex.lib.Messages;
import io.github.simplex.lib.MiniComponent;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import io.github.simplex.luck.player.PlayerConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LuckCMD extends Command implements TabCompleter {
    private final FeelingLucky plugin;

    public LuckCMD(FeelingLucky plugin) {
        super("luck", "FeelingLucky main command.", "/<command> <info | set | reset | give | take> [player] [amount]", List.of());
        this.plugin = plugin;
        setPermission("luck.default");
        plugin.getServer().getCommandMap().register("luck", "FeelingLucky", this);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length < 1 || args.length > 3) return false;

        if (args.length == 3) {
            if ((sender instanceof ConsoleCommandSender) || sender.hasPermission("luck.admin")) {
                Player player = Bukkit.getPlayer(args[1]);
                double amount = Double.parseDouble(args[2]);

                if (player == null) {
                    sender.sendMessage(Messages.NO_PLAYER.get());
                    return true;
                }

                if (amount > 1024.0 || amount < -1024.0) {
                    sender.sendMessage(Messages.OUT_OF_BOUNDS.get());
                    return true;
                }

                Luck luck = plugin.getHandler().getLuckContainer(player);
                PlayerConfig config = plugin.getConfigMap().get(player.getUniqueId());

                switch (args[0]) {
                    case "set" -> {
                        luck.setValue(amount);
                        plugin.getHandler().updatePlayer(player, luck);
                        config.setLuck(luck.getValue());
                        sender.sendMessage(MiniComponent.info("Successfully reset " + args[1] + "'s Luck stat."));
                        return true;
                    }
                    case "give" -> {
                        luck.addTo(amount);
                        plugin.getHandler().updatePlayer(player, luck);
                        config.setLuck(luck.getValue());
                        sender.sendMessage(MiniComponent.info("Successfully reset " + args[1] + "'s Luck stat."));
                        return true;
                    }
                    case "take" -> {
                        luck.takeFrom(amount);
                        plugin.getHandler().updatePlayer(player, luck);
                        config.setLuck(luck.getValue());
                        sender.sendMessage(MiniComponent.info("Successfully reset " + args[1] + "'s Luck stat."));
                        return true;
                    }
                }
            } else {
                sender.sendMessage(Messages.NO_PERMISSION.get());
                return true;
            }
        }

        if (args.length == 2) {
            if ((sender instanceof ConsoleCommandSender) || sender.hasPermission("luck.admin")) {
                if (args[0].equalsIgnoreCase("info")) {
                    Player player = Bukkit.getPlayer(args[1]);

                    if (player == null) {
                        sender.sendMessage(Messages.NO_PLAYER.get());
                        return true;
                    }

                    Luck luck = plugin.getHandler().getLuckContainer(player);
                    sender.sendMessage(MiniComponent.info("Luck stat for " + args[1] + ": " + luck.getValue()));
                    return true;
                }

                if (args[0].equalsIgnoreCase("reset")) {
                    Player player = Bukkit.getPlayer(args[1]);

                    if (player == null) {
                        sender.sendMessage(Messages.NO_PLAYER.get());
                        return true;
                    }

                    Luck luck = plugin.getHandler().getLuckContainer(player);
                    PlayerConfig config = plugin.getConfigMap().get(player.getUniqueId());
                    luck.reset();
                    plugin.getHandler().updatePlayer(player, luck);
                    config.setLuck(luck.getValue());
                    sender.sendMessage(MiniComponent.info("Successfully reset " + args[1] + "'s Luck stat."));
                    return true;
                }
            } else {
                sender.sendMessage(Messages.NO_PERMISSION.get());
                return true;
            }
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("luck.admin")) {
                plugin.getConfigMap().values().forEach(PlayerConfig::reload);
                sender.sendMessage(MiniComponent.info("Player configurations reloaded."));
                return true;
            }

            if ((sender instanceof Player player) && player.hasPermission("luck.default")) {
                if (args[0].equalsIgnoreCase("info")) {
                    Luck luck = plugin.getHandler().getLuckContainer(player);
                    player.sendMessage(MiniComponent.info("Your Luck: " + luck.getPercentage()));
                    return true;
                }
            } else if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage(Messages.NOT_FROM_CONSOLE.get());
                return true;
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>() {{
            add("info");
        }};
        List<String> playerNames = new ArrayList<>() {{
            Bukkit.getOnlinePlayers().forEach(p -> add(p.getName()));
        }};
        List<String> adminCommands = List.of("set", "reset", "give", "take");

        if ((sender instanceof ConsoleCommandSender) || sender.hasPermission("luck.admin")) {
            completions.addAll(adminCommands);
            return completions;
        }

        if (args[0].equalsIgnoreCase("info") && sender.hasPermission("luck.admin")) {
            return playerNames;
        }

        if (completions.contains(args[1]) && sender.hasPermission("luck.admin")) {
            switch (args[0]) {
                case "info":
                case "reset":
                case "reload":
                    return new ArrayList<>();
                case "give":
                case "take":
                case "set":
                    return List.of("amount");
            }
        }

        return completions;
    }
}
