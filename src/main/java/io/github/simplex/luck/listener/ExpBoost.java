package io.github.simplex.luck.listener;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public record ExpBoost(FeelingLucky plugin) implements Listener {
    public ExpBoost(FeelingLucky plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void boostExperienceGain(PlayerPickupExperienceEvent event) {
        ExperienceOrb orb = event.getExperienceOrb();
        int n = orb.getExperience();
        int math = (5 * n ^ 2) / (2 * n + 4);
        int rounded = Math.round(math);
        Player player = event.getPlayer();
        Luck luck = plugin.getHandler().getLuckContainer(player);
        if (luck.quickRNG(luck.getPercentage())) {
            orb.setExperience(rounded);
        }
    }
}
