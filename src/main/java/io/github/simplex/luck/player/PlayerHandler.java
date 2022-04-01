package io.github.simplex.luck.player;

import io.github.simplex.luck.FeelingLucky;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record PlayerHandler(FeelingLucky plugin) implements Listener {
    private static final Map<Player, Luck> playerLuckMap = new HashMap<>();
    private static final List<Player> markedPlayers = new ArrayList<>();

    public static Luck getLuckContainer(Player player) {
        return playerLuckMap.get(player);
    }

    public static void markPlayer(Player player) {
        markedPlayers.add(player);
    }

    public static void unmarkPlayer(Player player) {
        markedPlayers.remove(player);
    }

    public static boolean isMarked(Player player) {
        return markedPlayers.contains(player);
    }

    @EventHandler
    public void initializePlayer(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        PlayerConfig config = FeelingLucky.getConfigMap().get(player.getUniqueId());

        if (config == null) {
            config = new PlayerConfig(plugin, player);
            FeelingLucky.getConfigMap().put(player.getUniqueId(), config);
        }

        String username = config.getString("username");
        double luck = config.getDouble("luck");
        double multiplier = config.getDouble("multiplier");

        if (!player.getName().equalsIgnoreCase(username)) {
            config.set("username", player.getName());
            config.save();
            config.load();
        }

        Luck container = new Luck(player, multiplier);
        container.setValue(luck);

        playerLuckMap.put(player, container);
    }

    public static void updatePlayer(Player player, Luck luck) {
        playerLuckMap.replace(player, luck);
    }
}
