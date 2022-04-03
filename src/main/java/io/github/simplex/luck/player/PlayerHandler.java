package io.github.simplex.luck.player;

import io.github.simplex.luck.FeelingLucky;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

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
