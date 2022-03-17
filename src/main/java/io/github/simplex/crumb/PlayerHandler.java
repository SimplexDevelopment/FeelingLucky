package io.github.simplex.crumb;

import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerHandler implements Listener {
    private final Map<Player, Luck> playerLuckMap = new HashMap<>();
    private final List<Player> markedPlayers = new ArrayList<>();
    private final Crumb plugin;

    public PlayerHandler(Crumb plugin) {
        this.plugin = plugin;
    }

    public Luck getLuckContainer(Player player) {
        return playerLuckMap.get(player);
    }

    @EventHandler
    public void initializePlayer(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        ConfigurationSection identifier = plugin.config.getConfigurationSection(uuid);
        Luck luck = new Luck(player);
        if (identifier == null) {
            identifier = plugin.config.createSection(player.getUniqueId().toString());
            identifier.set("luck", luck.defaultValue());
            plugin.config.save();
        }
        luck.setValue(identifier.getDouble("luck"));
        playerLuckMap.put(player, luck);
    }

    public void updatePlayer(Player player, Luck luck) {
        playerLuckMap.replace(player, luck);
    }

    public void markPlayer(Player player) {
        markedPlayers.add(player);
    }

    public void unmarkPlayer(Player player) {
        markedPlayers.remove(player);
    }

    public boolean isMarked(Player player) {
        return markedPlayers.contains(player);
    }
}
