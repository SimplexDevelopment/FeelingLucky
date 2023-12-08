package io.github.simplex.luck.player;

import io.github.simplex.luck.FeelingLucky;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class PlayerHandler implements Listener {
    public final FeelingLucky plugin;
    private final Map<Player, Luck> playerLuckMap = new HashMap<>();

    public PlayerHandler(FeelingLucky plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public Luck getLuckContainer(Player player) {
        return playerLuckMap.get(player);
    }

    public void updatePlayer(Player player, Luck luck) {
        playerLuckMap.replace(player, luck);
    }

    @EventHandler
    public void initializePlayer(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        PlayerConfig playerConfig = plugin.getConfigMap().get(player.getUniqueId());

        if (playerConfig == null) {
            playerConfig = new PlayerConfig(plugin, player);
            plugin.getConfigMap().put(player.getUniqueId(), playerConfig);
        }

        String username = playerConfig.getUsername();
        double luck = playerConfig.getLuck();
        double multiplier = playerConfig.getMultiplier();
        boolean verbose = playerConfig.isVerbose();

        if (!player.getName().equalsIgnoreCase(username)) {
            playerConfig.getConfig().set("username", player.getName());
            playerConfig.save();
            playerConfig.load();
        }

        Luck container = new Luck(plugin, player, multiplier);
        container.setVerbose(verbose);
        container.setValue(luck);

        playerLuckMap.put(player, container);
    }

    @EventHandler
    public void clearContainer(PlayerQuitEvent event) {
        playerLuckMap.remove(event.getPlayer());
    }
}
