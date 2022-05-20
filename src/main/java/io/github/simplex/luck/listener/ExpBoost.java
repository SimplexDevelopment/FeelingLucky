package io.github.simplex.luck.listener;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import io.github.simplex.luck.FeelingLucky;
import io.github.simplex.luck.player.Luck;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public final class ExpBoost extends AbstractListener {
    public ExpBoost(FeelingLucky plugin) {
        super(plugin);
    }

    @EventHandler
    public void boostExperienceGain(PlayerPickupExperienceEvent event) {
        ExperienceOrb orb = event.getExperienceOrb();
        int n = orb.getExperience();
        int math = (5 * n ^ 2) / (2 * n + 4);
        int rounded = Math.round(math);
        Player player = event.getPlayer();
        Luck luck = plugin.getHandler().getLuckContainer(player);
        if (luck.quickRNG(luck.getValue()) && doesQualify("experience", luck.getValue())) {
            orb.setExperience(rounded);
        }
    }
}
