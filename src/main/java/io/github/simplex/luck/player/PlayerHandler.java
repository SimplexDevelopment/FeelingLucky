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

    public static void updatePlayer(Player player, Luck luck) {
        playerLuckMap.replace(player, luck);
    }

    @EventHandler
    public void initializePlayer(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        PlayerConfig playerConfig = FeelingLucky.getConfigMap().get(player.getUniqueId());

        if (playerConfig == null) {
            playerConfig = new PlayerConfig(plugin, player);
            FeelingLucky.getConfigMap().put(player.getUniqueId(), playerConfig);
        }

        String username = playerConfig.getConfig().getString("username");
        double luck = playerConfig.getConfig().getDouble("luck");
        double multiplier = playerConfig.getConfig().getDouble("multiplier");

        if (!player.getName().equalsIgnoreCase(username)) {
            playerConfig.getConfig().set("username", player.getName());
            playerConfig.save();
            playerConfig.load();
        }

        Luck container = new Luck(player, multiplier);
        container.setValue(luck);

        playerLuckMap.put(player, container);
    }
}
