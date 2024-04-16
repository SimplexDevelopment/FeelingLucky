package io.github.simplex.luck.util;

import io.github.simplex.lib.Messages;
import io.github.simplex.lib.MiniComponent;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import io.github.simplex.luck.player.PlayerConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LuckCMD extends Command implements TabCompleter, PluginIdentifiableCommand {
    private final FeelingLucky plugin;

    public LuckCMD(FeelingLucky plugin) {
        super("luck", "FeelingLucky main command.", "/<command> <info | set | reset | give | take> [player] [amount]", List.of());
        this.plugin = plugin;
        setPermission("luck.default");
        plugin.getCommandMap().register("luck", "FeelingLucky", this);
        plugin.getLogger().info("Successfully registered command: Luck");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length < 1 || args.length > 3) {
            sender.sendMessage(this.getUsage());
            return false;
        }

        if (args.length == 3) {
            if ((sender instanceof ConsoleCommandSender) || sender.hasPermission("luck.admin")) {
                if (args[0].equalsIgnoreCase("reload") && args[1].equalsIgnoreCase("-p")) {
                    Player player = Bukkit.getPlayer(args[2]);
                    if (player == null) {
                        sender.sendMessage(Messages.NO_PLAYER.get());
                        return true;
                    }
                    UUID uuid = player.getUniqueId();
                    PlayerConfig config = plugin.getConfigMap().get(uuid);
                    config.reload();
                    sender.sendMessage(MiniComponent.info("Successfully reloaded " + player.getName() + "'s configuration file."));
                    return true;
                }

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
                        sender.sendMessage(MiniComponent.info("Successfully set " + args[1] + "'s Luck stat to " + amount + "."));
                        return true;
                    }
                    case "give" -> {
                        luck.addTo(amount);
                        plugin.getHandler().updatePlayer(player, luck);
                        config.setLuck(luck.getValue());
                        sender.sendMessage(MiniComponent.info("Successfully gave " + args[1] + " " + amount + " points of luck!"));
                        return true;
                    }
                    case "take" -> {
                        luck.takeFrom(amount);
                        plugin.getHandler().updatePlayer(player, luck);
                        config.setLuck(luck.getValue());
                        sender.sendMessage(MiniComponent.info("Successfully took " + amount + " points of luck from " + args[1]));
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
                if (args[0].equalsIgnoreCase("reload") && args[1].equalsIgnoreCase("-m")) {
                    plugin.getConfig().reload();
                    sender.sendMessage(MiniComponent.info("Configuration successfully reloaded."));
                    return true;
                }

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

                if (args[0].equalsIgnoreCase("verbose") && sender instanceof Player player) {

                    if (!plugin.getConfig().isVerboseGlobal()) {
                        player.sendMessage(Messages.VERBOSE_DISABLED.get());
                        return true;
                    }

                    final boolean a1 = Boolean.parseBoolean(args[1]);
                    Luck luck = plugin.getHandler().getLuckContainer(player);
                    PlayerConfig config = plugin.getConfigMap().get(player.getUniqueId());
                    luck.setVerbose(a1);
                    plugin.getHandler().updatePlayer(player, luck);
                    config.setVerbose(a1);
                    sender.sendMessage(MiniComponent.info("Verbose mode set to " + a1 + "."));
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
                    player.sendMessage(MiniComponent.info("Your Luck: " + luck.getValue()));
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
        List<String> playerNames = Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        List<String> adminCommands = List.of("set", "reset", "give", "take", "reload");

        if ((sender instanceof ConsoleCommandSender) || sender.hasPermission("luck.admin")) {
            completions.addAll(adminCommands);
            return completions.stream().filter(n -> n.startsWith(args[0])).toList();
        }

        if (adminCommands.contains(args[0])
                && sender.hasPermission("luck.admin")) {
            if (args.length == 2) {
                switch (args[0]) {
                    case "info":
                    case "reset":
                        return playerNames.stream().filter(n -> n.startsWith(args[1])).toList();
                    case "reload":
                        return List.of("-m", "-p");
                }
            }

            if (args.length == 3 && playerNames.contains(args[1])) {
                switch (args[0]) {
                    case "give":
                    case "take":
                    case "set":
                        return List.of("amount");
                }
            }
        }

        if (args[0].equalsIgnoreCase("reload")
                && args[1].equalsIgnoreCase("-p")
                && sender.hasPermission("luck.admin") && (args.length == 3)) {
            return playerNames.stream().filter(n -> n.startsWith(args[2])).toList();
        }

        return completions.stream().filter(n -> n.startsWith(args[0])).toList();
    }

    @Override
    public @NotNull FeelingLucky getPlugin() {
        return plugin;
    }
}
